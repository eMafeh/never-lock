package service;

import common.util.DateUtil;
import dto.ProxyMsg;
import dto.UserDto;
import nio.RpcService;

public class ProxyMsgService extends RpcService<ProxyMsg> {
    public ProxyMsgService() {
        super(4);
    }

    @Override
    public void service(final ProxyMsg proxyMsg, final UserDto sender) {
        proxyMsg.sender.getUser()
                .newMsg(DateUtil.format("MM-dd hh:mm:ss", proxyMsg.time) + " : " + proxyMsg.msg);
    }
}
