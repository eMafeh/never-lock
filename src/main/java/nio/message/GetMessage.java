package nio.message;

import common.util.ExceptionUtil;
import common.util.SerializableUtil;
import dto.UserDto;
import lombok.ToString;
import nio.RpcService;
import nio.core.User;

import java.io.Serializable;

@ToString
public class GetMessage<T extends Serializable> {
    private final UserDto sender;
    public final RpcService<T> rpcService;
    private final T obj;

    public GetMessage(final User sender, int i, byte[] bytes) {
        this.obj = SerializableUtil.deSerializ(bytes);
        this.rpcService = RpcService.get(i);
        this.sender = new UserDto(sender);
        ExceptionUtil.isTrue(rpcService != null && rpcService.support(obj), () -> "order num " + i + " not support service");
    }

    public T getObj() {
        return obj;
    }

    public void doService() {
        rpcService.service(obj, sender);
    }
}
