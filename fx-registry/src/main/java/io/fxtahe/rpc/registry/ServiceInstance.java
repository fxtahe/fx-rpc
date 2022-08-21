package io.fxtahe.rpc.registry;

import java.util.Map;

/**
 * 服务实例
 * @author fxtahe
 * @since 2022-08-21 14:38
 */
public interface ServiceInstance {

    String getId();

    String getServiceId();

    String getHost();

    int getPort();

    Map<String, String> getMetadata();
}
