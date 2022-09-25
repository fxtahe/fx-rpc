package io.fxtahe.rpc.common.core;

import io.fxtahe.rpc.common.util.ClassUtil;

import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;

/**
 * @author fxtahe
 * @since 2022/8/31 17:19
 */
public class AsyncResult implements Result {

    private final Invocation invocation;
    private CompletableFuture<AppResult> resultFuture;

    public AsyncResult(CompletableFuture<AppResult> resultFuture,Invocation invocation) {
        this.resultFuture = resultFuture;
        this.invocation = invocation;
    }

    @Override
    public Object getValue() {
        if (resultFuture.isDone()) {
            try {
                return resultFuture.get().getValue();
            } catch (Exception exception) {
                throw new RuntimeException("set value error when fetch result");
            }
        }

        return createDefaultResponse().getValue();
    }

    @Override
    public void setValue(Object obj) {
        if (resultFuture.isDone()) {
            try {
                resultFuture.get().setValue(obj);
            } catch (Exception exception) {
                throw new RuntimeException("set value error when fetch result");
            }
        } else {
            AppResult appResult = new AppResult();
            appResult.setValue(obj);
            resultFuture.complete(appResult);
        }
    }

    @Override
    public Throwable getException() {

        if (resultFuture.isDone()) {
            try {
                return resultFuture.get().getException();
            } catch (Exception exception) {
                throw new RuntimeException("get exception error when fetch result");
            }
        }
        return null;
    }

    @Override
    public void setException(Throwable t) {
        if (resultFuture.isDone()) {
            try {
                resultFuture.get().setException(t);
            } catch (Exception exception) {
                throw new RuntimeException("set value error when fetch result");
            }
        } else {
            AppResult appResult = new AppResult();
            appResult.setException(t);
            resultFuture.complete(appResult);
        }
    }

    @Override
    public boolean hasException() {
        return getException()!=null;
    }

    @Override
    public Result whenComplete(BiConsumer<Result, Throwable> fn) {
        resultFuture.whenComplete(fn);
        return this;
    }

    @Override
    public Object recreate() throws Throwable {
        return getValue();
    }

    public Result createDefaultResponse(){
        AppResult appResult = new AppResult();
        if(invocation.getReturnType()!=null && invocation.getReturnType().isPrimitive()){
            Object defaultPrimitiveValue = ClassUtil.getDefaultPrimitiveValue(invocation.getReturnType());
            appResult.setValue(defaultPrimitiveValue);
        }
        return appResult;

    }

    public CompletableFuture<AppResult> getResultFuture(){
        return this.resultFuture;
    }
}
