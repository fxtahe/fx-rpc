package io.fxtahe.rpc.registry;

import com.google.common.base.Objects;

import java.io.Serializable;
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

    private Map<String,String> metaData;

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

    public Map<String, String> getMetaData() {
        return metaData;
    }

    public void setMetaData(Map<String, String> metaData) {
        this.metaData = metaData;
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
