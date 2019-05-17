package nio;

import dto.UserDto;
import sun.reflect.generics.reflectiveObjects.ParameterizedTypeImpl;

import java.io.Serializable;
import java.nio.ByteBuffer;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public abstract class RpcService<T> {
    private static final Map<Byte, RpcService> ALL_SERVICE = new ConcurrentHashMap<>();
    private static final Map<Class<? extends RpcService>, Integer> ALL_ORDER = new ConcurrentHashMap<>();
    private final int order;
    private final Class<? extends T> supportType = (Class) ((ParameterizedTypeImpl) getClass().getGenericSuperclass()).getActualTypeArguments()[0];

    public RpcService(int order) {
        this.order = order;
        ALL_ORDER.put(this.getClass(), order);
        RpcService put = ALL_SERVICE.put((byte) order, this);
        if (put != null) {
            throw new IllegalArgumentException(put.getClass() + " " + put.order + "/" + getClass() + " " + order + " has same byte value");
        }
    }

    public abstract void service(T t, UserDto sender);

    public boolean support(Object obj) {
        return obj == null || supportType.isAssignableFrom(obj.getClass());
    }

    public static ByteBuffer getHead(int length, int order) {
        byte[] prefix = new byte[5];

        prefix[0] = (byte) ((length & 0xff) - 128);
        prefix[1] = (byte) ((length >> 8 & 0xff) - 128);
        prefix[2] = (byte) ((length >> 16 & 0xff) - 128);
        prefix[3] = (byte) ((length >> 24 & 0xff) - 128);
        prefix[4] = (byte) order;
        return ByteBuffer.wrap(prefix);
    }

    static int getLength(byte[] prefix) {
        return (prefix[3] + 128) << 24 |

                (prefix[2] + 128) << 16 |

                (prefix[1] + 128) << 8 |

                (prefix[0] + 128);
    }

    public static RpcService get(int i) {
        return ALL_SERVICE.get((byte) i);
    }

    public static <T extends Serializable> int get(Class<? extends RpcService<? super T>> rpcService) {
        return ALL_ORDER.get(rpcService);
    }
}
