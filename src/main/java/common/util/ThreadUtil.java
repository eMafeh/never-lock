package common.util;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

public class ThreadUtil {
    private static final ThreadGroup GROUP = System.getSecurityManager() == null ? Thread.currentThread()
            .getThreadGroup() : System.getSecurityManager()
            .getThreadGroup();

    public static Thread createThread(Runnable runnable, String name) {
        return new Thread(GROUP, () -> {
            System.out.println(Thread.currentThread()
                    .getName() + " is start");
            runnable.run();
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
        return new ThreadPoolExecutor(corePoolSize, maximumPoolSize, 0L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<>(10), new ThreadFactory() {
            AtomicLong atomicLong = new AtomicLong();

            @Override
            public Thread newThread(final Runnable r) {
                return createThread(r, prefix + atomicLong.incrementAndGet());
            }
        }, new ThreadPoolExecutor.AbortPolicy());
    }
}
