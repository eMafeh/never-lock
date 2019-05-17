package common.util;

import java.util.function.Supplier;

public class ExceptionUtil {
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
}
