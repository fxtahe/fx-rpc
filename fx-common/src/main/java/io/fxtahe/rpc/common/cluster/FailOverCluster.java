package io.fxtahe.rpc.common.cluster;

import io.fxtahe.rpc.common.config.ConsumerConfig;
import io.fxtahe.rpc.common.core.Invocation;
import io.fxtahe.rpc.common.core.Result;
import io.fxtahe.rpc.common.exception.RpcException;
import io.fxtahe.rpc.common.ext.annotation.Extension;
import io.fxtahe.rpc.common.invoke.Invoker;
import io.fxtahe.rpc.common.registry.ServiceInstance;

import static io.fxtahe.rpc.common.costants.InvocationConstants.RETRIES_NUM_KEY;


/**
 * @author fxtahe
 * @since 2022/9/29 16:14
 */
@Extension(alias = "fail-over")
public class FailOverCluster extends AbstractCluster{


    public FailOverCluster(ConsumerConfig<?> consumerConfig) {
        super(consumerConfig);
    }

    @Override
    public Result doInvoke(Invocation invocation) {
        int retires = getRetries(invocation);
        RpcException rpcException = null;
        for(int i=0;i<retires;i++){
            ServiceInstance select = select(invocation);
            Invoker clusterInvoker = getClusterInvoker(select);
            try{
                return clusterInvoker.invoke(invocation);
            }catch (RpcException e){
                rpcException = e;
            }catch (Throwable t){
                rpcException = new RpcException(t);
            }
        }
        throw  rpcException;
    }

    public int getRetries(Invocation invocation){
        int retries = (int) invocation.getAttribute(RETRIES_NUM_KEY);
        return retries<=0?1:retries;
    }
}
