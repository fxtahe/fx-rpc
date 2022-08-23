package io.fxtahe.rpc.registry;

/**
 * 服务状态
 * @author fxtahe
 * @since 2022-08-21 15:07
 */
public enum ServiceChangeState {

    /**
     * service connect to registry state
     */
    CONNECTED,
    /**
     * service disconnect from registry state
     */
    DISCONNECTED
}
