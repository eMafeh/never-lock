package nio.message;

import common.util.SerializableUtil;
import nio.RpcService;
import nio.core.User;
import service.UpdateUser;

import java.io.Serializable;
import java.nio.ByteBuffer;

public class SendMessage {
    public final User user;
    private final byte[] bytes;
    private final int i;
    public int again;
    public int sleepTime;
    public long lastTime;

    public <T extends Serializable> SendMessage(User user, Class<? extends RpcService<? super T>> rpcService, T obj, int again) {
        this.user = user;
        this.bytes = SerializableUtil.serializ(obj);
        this.i = RpcService.get(rpcService);
        this.again = again;
    }

    private SendMessage(final User user, final byte[] bytes, final int i, final int again) {
        this.user = user;
        this.bytes = bytes;
        this.i = i;
        this.again = again;
    }

    public GetMessage toGetMessage() {
        return new GetMessage(user, i, bytes);
    }

    public ByteBuffer[] toByteBuffer() {
        return new ByteBuffer[]{RpcService.getHead(bytes.length, i), ByteBuffer.wrap(bytes)};
    }

    public boolean isUpdateUser() {
        return RpcService.get(i) instanceof UpdateUser;
    }

    public static class Prepare<T extends Serializable> {
        private final byte[] bytes;

        public Prepare(T obj) {
            this.bytes = SerializableUtil.serializ(obj);
        }

        public SendMessage toSendMessage(User user, Class<? extends RpcService<? super T>> rpcService, int again) {
            return new SendMessage(user, bytes, RpcService.get(rpcService), again);
        }
    }
}
