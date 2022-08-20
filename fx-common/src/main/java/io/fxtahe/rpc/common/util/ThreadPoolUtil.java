package io.fxtahe.rpc.common.util;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author fxtahe
 * @since 2022/8/19 10:45
 */
public class ThreadPoolUtil {

    private static final ThreadPoolExecutor pool = new ThreadPoolExecutor(10, 200,
            5000L, TimeUnit.SECONDS, new ArrayBlockingQueue<>(500), (ThreadFactory) Thread::new);

    static {
        pool.prestartAllCoreThreads();
    }

    public static ThreadPoolExecutor getPool(){
        return pool;
    }

    public static void execute(Runnable runnable){
        pool.execute(runnable);
    }

    public static <T> Future<T> submit(Callable<T> callable){
        return pool.submit(callable);
    }


}
