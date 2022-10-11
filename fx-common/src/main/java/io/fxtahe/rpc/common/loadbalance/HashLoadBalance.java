package io.fxtahe.rpc.common.loadbalance;

import io.fxtahe.rpc.common.core.Invocation;
import io.fxtahe.rpc.common.ext.annotation.Extension;
import io.fxtahe.rpc.common.registry.ServiceInstance;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

/**
 * @author fxtahe
 * @since 2022/10/11 11:17
 */
@Extension(alias = "hash")
public class HashLoadBalance implements LoadBalance {

    private int ipHash;

    public HashLoadBalance(){
        try {
            String hostAddress = InetAddress.getLocalHost().getHostAddress();
            ipHash = hostAddress.hashCode() >>> 1;
        } catch (UnknownHostException e) {
            ipHash = ThreadLocalRandom.current().nextInt();
        }
    }

    @Override
    public ServiceInstance select(List<ServiceInstance> serviceInstances, Invocation invocation) {
        int i = ipHash % serviceInstances.size();
        return serviceInstances.get(i);
    }

}

