package io.fxtahe.rpc.common.config;


import io.fxtahe.rpc.common.cluster.Cluster;
import io.fxtahe.rpc.common.costants.InvokeTypeEnum;
import io.fxtahe.rpc.common.ext.ExtensionLoaderFactory;
import io.fxtahe.rpc.common.proxy.ProxyFactory;
import io.fxtahe.rpc.common.registry.ServiceListener;
import io.fxtahe.rpc.common.registry.ServiceRegistry;
import io.fxtahe.rpc.common.registry.ServiceRegistryFactory;

/**
 * @author fxtahe
 * @since 2022/8/19 15:03
 */
public class ConsumerConfig<T> extends AbstractConfig<T, ConsumerConfig<T>>{


    private boolean subscribe = true;

    /**
     * reflect type
     */
    private String reflectType = "jdk";
    /**
     * cluster type
     */
    private String clusterType = "fail-fast";
    /**
     * execute request client type
     */
    private String client = "netty";

    /**
     * serialization type
     */
    private String SerializationName = "hessian";

    /**
     * client load balance
     */
    private String loadBalance = "random";

    /**
     * invoke sync
     */
    private InvokeTypeEnum invokeType = InvokeTypeEnum.ASYNC;

    /**
     * cluster invoker
     */
    private Cluster cluster;

    /**
     * service change listener
     */
    private ServiceListener serviceListener;

    /**
     * retries
     */
    private int retries;

    /**
     * execute timeOut
     */
    private long timeOut;

    /**
     * interface refer
     */
    private T ref;

    /**
     * 服务引用
     * @return ref
     */
    public T refer(){
        if(ref!=null){return ref;}

        synchronized (this){
            cluster = ExtensionLoaderFactory.getExtensionLoader(Cluster.class).getInstance(clusterType, new Class[]{ConsumerConfig.class}, new Object[]{this});
            if (registries != null && isSubscribe()) {
                for (RegistryConfig registryConfig : registries) {
                    ServiceRegistry serviceRegistry = ServiceRegistryFactory.buildRegistry(registryConfig);
                    serviceRegistry.subscribe(interfaceClass.getName(),serviceListener);
                }
            }
            ProxyFactory proxyFactory = ExtensionLoaderFactory.getExtensionLoader(ProxyFactory.class).getInstance(reflectType);
            ref = proxyFactory.getProxy(cluster, interfaceClass);
            return ref;
        }
    }


    public void unRefer(){
        if(ref==null) return;
        synchronized (this){
            try{
                if (registries != null && isSubscribe()) {
                    for (RegistryConfig registryConfig : registries) {
                        ServiceRegistry serviceRegistry = ServiceRegistryFactory.buildRegistry(registryConfig);
                        serviceRegistry.unsubscribe(interfaceClass.getName(),serviceListener);
                    }
                }
                cluster.shutdown();
                ref = null;
            }catch (Exception e){
                throw new RuntimeException(e);
            }

        }

    }


    public boolean isSubscribe() {
        return subscribe;
    }

    public void setSubscribe(boolean subscribe) {
        this.subscribe = subscribe;
    }

    public String getReflectType() {
        return reflectType;
    }

    public void setReflectType(String reflectType) {
        this.reflectType = reflectType;
    }

    public String getClusterType() {
        return clusterType;
    }

    public void setClusterType(String clusterType) {
        this.clusterType = clusterType;
    }

    public String getClient() {
        return client;
    }

    public void setClient(String client) {
        this.client = client;
    }

    public String getLoadBalance() {
        return loadBalance;
    }

    public void setLoadBalance(String loadBalance) {
        this.loadBalance = loadBalance;
    }

    public String getSerializationName() {
        return SerializationName;
    }

    public void setSerializationName(String serializationName) {
        SerializationName = serializationName;
    }

    public InvokeTypeEnum getInvokeType() {
        return invokeType;
    }

    public void setInvokeType(InvokeTypeEnum invokeType) {
        this.invokeType = invokeType;
    }

    public T getRef() {
        return ref;
    }

    public void setRef(T ref) {
        this.ref = ref;
    }


    public ServiceListener getServiceListener() {
        return serviceListener;
    }

    public void setServiceListener(ServiceListener serviceListener) {
        this.serviceListener = serviceListener;
    }

    public int getRetries() {
        return retries;
    }

    public void setRetries(int retries) {
        this.retries = retries;
    }

    public long getTimeOut() {
        return timeOut;
    }

    public void setTimeOut(long timeOut) {
        this.timeOut = timeOut;
    }
}
