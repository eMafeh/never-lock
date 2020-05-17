package common.util;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * @author 88382571
 * 2019/5/8
 */
public class ExceptionUtil {
    public static Consumer<String> consumer = System.out::println;

    public static void isTrue(boolean b) {
        if (!b) {
            throw new RuntimeException();
        }
    }

    public static void isTrue(boolean b, Supplier<String> msg) {
        if (!b) {
            throw new RuntimeException(msg.get());
        }
    }

    @SuppressWarnings("all")
    public static <T extends Throwable, E> E throwT(Throwable t) throws T {
        throw (T) t;
    }

    public static void print(Exception e) {
        if (consumer != null && e != null) {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            e.printStackTrace(new PrintStream(out));
            consumer.accept(out.toString());
        }
    }
}
