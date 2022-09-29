package io.fxtahe.rpc.common.cluster;

import io.fxtahe.rpc.common.config.ConsumerConfig;
import io.fxtahe.rpc.common.core.*;
import io.fxtahe.rpc.common.costants.InvokeTypeEnum;
import io.fxtahe.rpc.common.exception.RemotingException;
import io.fxtahe.rpc.common.exception.RpcException;
import io.fxtahe.rpc.common.ext.annotation.Extension;
import io.fxtahe.rpc.common.filter.FilterChainBuilder;
import io.fxtahe.rpc.common.future.FutureManager;
import io.fxtahe.rpc.common.future.RpcFuture;
import io.fxtahe.rpc.common.invoke.Invoker;
import io.fxtahe.rpc.common.loadbalance.LoadBalance;
import io.fxtahe.rpc.common.registry.ServiceInstance;
import io.fxtahe.rpc.common.remoting.Client;
import io.fxtahe.rpc.common.util.IdGenerator;

import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * @author fxtahe
 * @since 2022-09-16 21:50
 */
@Extension(alias = "fail-fast",singleton = false)
public class FailFastCluster extends AbstractCluster {


    public FailFastCluster(ConsumerConfig consumerConfig) {
        super(consumerConfig);
    }


    @Override
    public Result doInvoke(Invocation invocation) {
        ServiceInstance serviceInstance = select(invocation);
        try{
            Invoker clusterInvoker = getClusterInvoker(serviceInstance);
            return clusterInvoker.invoke(invocation);
        }catch (Exception e){
            throw new RpcException(e);
        }

    }
}
