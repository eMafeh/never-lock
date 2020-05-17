package common.util;

import java.io.*;

/**
 * @author 88382571
 * 2019/5/8
 */
public class SerializableUtil {
    public static byte[] serializable(Serializable obj) {
        try (ByteArrayOutputStream o1 = new ByteArrayOutputStream(256); ObjectOutputStream o2 = new ObjectOutputStream(
                o1)) {
            o2.writeObject(obj);
            o2.flush();
            return o1.toByteArray();
        } catch (IOException e) {
            return ExceptionUtil.throwT(e);
        }
    }

    public static <T extends Serializable> T deSerializable(byte[] bytes) {
        return deSerializable(bytes, 0, bytes.length);
    }

    public static <T extends Serializable> T deSerializable(byte[] bytes, int offset, int length) {
        try {
            try (InputStream byteInputStream = new ByteArrayInputStream(
                    bytes, offset, length); ObjectInputStream stream = new ObjectInputStream(byteInputStream)) {
                Object o = stream.readObject();
                @SuppressWarnings({"all"}) T object = (T) o;
                return object;
            }
        } catch (IOException | ClassNotFoundException e) {
            return ExceptionUtil.throwT(e);
        }
    }
}
