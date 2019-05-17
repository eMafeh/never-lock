package nio.core;

import nio.RpcService;

import java.io.Serializable;

@FunctionalInterface
public interface SendMessageListener {
    <T extends Serializable> void call(Class<? extends RpcService<? super T>> rpcService, T obj, int again);
}
