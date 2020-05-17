package service;

import dto.UserDto;
import nio.ChannelHandler;
import nio.core.User;
import nio.message.RemoteServicePackage;
import nio.RpcService;

import java.nio.channels.SocketChannel;

public class GetUsersInfo extends RpcService<UserDto> {
    @Override
    public void service(UserDto obj, SocketChannel channel, byte[] data) {
        User sender = obj.getUser();
        //新用户获取集群信息
        User.getAll()
                .filter(user -> user != sender)
                .filter(user -> user.getStatus() != -1)
                .forEach(user -> ChannelHandler.send(channel,
                        new RemoteServicePackage<>(sender, UpdateUser.class, new UserDto(user))));
    }
}
