package service;

import dto.UserDto;
import nio.core.User;
import nio.RpcService;

import java.nio.channels.SocketChannel;

/**
 * @author kelaite
 */
public class UpdateUser extends RpcService<UserDto> {

    @Override
    public void service(UserDto userDto, SocketChannel channel, byte[] data) {
        User user = userDto.getUser()
                .setName(userDto.name)
                .setStatus(userDto.status);
        System.out.println("update " + user);
    }
}