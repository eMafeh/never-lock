package common.util;

import java.io.*;

public class SerializableUtil {
    public static byte[] serializ(Serializable obj) {
        try (ByteArrayOutputStream o1 = new ByteArrayOutputStream(256); ObjectOutputStream o2 = new ObjectOutputStream(o1)) {
            o2.writeObject(obj);
            o2.flush();
            return o1.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static <T extends Serializable> T deSerializ(byte[] bytes) {
        try (InputStream o1 = new ByteArrayInputStream(bytes); ObjectInputStream o2 = new ObjectInputStream(o1)) {
            Object o = o2.readObject();
            @SuppressWarnings("all") T obj = (T) o;
            return obj;
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
}
