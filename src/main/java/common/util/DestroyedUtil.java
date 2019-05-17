package common.util;

import java.util.ArrayList;
import java.util.List;

public class DestroyedUtil {
    private static final List<Runnable> RUNNABLE_LIST = new ArrayList<>();

    static {
        Runtime.getRuntime()
                .addShutdownHook(ThreadUtil.createThread(() -> {
                    System.out.println("process try over");
                    RUNNABLE_LIST.forEach(Runnable::run);
                    System.out.println("process over");
                }, "destroyed"));
    }

    public static void addListener(Runnable runnable) {
        RUNNABLE_LIST.add(runnable);
    }
}
