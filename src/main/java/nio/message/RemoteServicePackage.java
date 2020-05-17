package nio.message;

import common.util.SerializableUtil;
import nio.RpcService;
import nio.core.User;

import java.io.Serializable;
import java.nio.ByteBuffer;

public class RemoteServicePackage<T extends Serializable> {
    public final Head<T> head;
    public final byte[] data;
    private byte[] byteHead;

    public synchronized ByteBuffer[] byteBuffers() {
        if (byteHead == null) {
            byteHead = SerializableUtil.serializable(head);
        }
        return new ByteBuffer[]{
                //总长度
                RpcService.getLength(4 + byteHead.length + data.length),
                //头部描述
                RpcService.getLength(byteHead.length),
                //头部数据
                ByteBuffer.wrap(byteHead),
                //二进制数据
                ByteBuffer.wrap(data)
        };
    }

    private static final byte[] EMPTY = new byte[0];

    public RemoteServicePackage(User getter, Class<? extends RpcService<? super T>> rpcService, T obj) {
        this.head = new Head<>(getter, rpcService, obj);
        this.data = EMPTY;
    }

    public RemoteServicePackage(User getter, Class<? extends RpcService<? super T>> rpcService, T obj, byte[] data) {
        this.head = new Head<>(getter, rpcService, obj);
        this.data = data == null ? EMPTY : data;
    }

    public RemoteServicePackage(Head<T> head, byte[] data) {
        this.head = head;
        this.data = data == null ? EMPTY : data;
    }

    public RemoteServicePackage newGetter(User u) {
        return new RemoteServicePackage<>(u, head.rpcService, head.obj);
    }
}
