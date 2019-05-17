package nio;

import common.util.ExceptionUtil;
import nio.message.SendMessage;

import java.io.IOException;

public class ChannelLocal extends BaseChannelProxy {
    public ChannelLocal(final ServerBoot boot) {
        super(boot, null, boot.self);
    }

    @Override
    void send(final SendMessage sendMessage) throws IOException {
        ExceptionUtil.isTrue(user.equals(sendMessage.user));
        serverBoot.service(this, sendMessage.toGetMessage());
    }
}
