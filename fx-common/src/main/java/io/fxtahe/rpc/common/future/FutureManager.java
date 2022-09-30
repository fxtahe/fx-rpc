package io.fxtahe.rpc.common.future;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.Expiry;
import com.github.benmanes.caffeine.cache.LoadingCache;
import com.github.benmanes.caffeine.cache.RemovalCause;
import com.github.benmanes.caffeine.cache.RemovalListener;
import com.github.benmanes.caffeine.cache.Scheduler;
import io.fxtahe.rpc.common.core.Invocation;
import io.fxtahe.rpc.common.core.RpcRequest;
import io.fxtahe.rpc.common.core.RpcResponse;
import io.fxtahe.rpc.common.costants.StatusConstants;
import org.checkerframework.checker.index.qual.NonNegative;
import org.checkerframework.checker.index.qual.Positive;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

/**
 * @author fxtahe
 * @since 2022/9/15 19:07
 */
public class FutureManager {

    private static final Logger log = LoggerFactory.getLogger(FutureManager.class);

    private static final ScheduledThreadPoolExecutor scheduledThreadPool = new ScheduledThreadPoolExecutor(5, r -> {
        Thread thread = new Thread(r);
        thread.setName("timeout-scheduler-thread");
        thread.setDaemon(true);
        return thread;
    });

    private static final Cache<Long, RpcFuture> cache = Caffeine.newBuilder().scheduler(Scheduler.forScheduledExecutorService(scheduledThreadPool))
            .expireAfter(new Expiry<Long, RpcFuture>() {
                @Override
                public long expireAfterCreate(@NonNull Long id, @NonNull RpcFuture rpcFuture, long currentNano) {
                    return TimeUnit.MILLISECONDS.toNanos(rpcFuture.getTimeOut());
                }
                @Override
                public long expireAfterUpdate(@NonNull Long id, @NonNull RpcFuture rpcFuture, long l, @NonNegative long currentNano) {
                    return currentNano;
                }
                @Override
                public long expireAfterRead(@NonNull Long id, @NonNull RpcFuture rpcFuture, long l, @NonNegative long currentNano) {
                    return currentNano;
                }
            }).evictionListener((id, future, cause) -> {
                log.info("remove from cache {}, cause {}",id,cause);
                if(cause.equals(RemovalCause.EXPIRED)){
                    Invocation data = (Invocation) future.getRpcRequest().getData();
                    RpcResponse rpcResponse = new RpcResponse();
                    rpcResponse.setId(id);
                    rpcResponse.setErrorMsg("request "+data.getInterfaceName()+" service timeout!");
                    rpcResponse.setStatus(StatusConstants.TIMEOUT);
                    future.received(rpcResponse);
                }
            }).build();


    public static RpcFuture createFuture(RpcRequest rpcRequest,long timeout){
        long id = rpcRequest.getId();
        RpcFuture rpcFuture = new RpcFuture();
        rpcFuture.setTimeOut(timeout);
        rpcFuture.setId(id);
        rpcFuture.setRpcRequest(rpcRequest);
        cache.put(id,rpcFuture);
        return rpcFuture;
    }

    public static RpcFuture getFuture(long id){
        return cache.getIfPresent(id);
    }


    public static RpcFuture removeFuture(long id){
        RpcFuture future = cache.getIfPresent(id);
        cache.invalidate(id);
        return future;
    }


    public static void main(String[] args) {
        long current = System.currentTimeMillis() + 2000;
        System.out.println(current);

        System.out.println(TimeUnit.NANOSECONDS.toMillis(1322534285089100L));

    }

}
