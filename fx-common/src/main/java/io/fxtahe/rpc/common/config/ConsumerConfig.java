package io.fxtahe.rpc.common.config;



/**
 * @author fxtahe
 * @since 2022/8/19 15:03
 */
public class ConsumerConfig<T> extends AbstractConfig<T, ConsumerConfig<T>>{


    private boolean subscribe = true;

    /**
     * 服务引用
     * @return
     */
    public T refer(){
        //服务引用
        //获取服务列表
        //负载均衡
        //创建执行器 -> client(netty,http ...)
        //添加filter chain
        //获取 proxy
        return null;
    }





}
