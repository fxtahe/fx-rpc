package io.fxtahe.rpc.common.future;

import io.fxtahe.rpc.common.core.RpcRequest;

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
}
