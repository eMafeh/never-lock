package nio.message;

import dto.UserDto;
import nio.RpcService;
import nio.core.User;

import java.io.Serializable;

public class Head<T extends Serializable> implements Serializable {
    private static final long serialVersionUID = 1;

    public final UserDto getter;
    public final Class<? extends RpcService<? super T>> rpcService;
    public final T obj;

    Head(User getter, Class<? extends RpcService<? super T>> rpcService, T obj) {
        this.getter = getter == null ? null : new UserDto(getter);
        this.rpcService = rpcService;
        this.obj = obj;
    }
}
