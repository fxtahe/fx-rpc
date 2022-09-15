package io.fxtahe.rpc.common.future;

import io.fxtahe.rpc.common.core.RpcRequest;
import io.fxtahe.rpc.common.core.RpcResponse;
import io.fxtahe.rpc.common.costants.StatusConstants;

import java.util.concurrent.CompletableFuture;

/**
 * @author fxtahe
 * @since 2022/9/15 18:58
 */
public class RpcFuture extends CompletableFuture<Object> {


    private long id;

    private RpcRequest rpcRequest;


    public void received(RpcResponse response){
        if(StatusConstants.OK == response.getStatus()){
            this.complete(response.getData());
        }else{
            this.completeExceptionally(new RuntimeException(response.getErrorMsg()));
        }
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public RpcRequest getRpcRequest() {
        return rpcRequest;
    }

    public void setRpcRequest(RpcRequest rpcRequest) {
        this.rpcRequest = rpcRequest;
    }
}
