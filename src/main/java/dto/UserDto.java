package dto;

import nio.core.User;

import java.io.Serializable;

/**
 * @author 88382571
 * 2019/5/16
 */
public class UserDto implements Serializable {
    private static final long serialVersionUID = 1;
    private final String host;
    private final int mainPort;

    public final String name;
    public final int status;
    public final boolean windows;

    public UserDto(User user) {
        this.host = user.host;
        this.mainPort = user.mainPort;
        this.name = user.getName();
        this.status = user.getStatus();
        this.windows = user.windows;
    }

    public User getUser() {
        return User.getUser(host, mainPort, windows);
    }

    @Override
    public String toString() {
        return "UserDto{" + "host='" + host + '\'' + ", name='" + name + '\'' + '}';
    }
}
