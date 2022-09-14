package io.fxtahe.rpc.common.registry;

import java.util.List;

/**
 * 服务状态监听
 * @author fxtahe
 * @since 2022-08-21 15:04
 */
@FunctionalInterface
public interface ServiceListener {


    void onStateChange(String serviceId, List<ServiceInstance> serviceInstances, ServiceChangeState newState);

}
