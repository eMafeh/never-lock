package service;

import dto.UserDto;
import nio.RpcService;

public class HelloService extends RpcService<String> {
    public HelloService() {
        super(5);
    }

    @Override
    public void service(final String s, final UserDto sender) {
        System.out.println(sender + " " + s);
    }
}
