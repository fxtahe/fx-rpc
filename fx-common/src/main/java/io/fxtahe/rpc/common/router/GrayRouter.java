package io.fxtahe.rpc.common.router;

import io.fxtahe.rpc.common.core.Invocation;
import io.fxtahe.rpc.common.costants.InvocationConstants;
import io.fxtahe.rpc.common.ext.annotation.Extension;
import io.fxtahe.rpc.common.registry.ServiceInstance;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author fxtahe
 * @since 2022/10/11 16:49
 */
@Extension(alias = "gray",singleton = false)
public class GrayRouter implements Router{


    @Override
    public List<ServiceInstance> route(List<ServiceInstance> serviceInstances, Invocation invocation) {
        String version = (String) invocation.getAttribute(InvocationConstants.VERSION_KEY);
        if(version==null){
            return serviceInstances;
        }
        return serviceInstances.stream().filter(el->el.getMetaData(InvocationConstants.VERSION_KEY) != null && version.equals(el.getMetaData(InvocationConstants.VERSION_KEY))).collect(Collectors.toList());
    }


}
