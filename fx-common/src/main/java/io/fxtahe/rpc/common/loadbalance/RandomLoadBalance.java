package io.fxtahe.rpc.common.loadbalance;

import io.fxtahe.rpc.common.core.Invocation;
import io.fxtahe.rpc.common.ext.annotation.Extension;
import io.fxtahe.rpc.common.registry.ServiceInstance;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

/**
 * @author fxtahe
 * @since 2022-09-16 22:13
 */
@Extension(alias = "random")
public class RandomLoadBalance implements LoadBalance{

    @Override
    public ServiceInstance select( List<ServiceInstance> serviceInstances,Invocation invocation) {

        ThreadLocalRandom threadLocalRandom = ThreadLocalRandom.current();
        int index = threadLocalRandom.nextInt(serviceInstances.size());

        return serviceInstances.get(index);
    }
}
