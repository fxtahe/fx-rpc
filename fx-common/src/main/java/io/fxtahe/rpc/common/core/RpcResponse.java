package io.fxtahe.rpc.common.core;

import io.fxtahe.rpc.common.costants.StatusConstants;
import io.fxtahe.rpc.common.util.IdGenerator;

/**
 * @author fxtahe
 * @since 2022/8/18 16:15
 */
public class RpcResponse {

    private long id;

    private byte status = StatusConstants.OK;

    private boolean heartBeat;

    private Object data;

    private String errorMsg;

    private String  serializationName;

    public String getSerializationName() {
        return serializationName;
    }

    public void setSerializationName(String serializationName) {
        this.serializationName = serializationName;
    }

    public RpcResponse() {
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public byte getStatus() {
        return status;
    }

    public void setStatus(byte status) {
        this.status = status;
    }

    public boolean isHeartBeat() {
        return heartBeat;
    }

    public void setHeartBeat(boolean heartBeat) {
        this.heartBeat = heartBeat;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public String getErrorMsg() {
        return errorMsg;
    }

    public void setErrorMsg(String errorMsg) {
        this.errorMsg = errorMsg;
    }


    @Override
    public String toString() {
        return "RpcResponse{" +
                "id=" + id +
                ", status=" + status +
                ", heartBeat=" + heartBeat +
                ", data=" + data +
                ", errorMsg='" + errorMsg + '\'' +
                ", serializationName='" + serializationName + '\'' +
                '}';
    }
}
