package io.fxtahe.rpc.common.remoting;

import io.fxtahe.rpc.common.core.Invocation;
import io.fxtahe.rpc.common.core.Result;
import io.fxtahe.rpc.common.core.RpcRequest;
import io.fxtahe.rpc.common.core.RpcResponse;
import io.fxtahe.rpc.common.costants.StatusConstants;
import io.fxtahe.rpc.common.future.FutureManager;
import io.fxtahe.rpc.common.future.RpcFuture;
import io.fxtahe.rpc.common.handler.RequestProcessHandler;
import io.fxtahe.rpc.common.util.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;

/**
 * @author fxtahe
 * @since 2022/9/29 11:16
 */
public class DefaultConnectionHandler implements ConnectionHandler {

    public static final Logger log = LoggerFactory.getLogger(DefaultConnectionHandler.class);

    private RequestProcessHandler requestProcessHandler;

    public DefaultConnectionHandler(RequestProcessHandler requestProcessHandler) {
        this.requestProcessHandler = requestProcessHandler;
    }

    @Override
    public void received(Connection connection, Object message) {
        if (message instanceof RpcRequest) {
            RpcRequest rpcRequest = (RpcRequest) message;
            RpcResponse rpcResponse = new RpcResponse();
            rpcResponse.setId(rpcRequest.getId());
            if (rpcRequest.isHeartBeat()) {
                if (log.isDebugEnabled()) {
                    log.debug("receive heartbeat request");
                }
                rpcResponse.setHeartBeat(true);
                connection.send(rpcResponse);
            } else {
                rpcResponse.setSerializationName(rpcRequest.getSerializationName());
                Invocation data = (Invocation) rpcRequest.getData();
                dispatchHandle(data).whenComplete((r, t) -> {
                    if (t != null) {
                        rpcResponse.setErrorMsg(StringUtil.toString(t));
                        rpcResponse.setStatus(StatusConstants.BAD_RESPONSE);
                    } else {
                        if(r.hasException()){
                            rpcResponse.setErrorMsg(StringUtil.toString(r.getException()));
                            rpcResponse.setStatus(StatusConstants.BAD_RESPONSE);
                        }else{
                            rpcResponse.setData(r);
                        }
                    }
                    // ignore one way request
                    if (rpcRequest.isTwoWay()) {
                        connection.send(rpcResponse);
                    }
                }).exceptionally((t)->{
                    rpcResponse.setErrorMsg(StringUtil.toString(t));
                    rpcResponse.setStatus(StatusConstants.BAD_RESPONSE);
                    connection.send(rpcResponse);
                    return null;
                });
            }
        } else if (message instanceof RpcResponse) {
            RpcResponse rpcResponse = (RpcResponse) message;
            if (rpcResponse.isHeartBeat()) {
                if (log.isDebugEnabled()) {
                    log.debug("receive heartbeat response");
                }
            } else {
                long id = rpcResponse.getId();
                RpcFuture rpcFuture = FutureManager.removeFuture(id);
                if(rpcFuture!=null){
                    rpcFuture.received(rpcResponse);
                }
            }
        }
    }

    public CompletableFuture<Result> dispatchHandle(final Invocation invocation) {
        ExecutorService executorService = ThreadPoolRegister.selectThreadPool(invocation.getInterfaceName());
        return CompletableFuture.supplyAsync(() -> requestProcessHandler.handleRequest(invocation), executorService);
    }
}
