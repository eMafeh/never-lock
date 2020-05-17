package nio;

import java.io.Serializable;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

/**
 * @author 88382571
 * 2019/5/8
 */
public abstract class RpcService<T extends Serializable> {
    public abstract void service(T obj, SocketChannel channel, byte[] data);

    public static int getLength(byte[] prefix) {
        return (prefix[3] + 128) << 24 |

                (prefix[2] + 128) << 16 |

                (prefix[1] + 128) << 8 |

                (prefix[0] + 128);
    }

    public static ByteBuffer getLength(int length) {
        byte[] prefix = new byte[4];

        prefix[0] = (byte) ((length & 0xff) - 128);
        prefix[1] = (byte) ((length >> 8 & 0xff) - 128);
        prefix[2] = (byte) ((length >> 16 & 0xff) - 128);
        prefix[3] = (byte) ((length >> 24 & 0xff) - 128);
        return ByteBuffer.wrap(prefix);
    }
}
