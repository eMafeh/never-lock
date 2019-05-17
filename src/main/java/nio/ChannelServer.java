package nio;

import nio.core.User;
import nio.message.SendMessage;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SocketChannel;

public class ChannelServer extends BaseChannelProxy {
    private final String host;

    public ChannelServer(final ServerBoot serverBoot, final SocketChannel channel, final String host) {
        super(serverBoot, channel, null);
        this.host = host;
    }

    static ChannelServer handleServer(ServerBoot boot, SocketChannel channel) throws IOException {
        InetSocketAddress remoteAddress = (InetSocketAddress) channel.getRemoteAddress();
        return new ChannelServer(boot, channel, remoteAddress.getHostString());
    }

    @Override
    void send(final SendMessage sendMessage) throws IOException {
        throw new RuntimeException("ChannelServer can not send msg");
    }

    ChannelJoin toJoin(int mainPort) {
        return new ChannelJoin(serverBoot, channel, User.getUser(host, mainPort));
    }

    static class ChannelJoin extends BaseChannelProxy {

        public ChannelJoin(final ServerBoot serverBoot, final SocketChannel channel, final User user) {
            super(serverBoot, channel, user);
        }
    }
}
