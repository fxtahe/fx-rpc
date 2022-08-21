package io.fxtahe.rpc.registry.zookeeper;

import java.io.Serializable;

/**
 * @author fxtahe
 * @since 2022-08-20 21:59
 */
public class ServiceMeta implements Serializable {

    private String appName;

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }
}
