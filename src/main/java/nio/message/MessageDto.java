package nio.message;

import dto.UserDto;
import nio.core.User;

import java.io.Serializable;

public class MessageDto implements Serializable {
    private static final long serialVersionUID = 1;
    public final UserDto sender = new UserDto(User.SELF);
    public final long begin = System.currentTimeMillis();
    public final String msg;

    public MessageDto(String msg) {
        this.msg = msg;
    }
}
