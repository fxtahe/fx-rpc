package io.fxtahe.rpc.common.config;

import com.google.common.base.Objects;

/**
 * @author fxtahe
 * @since 2022/8/19 15:03
 */
public class RegistryConfig {


    private String registryType;

    private String connectionString;

    private int connectTimeout;

    private int readTimeout;

    private boolean subscribe;

    private boolean register;


    public String getRegistryType() {
        return registryType;
    }

    public void setRegistryType(String registryType) {
        this.registryType = registryType;
    }

    public String getConnectionString() {
        return connectionString;
    }

    public void setConnectionString(String connectionString) {
        this.connectionString = connectionString;
    }

    public int getConnectTimeout() {
        return connectTimeout;
    }

    public void setConnectTimeout(int connectTimeout) {
        this.connectTimeout = connectTimeout;
    }

    public int getReadTimeout() {
        return readTimeout;
    }

    public void setReadTimeout(int readTimeout) {
        this.readTimeout = readTimeout;
    }

    public boolean isSubscribe() {
        return subscribe;
    }

    public void setSubscribe(boolean subscribe) {
        this.subscribe = subscribe;
    }

    public boolean isRegister() {
        return register;
    }

    public void setRegister(boolean register) {
        this.register = register;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RegistryConfig that = (RegistryConfig) o;
        return connectTimeout == that.connectTimeout && readTimeout == that.readTimeout && subscribe == that.subscribe && register == that.register && Objects.equal(registryType, that.registryType) && Objects.equal(connectionString, that.connectionString);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(registryType, connectionString, connectTimeout, readTimeout, subscribe, register);
    }
}
