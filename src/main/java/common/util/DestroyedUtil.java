package common.util;

import java.util.ArrayList;
import java.util.List;

/**
 * @author 88382571
 * 2019/4/25
 */
public class DestroyedUtil {
    private static final List<Runnable> RUNNABLE_LIST = new ArrayList<>();

    static {
        Runtime.getRuntime()
                .addShutdownHook(ThreadUtil.createThread(() -> RUNNABLE_LIST.forEach(Runnable::run), "thread-destroyed-task"));
    }

    public static void addListener(Runnable runnable) {
        RUNNABLE_LIST.add(runnable);
    }
}
