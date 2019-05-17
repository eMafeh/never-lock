package nio;

import nio.core.User;
import nio.message.SendMessage;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SocketChannel;

public class ChannelClient extends BaseChannelProxy {
    private static final int WAIT_CONNECT_TIMES = 10;

    public ChannelClient(final ServerBoot serverBoot, final SocketChannel channel, final User user) {
        super(serverBoot, channel, user);
    }


    public static BaseChannelProxy newChannelClient(final ServerBoot boot, final User user) {
        try {
            SocketChannel channel = SocketChannel.open();
            channel.configureBlocking(false);
            channel.connect(new InetSocketAddress(user.host, user.mainPort));
            if (channel.isConnectionPending()) {
                for (int i = 0; i < WAIT_CONNECT_TIMES; i++) {
                    if (channel.finishConnect()) {
                        {
                            ChannelClient channelClient = new ChannelClient(boot, channel, user);
                            boot.register(channelClient);
                            //逻辑加入
                            channelClient.send(new SendMessage(user, ServerBoot.JoinService.class, boot.self.mainPort, -1));
                            Thread.sleep(1000L);
                            return channelClient;
                        }
                    }
                }
            }
            channel.close();
            System.out.println("connect fail " + user);
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
        return null;
    }
}
