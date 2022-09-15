package io.fxtahe.rpc.common.config;

import com.google.common.base.Objects;

import java.io.Serializable;

/**
 * @author fxtahe
 * @since 2022/9/13 16:46
 */
public class BootStrapConfig implements Serializable {


    private String host;

    private int port;

    private String serverType;

    private String  serializationName;

    public String getSerializationName() {
        return serializationName;
    }

    public void setSerializationName(String serializationName) {
        this.serializationName = serializationName;
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

    public String getServerType() {
        return serverType;
    }

    public void setServerType(String serverType) {
        this.serverType = serverType;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BootStrapConfig that = (BootStrapConfig) o;
        return port == that.port && Objects.equal(host, that.host) && Objects.equal(serverType, that.serverType);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(host, port, serverType);
    }
}
