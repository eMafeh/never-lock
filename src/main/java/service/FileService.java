package service;

import common.util.ExceptionUtil;
import nio.ChannelHandler;
import nio.RpcService;
import nio.core.User;
import nio.message.MessageDto;
import nio.message.RemoteServicePackage;

import java.io.File;
import java.io.IOException;
import java.nio.channels.SocketChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static java.nio.file.StandardOpenOption.CREATE;

public class FileService extends RpcService<MessageDto> {
    @Override
    public void service(MessageDto dto, SocketChannel channel, byte[] data) {
        String s = dto.msg;
        User send = dto.sender.getUser();
        Path path = Paths.get("Downloads" + File.separator + s);
        File file = path.toFile();
        if (!file.exists() && data.length == 0) {
            file.mkdirs();
        }
        if (data.length != 0 && !file.isDirectory()) {
            try {
                File parentFile = file.getParentFile();
                if (parentFile != null) {
                    parentFile.mkdirs();
                }
                Files.write(path, data, CREATE);
                send.newFile(file);
            } catch (IOException e) {
                ExceptionUtil.print(e);
            }
        }
    }

    public static void send(User user, File file) {
        if (file.isDirectory()) {
            String prefix = file.getParent();
            List<File> all = all(file);
            for (File entry : all) {
                try {
                    send(user, entry.getAbsolutePath()
                                    .substring(prefix.length() + 1),
                            entry.isFile() ? Files.readAllBytes(entry.toPath()) : new byte[0]);
                } catch (IOException e) {
                    ExceptionUtil.print(e);
                }
            }
        } else {
            try {
                byte[] data = Files.readAllBytes(file.toPath());
                send(user, file.getName(), data);
            } catch (IOException e) {
                ExceptionUtil.print(e);
            }
        }
    }

    private static final int min = 512 * 1024;
    private static final int max = 1024 * 1024;

    private static void send(User user, String name, byte[] data) {
//        int l = Math.min(Math.max(data.length / 100, FileService.min), max);
        int l = data.length;
        for (int i = 0; i < data.length; i += l) {
            ChannelHandler.send(new RemoteServicePackage<>(user,
                    FileService.class,
                    new MessageDto(name),
                    Arrays.copyOfRange(data, i, Math.min(data.length, i + l))));
        }
    }

    private static List<File> all(File file) {
        List<File> map = new ArrayList<>();
        map.add(file);
        if (file.isDirectory()) {
            File[] files = file.listFiles();
            if (files != null) {
                for (File f : files) {
                    map.addAll(all(f));
                }
            }
        }
        return map;
    }
}
