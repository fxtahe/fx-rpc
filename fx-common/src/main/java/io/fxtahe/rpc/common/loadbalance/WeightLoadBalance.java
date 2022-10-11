package io.fxtahe.rpc.common.loadbalance;

import io.fxtahe.rpc.common.registry.ServiceInstance;

import javax.xml.ws.Service;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

/**
 * @author fxtahe
 * @since 2022/10/11 13:57
 */
public abstract class WeightLoadBalance implements LoadBalance{

    private static final int upThresholdTime = 10*60*1000;
    private static final int defaultThresholdWeight = 25;

    public int getWeight(ServiceInstance serviceInstance){
        long registrationTime = serviceInstance.getRegistrationTime();
        long currentTime = System.currentTimeMillis();
        long upTime = currentTime - registrationTime;
        if(upTime < 0){
            return 1;
        }else{
            return Math.min((int)(upTime / upThresholdTime) + 1, 4) * defaultThresholdWeight;
        }
    }


}
