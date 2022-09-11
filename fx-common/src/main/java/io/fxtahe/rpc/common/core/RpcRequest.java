package io.fxtahe.rpc.common.core;

import io.fxtahe.rpc.common.util.IdGenerator;

/**
 * @author fxtahe
 * @since 2022/8/18 16:15
 */
public class RpcRequest {

    private long id;

    private boolean heartBeat;

    private boolean twoWay;

    private int version;

    private Object data;

    private String  serializationName;

    public RpcRequest() {
    }

    public RpcRequest(long id, boolean heartBeat, boolean twoWay, Object data) {
        this.id = id;
        this.heartBeat = heartBeat;
        this.twoWay = twoWay;
        this.data = data;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public boolean isHeartBeat() {
        return heartBeat;
    }

    public void setHeartBeat(boolean heartBeat) {
        this.heartBeat = heartBeat;
    }

    public boolean isTwoWay() {
        return twoWay;
    }

    public void setTwoWay(boolean twoWay) {
        this.twoWay = twoWay;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public String getSerializationName() {
        return serializationName;
    }

    public void setSerializationName(String serializationName) {
        this.serializationName = serializationName;
    }
}
