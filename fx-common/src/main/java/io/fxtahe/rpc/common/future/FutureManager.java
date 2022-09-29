package io.fxtahe.rpc.common.future;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.Expiry;
import com.github.benmanes.caffeine.cache.LoadingCache;
import io.fxtahe.rpc.common.core.RpcRequest;
import org.checkerframework.checker.index.qual.NonNegative;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author fxtahe
 * @since 2022/9/15 19:07
 */
public class FutureManager {

    public static final Map<Long, RpcFuture> futures = new ConcurrentHashMap<>();



    public static RpcFuture createFuture(RpcRequest rpcRequest){
        long id = rpcRequest.getId();
        RpcFuture rpcFuture = new RpcFuture();
        rpcFuture.setId(id);
        rpcFuture.setRpcRequest(rpcRequest);
        futures.put(id,rpcFuture);
        return rpcFuture;
    }

    public static RpcFuture getFuture(long id){
        return futures.get(id);
    }


    public static RpcFuture removeFuture(long id){
        return futures.remove(id);
    }

    private static Cache<Long, RpcFuture> cache = Caffeine.newBuilder()
            .expireAfter(new Expiry<Long, RpcFuture>() {
                @Override
                public long expireAfterCreate(@NonNull Long aLong, @NonNull RpcFuture rpcFuture, long l) {
                    return 0;
                }

                @Override
                public long expireAfterUpdate(@NonNull Long aLong, @NonNull RpcFuture rpcFuture, long l, @NonNegative long l1) {
                    return 0;
                }

                @Override
                public long expireAfterRead(@NonNull Long aLong, @NonNull RpcFuture rpcFuture, long l, @NonNegative long l1) {
                    return 0;
                }
            })
            .build();
}
