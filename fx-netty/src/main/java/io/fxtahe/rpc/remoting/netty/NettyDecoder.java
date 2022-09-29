package io.fxtahe.rpc.remoting.netty;

import io.fxtahe.rpc.common.core.Invocation;
import io.fxtahe.rpc.common.core.RpcRequest;
import io.fxtahe.rpc.common.core.RpcResponse;
import io.fxtahe.rpc.common.costants.StatusConstants;
import io.fxtahe.rpc.common.future.FutureManager;
import io.fxtahe.rpc.common.future.RpcFuture;
import io.fxtahe.rpc.common.serialize.Serialization;
import io.fxtahe.rpc.common.serialize.SerializationEnum;
import io.fxtahe.rpc.common.serialize.SerializationFactory;
import io.fxtahe.rpc.common.util.ClassUtil;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * @author fxtahe
 * @since 2022/9/9 15:47
 */
public class NettyDecoder extends ByteToMessageDecoder {

    public static final Logger log = LoggerFactory.getLogger(NettyDecoder.class);


    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {

        int i = in.readableBytes();
        if (i < FxProtocol.HEADER_LENGTH) {
            return;
        }
        in.markReaderIndex();

        if (in.readByte() != FxProtocol.MAGIC_NUM) {
            return;
        }
        if (in.readByte() != FxProtocol.VERSION) {
            return;
        }
        long id = in.readLong();
        byte requestByte = in.readByte();
        byte serializationId = (byte) (requestByte & FxProtocol.SERIALIZATION_MASK);
        Serialization serialization = SerializationFactory.buildSerialization(SerializationEnum.getEnum(serializationId));
        if ((requestByte & FxProtocol.MESSAGE_FLAG) == 0) {
            //decode response
            RpcResponse rpcResponse = new RpcResponse();
            rpcResponse.setHeartBeat((requestByte & FxProtocol.HEART_BEAT) != 0);
            rpcResponse.setId(id);
            byte status = in.readByte();
            rpcResponse.setStatus(status);
            int dataLength = in.readInt();
            if(in.readableBytes()<dataLength){
                in.resetReaderIndex();
                return;
            }
            byte[] bytes = new byte[dataLength];
            in.readBytes(bytes);
            if(StatusConstants.OK == status){
                if(dataLength ==0 ||rpcResponse.isHeartBeat()){
                    rpcResponse.setData(null);
                }else{
                    Invocation invocation = getInvocation(id);
                    Object deserialize = serialization.deserialize(bytes, invocation.getReturnType());
                    rpcResponse.setData(deserialize);
                }
            }else{
                if(dataLength !=0 ){
                    rpcResponse.setErrorMsg(serialization.deserialize(bytes,String.class));
                }
            }
            out.add(rpcResponse);
        } else {
            //decode request
            RpcRequest rpcRequest = new RpcRequest();
            rpcRequest.setTwoWay((requestByte & FxProtocol.TWO_WAY) != 0);
            rpcRequest.setHeartBeat((requestByte & FxProtocol.HEART_BEAT) != 0);
            rpcRequest.setId(id);
            rpcRequest.setSerializationName(SerializationEnum.getEnum(serializationId).getName());
            in.skipBytes(1);

            int dataLength = in.readInt();
            if(in.readableBytes()<dataLength){
                in.resetReaderIndex();
                return;
            }
            if(dataLength ==0 ||rpcRequest.isHeartBeat()){
                rpcRequest.setData(null);
            }else{
                byte[] bytes = new byte[dataLength];
                in.readBytes(bytes);
                Invocation invocation = serialization.deserialize(bytes, Invocation.class);
                invocation.setParameterTypes(ClassUtil.getClasses(invocation.getParameterTypesDesc()));
                rpcRequest.setData(invocation);
            }
            out.add(rpcRequest);
        }

    }

    /**
     * @param id requestId
     * @return invocation
     */
    private Invocation getInvocation(long id) {
        RpcFuture future = FutureManager.getFuture(id);
        RpcRequest rpcRequest = future.getRpcRequest();
        return (Invocation) rpcRequest.getData();
    }
}
