package io.fxtahe.rpc.common.config;

/**
 * @author fxtahe
 * @since 2022/8/19 15:03
 */
public class ServiceConfig<T> extends AbstractConfig<T, ServiceConfig<T>> {

    private T ref;


    /**
     * 发布服务
     */
    public void export(){
        //服务注册

    }

    public T getRef() {
        return ref;
    }

    public void setRef(T ref) {
        this.ref = ref;
    }
}
