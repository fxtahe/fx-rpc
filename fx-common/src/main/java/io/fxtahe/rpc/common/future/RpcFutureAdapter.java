package io.fxtahe.rpc.common.future;

import io.fxtahe.rpc.common.core.AppResult;
import io.fxtahe.rpc.common.core.AsyncResult;
import io.fxtahe.rpc.common.core.Invocation;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.function.Function;

/**
 * @author fxtahe
 * @since 2022-09-18 21:47
 */
public class RpcFutureAdapter<T> extends CompletableFuture<T> {



    private CompletableFuture<AppResult> completableFuture;

    public RpcFutureAdapter(CompletableFuture<AppResult> completableFuture) {
        this.completableFuture = completableFuture;
        completableFuture.whenComplete(((appResult, throwable) -> {
            if(throwable!=null){
                if(throwable instanceof CompletionException){
                    throwable =throwable.getCause();
                }
                this.completeExceptionally(throwable);
            }
            if(appResult.hasException()){
                this.completeExceptionally(appResult.getException());
            }else{
                this.complete((T) appResult.getValue());
            }
        }));
    }

    @Override
    public boolean cancel(boolean mayInterruptIfRunning) {
        return completableFuture.cancel(mayInterruptIfRunning);
    }

}
