package dto;

import lombok.Data;
import nio.core.User;

import java.io.Serializable;

@Data
public class UserDto implements Serializable {
    private static final long serialVersionUID = 1L;
    private final String host;
    private final int mainPort;
    public final String name;
    public final int status;

    public UserDto(User user) {
        this.host = user.host;
        this.mainPort = user.mainPort;
        this.name = user.getName();
        this.status = user.getStatus();
    }

    public User getUser() {
        return User.getUser(host, mainPort);
    }
}
