package io.fxtahe.rpc.common.config;

import com.google.common.base.Objects;

/**
 * @author fxtahe
 * @since 2022/8/19 15:03
 */
public class RegistryConfig {


    private String registryType = "zookeeper";

    private boolean useCache = true;

    private boolean autoRecover = true;

    private String connectionString;

    private int connectTimeout;

    private int readTimeout;



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

    public boolean isUseCache() {
        return useCache;
    }

    public void setUseCache(boolean useCache) {
        this.useCache = useCache;
    }

    public boolean isAutoRecover() {
        return autoRecover;
    }

    public void setAutoRecover(boolean autoRecover) {
        this.autoRecover = autoRecover;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RegistryConfig that = (RegistryConfig) o;
        return useCache == that.useCache && autoRecover == that.autoRecover && connectTimeout == that.connectTimeout && readTimeout == that.readTimeout && Objects.equal(registryType, that.registryType) && Objects.equal(connectionString, that.connectionString);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(registryType, useCache, autoRecover, connectionString, connectTimeout, readTimeout);
    }
}
