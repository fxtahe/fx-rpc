package io.fxtahe.rpc.common.context;

import io.fxtahe.rpc.common.core.Result;

import java.util.concurrent.Future;

/**
 * @author fxtahe
 * @since 2022/8/19 14:22
 */
public class RpcContext {

    private static final ThreadLocal<RpcContext> context = ThreadLocal.withInitial(RpcContext::new);

    private Future<Result> future;

    public Future getFuture(){
        return future;
    }

    public void setFuture(Future future){
        this.future = future;
    }

    public static RpcContext getContext(){
        return context.get();
    }

}
