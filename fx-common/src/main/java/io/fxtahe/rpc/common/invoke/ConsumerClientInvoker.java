package io.fxtahe.rpc.common.invoke;

import io.fxtahe.rpc.common.context.RpcContext;
import io.fxtahe.rpc.common.core.*;
import io.fxtahe.rpc.common.costants.InvokeTypeEnum;
import io.fxtahe.rpc.common.exception.RpcException;
import io.fxtahe.rpc.common.future.FutureManager;
import io.fxtahe.rpc.common.future.RpcFuture;
import io.fxtahe.rpc.common.future.RpcFutureAdapter;
import io.fxtahe.rpc.common.registry.ServiceInstance;
import io.fxtahe.rpc.common.remoting.Client;
import io.fxtahe.rpc.common.util.IdGenerator;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import static io.fxtahe.rpc.common.costants.InvocationConstants.*;

/**
 * @author fxtahe
 * @since 2022-09-24 20:52
 */
public class ConsumerClientInvoker implements Invoker{

    private Client client;

    private ServiceInstance serviceInstance;

    public ConsumerClientInvoker(Client client, ServiceInstance serviceInstance) {
        this.client = client;
        this.serviceInstance = serviceInstance;
    }

    @Override
    public Result invoke(Invocation invocation) {
        AsyncResult result = doInvoke(invocation);
        resolveInvokeTypeResult(result,invocation);
        return result;
    }

    private void resolveInvokeTypeResult(AsyncResult result,Invocation invocation) {
        InvokeTypeEnum invokeType = InvokeTypeEnum.valueOf((String) invocation.getAttribute(INVOKE_TYPE_KEY));
        if(InvokeTypeEnum.ASYNC.equals(invokeType)){
            RpcContext.getContext().setFuture(new RpcFutureAdapter<>(result.getResultFuture()));
        }else if(InvokeTypeEnum.SYNC.equals(invokeType)){
            try {
                if(invocation.getAttribute(TIMEOUT_KEY) instanceof Integer){
                    result.getResultFuture().get((Integer) invocation.getAttribute(TIMEOUT_KEY), TimeUnit.MILLISECONDS);
                }else{
                    result.getResultFuture().join();
                }
            } catch (Exception e) {
                throw new RpcException(e);
            }
        }
    }

    public AsyncResult doInvoke(Invocation invocation){
        RpcRequest rpcRequest = new RpcRequest();
        rpcRequest.setSerializationName((String) invocation.getAttribute(SERIALIZATION_NAME_KEY));
        rpcRequest.setId(IdGenerator.generateId());
        rpcRequest.setData(invocation);
        InvokeTypeEnum invokeType = InvokeTypeEnum.valueOf((String) invocation.getAttribute(INVOKE_TYPE_KEY));
        if(InvokeTypeEnum.ONEWAY.equals(invokeType)){
            rpcRequest.setTwoWay(false);
            client.send(rpcRequest);
            AppResult appResult = new AppResult();
            CompletableFuture<AppResult> appFuture = new CompletableFuture<>();
            appFuture.complete(appResult);
            return new AsyncResult(appFuture,invocation);
        }else{
            rpcRequest.setTwoWay(true);
            RpcFuture future = FutureManager.createFuture(rpcRequest);
            try{
                client.send(rpcRequest);
            }catch (Exception e){
                future.cancel(true);
                AppResult appResult = new AppResult();
                appResult.setException(e);
                CompletableFuture<AppResult> appFuture = new CompletableFuture<>();
                appFuture.complete(appResult);
                return new AsyncResult(appFuture,invocation);
            }
            return new AsyncResult(future.thenApply(r -> (AppResult) r),invocation);
        }
    }

    @Override
    public String getInterfaceName() {
        return serviceInstance.getServiceId();
    }
}
