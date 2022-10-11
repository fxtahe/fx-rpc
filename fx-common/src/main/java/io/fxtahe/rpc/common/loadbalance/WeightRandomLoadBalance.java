package io.fxtahe.rpc.common.loadbalance;

import io.fxtahe.rpc.common.core.Invocation;
import io.fxtahe.rpc.common.ext.annotation.Extension;
import io.fxtahe.rpc.common.registry.ServiceInstance;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

/**
 * @author fxtahe
 * @since 2022/10/11 13:48
 */
@Extension(alias = "weightRandom")
public class WeightRandomLoadBalance extends WeightLoadBalance{

    @Override
    public ServiceInstance select(List<ServiceInstance> serviceInstances, Invocation invocation) {
        int totalWeight = 0;
        int[] weights = new int[serviceInstances.size()];
        for(int i=0;i<serviceInstances.size();i++) {
            ServiceInstance serviceInstance = serviceInstances.get(i);
            int weight = getWeight(serviceInstance);
            totalWeight +=weight;
            weights[i] = totalWeight;
        }
        ThreadLocalRandom threadLocalRandom = ThreadLocalRandom.current();
        int index = threadLocalRandom.nextInt(totalWeight);
        for(int i=0;i<weights.length;i++){
            int weight = weights[i];
            if(weight>index){
                return serviceInstances.get(i);
            }
        }
        return serviceInstances.get(ThreadLocalRandom.current().nextInt(serviceInstances.size()));
    }
}
