package ktb.util;

import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;

public class ThreadPoolUtil {
    public static ThreadPoolExecutor createPool(int corePoolSize, int maxPoolSize, int queueCapacity, RejectedExecutionHandler rejectPolicy) {
        BlockingQueue<Runnable> waitQueue = new LinkedBlockingQueue <>(queueCapacity);

        if (rejectPolicy == null) {
            rejectPolicy = new ThreadPoolExecutor.CallerRunsPolicy();
        }

        ThreadPoolExecutor executor = new ThreadPoolExecutor(
                corePoolSize,
                maxPoolSize,
                60,
                TimeUnit.SECONDS,
                waitQueue,
                rejectPolicy
        );

        executor.prestartAllCoreThreads();
        return executor;
    }

    public static ThreadPoolExecutor createPool(int corePoolSize, int maxPoolSize, int queueCapacity, boolean isDaemon
            , String threadNamePrefix, RejectedExecutionHandler rejectPolicy) {
        ThreadFactory factory = r -> {
            Thread t = new Thread(r);
            t.setDaemon(isDaemon);
            t.setName(threadNamePrefix + t.getId());
            return t;
        };

        BlockingQueue<Runnable> waitQueue = new LinkedBlockingQueue <>(queueCapacity);

        if (rejectPolicy == null) {
            rejectPolicy = new ThreadPoolExecutor.CallerRunsPolicy();
        }

        ThreadPoolExecutor executor = new ThreadPoolExecutor(
                corePoolSize,
                maxPoolSize,
                60,
                TimeUnit.SECONDS,
                waitQueue,
                factory,
                rejectPolicy
        );

        executor.prestartAllCoreThreads();
        return executor;
    }
}
