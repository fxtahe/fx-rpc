package io.fxtahe.rpc.common.rpc;

/**
 * @author fxtahe
 * @since 2022/8/19 14:22
 */
public class RpcContext {

    private static final ThreadLocal<RpcContext> context = ThreadLocal.withInitial(RpcContext::new);


}
