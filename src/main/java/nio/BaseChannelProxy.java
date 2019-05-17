package nio;

import common.util.ExceptionUtil;
import nio.core.User;
import nio.message.SendMessage;

import java.io.IOException;
import java.nio.channels.SocketChannel;

public class BaseChannelProxy {
    final ServerBoot serverBoot;
    final SocketChannel channel;
    final MessageReorganise reorganise;
    public final User user;

    public BaseChannelProxy(final ServerBoot serverBoot, final SocketChannel channel, final User user) {
        this.serverBoot = serverBoot;
        this.channel = channel;
        this.user = user;
        this.reorganise = new MessageReorganise(this);
    }

    void send(SendMessage sendMessage) throws IOException {
        ExceptionUtil.isTrue(user.equals(sendMessage.user));
        channel.write(sendMessage.toByteBuffer());
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "{" + user + "}";
    }
}
