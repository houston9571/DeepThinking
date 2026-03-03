package com.deepthinking.common.thread;

import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.concurrent.*;

/**
 * 线程相关工具类.
 */
@Slf4j
public class Threads {

    private static ThreadPoolExecutor pool;

    private final static int corePoolSize = 64;
    private final static int maximumPoolSize = 500;
    private final static int keepAliveTime = 60;

    private static void initThreadPool() {
        if (pool == null) {
            if (corePoolSize > 10000)
                pool = new ThreadPoolExecutor(corePoolSize, maximumPoolSize, keepAliveTime, TimeUnit.SECONDS, new LinkedBlockingDeque<>(corePoolSize), new NamedThreadFactory());
            else
                pool = new ThreadPoolExecutor(corePoolSize, maximumPoolSize, keepAliveTime, TimeUnit.SECONDS, new ArrayBlockingQueue<>(corePoolSize), new NamedThreadFactory());
            log.info("-----> initThreadPool corePoolSize:{} maximumPoolSize:{} keepAliveTime:{}s ", corePoolSize, maximumPoolSize, keepAliveTime);
        }
    }


    public static void asyncExecute(Runnable... runnable) {
        initThreadPool();
        for (Runnable rr : runnable)
            pool.submit(rr);
    }

    public static <V> List<V> asyncExecute(List<Callable<V>> callable) {
        initThreadPool();
        List<V> ll = Lists.newArrayList();
        try {
            List<Future<V>> list = pool.invokeAll(callable);
            for (Future<V> ff : list) {
                ll.add(ff.get());
            }
        } catch (InterruptedException | ExecutionException e) {
            log.error("asyncExecute error:", e);
        }
        return ll;
    }

    /**
     * sleep等待,单位为毫秒,忽略InterruptedException.
     */
    public static void sleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            log.error("sleep error:", e);
        }
    }

    /**
     * sleep等待,忽略InterruptedException.
     */
    public static void sleep(long duration, TimeUnit unit) {
        try {
            Thread.sleep(unit.toMillis(duration));
        } catch (InterruptedException e) {
            log.error("sleep error:", e);
        }
    }

    /**
     * 按照ExecutorService JavaDoc示例代码编写的Graceful Shutdown方法.
     * 先使用shutdown, 停止接收新任务并尝试完成所有已存在任务.
     * 如果超时, 则调用shutdownNow, 取消在workQueue中Pending的任务,并中断所有阻塞函数.
     * 如果仍人超時，則強制退出.
     * 另对在shutdown时线程本身被调用中断做了处理.
     */
    public static void gracefulShutdown(ExecutorService pool, int shutdownTimeout, int shutdownNowTimeout,
                                        TimeUnit timeUnit) {
        pool.shutdown(); // Disable new tasks from being submitted
        try {
            // Wait a while for existing tasks to terminate
            if (!pool.awaitTermination(shutdownTimeout, timeUnit)) {
                pool.shutdownNow(); // Cancel currently executing tasks
                // Wait a while for tasks to respond to being cancelled
                if (!pool.awaitTermination(shutdownNowTimeout, timeUnit)) {
                    log.error("Pool did not terminated");
                }
            }
        } catch (InterruptedException e) {
            // (Re-)Cancel if current thread also interrupted
            pool.shutdownNow();
            // Preserve interrupt status
            Thread.currentThread().interrupt();
            log.error("gracefulShutdown error:", e);
        }
    }

    /**
     * 直接调用shutdownNow的方法, 有timeout控制.取消在workQueue中Pending的任务,并中断所有阻塞函数.
     */
    public static void normalShutdown(ExecutorService pool, int timeout, TimeUnit timeUnit) {
        try {
            pool.shutdownNow();
            if (!pool.awaitTermination(timeout, timeUnit)) {
                System.err.println("Pool did not terminated");
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.error("normalShutdown error:", e);
        }
    }

}
