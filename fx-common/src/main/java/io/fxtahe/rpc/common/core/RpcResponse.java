package io.fxtahe.rpc.common.core;

import io.fxtahe.rpc.common.costants.StatusConstants;

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


    public static RpcResponse builder(){
        return new RpcResponse();
    }

    public void id(long id){
        setId(id);
    }
    public void data(Object data){
        setData(data);
    }
    public void status(byte statusConstants){
        setStatus(statusConstants);
    }
    public void heartBeat(boolean heartBeat){
        setHeartBeat(heartBeat);
    }
    public void errorMsg(String errorMsg){
        setErrorMsg(errorMsg);
    }
    public RpcResponse build(){
        return this;
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




}
