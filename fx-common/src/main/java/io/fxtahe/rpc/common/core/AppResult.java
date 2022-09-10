package io.fxtahe.rpc.common.core;

/**
 *
 * 真实返回业务结果
 * @author fxtahe
 * @since 2022/8/19 14:19
 */
public class AppResult implements Result{

    private Object value;

    private Throwable throwable;

    @Override
    public Object getValue() {
        return value;
    }

    @Override
    public void setValue(Object obj) {
        value = obj;
    }

    @Override
    public boolean hasException() {
        return throwable!=null;
    }

    @Override
    public Throwable getException() {
        return throwable;
    }

    @Override
    public void setException(Throwable t) {
        this.throwable = t;
    }
}
