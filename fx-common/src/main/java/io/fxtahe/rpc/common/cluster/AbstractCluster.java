package io.fxtahe.rpc.common.cluster;

import io.fxtahe.rpc.common.bootstrap.BootStrap;
import io.fxtahe.rpc.common.bootstrap.BootStrapFactory;
import io.fxtahe.rpc.common.config.ConsumerConfig;
import io.fxtahe.rpc.common.core.Invocation;
import io.fxtahe.rpc.common.ext.ExtensionLoaderFactory;
import io.fxtahe.rpc.common.invoke.Invoker;
import io.fxtahe.rpc.common.loadbalance.LoadBalance;
import io.fxtahe.rpc.common.registry.ServiceInstance;
import io.fxtahe.rpc.common.registry.ServiceRegistry;
import io.fxtahe.rpc.common.registry.ServiceRegistryFactory;
import io.fxtahe.rpc.common.router.Router;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author fxtahe
 * @since 2022-09-16 22:09
 */
public abstract class AbstractCluster implements Cluster {


    private LoadBalance loadBalance;

    private List<Router> routeChain;

    private List<ServiceRegistry> serviceRegistries;

    protected BootStrap bootStrap;

    protected ConsumerConfig<?> consumerConfig;

    public AbstractCluster(ConsumerConfig<?> consumerConfig) {
        this.consumerConfig = consumerConfig;
        routeChain = ExtensionLoaderFactory.getExtensionLoader(Router.class).getExtensions();
        loadBalance = ExtensionLoaderFactory.getExtensionLoader(LoadBalance.class).getInstance(consumerConfig.getLoadBalance());
        serviceRegistries = consumerConfig.getRegistries().stream().map(ServiceRegistryFactory::buildRegistry).collect(Collectors.toList());
        bootStrap = ExtensionLoaderFactory.getExtensionLoader(BootStrap.class).getInstance(consumerConfig.getClient());
    }

    public ServiceInstance select(Invocation invocation){
        List<ServiceInstance> list = list(serviceRegistries, invocation);
        List<ServiceInstance> route = route(list, invocation);
        return loadBalance(route, invocation);
    }

    @Override
    public List<ServiceInstance> list(List<ServiceRegistry> registries,Invocation invocation) {
        return registries.stream().map(registry -> registry.getInstances(invocation.getInterfaceName())).filter(list -> list != null && !list.isEmpty()).reduce((a, b) -> {
            a.addAll(b);
            return a;
        }).orElse(new ArrayList<>());
    }

    @Override
    public List<ServiceInstance> route(List<ServiceInstance> serviceInstances, Invocation invocation) {
        if(routeChain!=null && routeChain.size()>0){
            for (Router router:routeChain){
                serviceInstances = router.route(serviceInstances, invocation);
            }
        }
        return serviceInstances;
    }

    @Override
    public ServiceInstance loadBalance(List<ServiceInstance> serviceInstances, Invocation invocation) {
        return loadBalance.select(serviceInstances,invocation);
    }


    private Invoker invoker;

    @Override
    public Class<?> getInterface() {
        return invoker.getInterface();
    }


    @Override
    public void shutdown() throws Exception {
        bootStrap.unRefer(invoker.getInterface().getName());
    }
}
