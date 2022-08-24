package io.fxtahe.rpc.registry;

import com.google.common.base.Objects;

/**
 * @author fxtahe
 * @since 2022-08-21 22:22
 */
public class Subscriber {

    private String serviceId;

    private String address;

    private ServiceListener serviceListener;


    public String getServiceId() {
        return serviceId;
    }

    public void setServiceId(String serviceId) {
        this.serviceId = serviceId;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public ServiceListener getServiceListener() {
        return serviceListener;
    }

    public void setServiceListener(ServiceListener serviceListener) {
        this.serviceListener = serviceListener;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Subscriber that = (Subscriber) o;
        return Objects.equal(serviceId, that.serviceId) && Objects.equal(address, that.address) && Objects.equal(serviceListener, that.serviceListener);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(serviceId, address, serviceListener);
    }
}
