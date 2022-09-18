package io.fxtahe.rpc.common.cluster;

import io.fxtahe.rpc.common.config.ConsumerConfig;
import io.fxtahe.rpc.common.core.*;
import io.fxtahe.rpc.common.costants.InvokeTypeEnum;
import io.fxtahe.rpc.common.exception.RemotingException;
import io.fxtahe.rpc.common.ext.annotation.Extension;
import io.fxtahe.rpc.common.future.FutureManager;
import io.fxtahe.rpc.common.future.RpcFuture;
import io.fxtahe.rpc.common.registry.ServiceInstance;
import io.fxtahe.rpc.common.remoting.Client;
import io.fxtahe.rpc.common.util.IdGenerator;

import java.util.concurrent.CompletableFuture;

/**
 * @author fxtahe
 * @since 2022-09-16 21:50
 */
@Extension(alias = "fail-fast")
public class FailFastCluster extends AbstractCluster {


    public FailFastCluster(ConsumerConfig consumerConfig) {
        super(consumerConfig);
    }


    @Override
    public Result invoke(Invocation invocation) {
        ServiceInstance select = select(invocation);
        Client client = bootStrap.refer(select);
        RpcRequest rpcRequest = new RpcRequest();
        rpcRequest.setSerializationName(consumerConfig.getSerializationName());
        rpcRequest.setId(IdGenerator.generateId());
        rpcRequest.setData(invocation);
        InvokeTypeEnum invokeType = consumerConfig.getInvokeType();
        if(InvokeTypeEnum.ONEWAY.equals(invokeType)){
            rpcRequest.setTwoWay(false);
            client.send(rpcRequest);
            AsyncResult asyncResult = new AsyncResult(new CompletableFuture<>());
            AppResult appResult = new AppResult();
            appResult.setValue(appResult);
            return asyncResult;
        }else{
            rpcRequest.setTwoWay(true);
            RpcFuture future = FutureManager.createFuture(rpcRequest);
            try{
                client.send(rpcRequest);
            }catch (Exception e){
                future.cancel(true);
                throw new RemotingException(e);
            }
            if(InvokeTypeEnum.ASYNC.equals(invokeType)){
                return new AsyncResult(future.thenApply(r -> (AppResult) r));
            }else{
                return (AppResult) future.join();
            }
        }
    }




}
