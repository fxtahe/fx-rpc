package io.fxtahe.rpc.common.registry;

import com.google.common.base.Objects;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * 服务实例
 * @author fxtahe
 * @since 2022-08-21 14:38
 */
public class ServiceInstance implements Serializable {

    private static final long serialVersionUID = 5914123259532236479L;

    private String id;

    private String serviceId;

    private String host;

    private int port;

    private long registrationTime;

    private Map<String,Object> metaData = new HashMap<>(4);

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getServiceId() {
        return serviceId;
    }

    public void setServiceId(String serviceId) {
        this.serviceId = serviceId;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public long getRegistrationTime() {
        return registrationTime;
    }

    public void setRegistrationTime(long registrationTime) {
        this.registrationTime = registrationTime;
    }

    public Object getMetaData(String key) {
        return metaData.get(key);
    }

    public void addMetaData(String key,String value) {
        this.metaData.put(key,value);
    }

    public Map<String, Object> getMetaData() {
        return metaData;
    }

    public void setMetaData(Map<String, Object> metaData) {
        this.metaData = metaData;
    }

    public String getAddress(){
        return String.join(":",host,String.valueOf(port));
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ServiceInstance that = (ServiceInstance) o;
        return port == that.port && Objects.equal(id, that.id) && Objects.equal(serviceId, that.serviceId) && Objects.equal(host, that.host) && Objects.equal(metaData, that.metaData);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id, serviceId, host, port, metaData);
    }
}
