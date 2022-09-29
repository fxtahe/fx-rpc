package io.fxtahe.rpc.common.cluster;

import io.fxtahe.rpc.common.bootstrap.BootStrap;
import io.fxtahe.rpc.common.config.ConsumerConfig;
import io.fxtahe.rpc.common.core.Invocation;
import io.fxtahe.rpc.common.core.Result;
import io.fxtahe.rpc.common.exception.RpcException;
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

import static io.fxtahe.rpc.common.costants.InvocationConstants.INVOKE_TYPE_KEY;
import static io.fxtahe.rpc.common.costants.InvocationConstants.RETRIES_NUM_KEY;
import static io.fxtahe.rpc.common.costants.InvocationConstants.SERIALIZATION_NAME_KEY;
import static io.fxtahe.rpc.common.costants.InvocationConstants.TIMEOUT_KEY;

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
        if(list ==null || list.size()==0){
            throw new RpcException("Unable found any available service of "+invocation.getInterfaceName());
        }
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

    @Override
    public String getInterfaceName() {
        return consumerConfig.getInterfaceClass().getName();
    }


    @Override
    public void shutdown() throws Exception {
        bootStrap.unRefer(consumerConfig.getInterfaceClass().getName());
    }

    @Override
    public Result invoke(Invocation invocation) {
        invocation.setAttribute(SERIALIZATION_NAME_KEY,consumerConfig.getSerializationName());
        invocation.setAttribute(INVOKE_TYPE_KEY,consumerConfig.getInvokeType().name());
        invocation.setAttribute(RETRIES_NUM_KEY,consumerConfig.getRetries());
        invocation.setAttribute(TIMEOUT_KEY,consumerConfig.getTimeOut());
        return doInvoke(invocation);
    }

    @Override
    public Invoker getClusterInvoker(ServiceInstance serviceInstance) {
        return bootStrap.refer(serviceInstance);
    }

    public abstract Result doInvoke(Invocation invocation);

}
