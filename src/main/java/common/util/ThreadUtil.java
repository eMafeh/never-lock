package common.util;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @author 88382571
 * 2019/4/25
 */
public class ThreadUtil {
    private static final ThreadGroup GROUP = System.getSecurityManager() == null ? Thread.currentThread()
            .getThreadGroup() : System.getSecurityManager()
            .getThreadGroup();

    public static Thread createThread(Runnable runnable, String name) {
        return new Thread(GROUP, () -> {
            System.out.println(Thread.currentThread()
                    .getName() + " is start");
            try {
                runnable.run();
            } finally {
                System.out.println(Thread.currentThread()
                        .getName() + " is end");
            }
        }, name, 0);
    }

    public static Thread createLoopThread(Runnable runnable, String name) {
        return createThread(() -> {
            while (true) {
                runnable.run();
            }
        }, name);
    }

    public static ThreadPoolExecutor createPool(int corePoolSize, int maximumPoolSize, String prefix) {
        return new ThreadPoolExecutor(corePoolSize, maximumPoolSize, 0L, TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<>(1000), new ThreadFactory() {
            AtomicLong aLong = new AtomicLong();

            @Override
            public Thread newThread(Runnable r) {
                return createThread(r, prefix + aLong.incrementAndGet());
            }
        }, new ThreadPoolExecutor.CallerRunsPolicy());
    }
}
