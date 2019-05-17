package service;

import dto.UserDto;
import nio.RpcService;
import nio.core.User;

import java.util.ArrayList;
import java.util.List;

public class UpdateUser extends RpcService<Object> {
    public UpdateUser() {
        super(2);
    }

    @Override
    public void service(final Object o, final UserDto sender) {
        if (o instanceof List) {
            for (Object obj : (List) o) {
                updateUser(obj);
            }
        } else {
            updateUser(o);
        }
    }

    private void updateUser(final Object obj) {
        if (obj instanceof UserDto) {
            UserDto userDto = (UserDto) obj;
            User user = userDto.getUser()
                    .setName(userDto.getName())
                    .setStatus(userDto.getStatus());
            System.out.println(" update " + user);
        } else {
            System.out.println(obj == null ? null : obj.getClass());
        }
    }

    @Override
    public boolean support(final Object obj) {
        if (obj == null) return true;
        Class<?> aClass = obj.getClass();
        return aClass == UserDto.class || aClass == ArrayList.class;
    }
}
