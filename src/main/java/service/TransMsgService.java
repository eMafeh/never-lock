package service;

import dto.ProxyMsg;
import dto.TransMsg;
import dto.UserDto;
import nio.RpcService;

public class TransMsgService extends RpcService<TransMsg> {
    public TransMsgService() {
        super(3);
    }

    @Override
    public void service(final TransMsg transMsg, final UserDto sender) {
        UserDto getter = transMsg.getter;
        getter.getUser()
                .sendMsg(ProxyMsgService.class, new ProxyMsg(sender, transMsg.msg, System.currentTimeMillis()), Integer.MAX_VALUE);
    }
}
