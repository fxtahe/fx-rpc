package io.fxtahe.rpc.remoing.netty;


import io.fxtahe.rpc.common.core.RpcRequest;
import io.fxtahe.rpc.common.core.RpcResponse;
import io.fxtahe.rpc.common.costants.StatusConstants;
import io.fxtahe.rpc.common.serialize.Serialization;
import io.fxtahe.rpc.common.serialize.SerializationEnum;
import io.fxtahe.rpc.common.serialize.SerializationFactory;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

import java.util.Objects;

/**
 * @author fxtahe
 * @since 2022/9/9 15:44
 */
public class NettyEncoder extends MessageToByteEncoder<Object> {


    @Override
    protected void encode(ChannelHandlerContext ctx, Object msg, ByteBuf out) throws Exception {
        if(msg instanceof RpcRequest){
            RpcRequest rpcRequest = (RpcRequest) msg;
            out.writeByte(FxProtocol.MAGIC_NUM);
            out.writeByte(FxProtocol.VERSION);
            out.writeLong(rpcRequest.getId());
            SerializationEnum serializationEnum = SerializationEnum.getEnum(rpcRequest.getSerializationName());
            byte serializationId =serializationEnum.getId();
            byte requestByte = (byte) (serializationId | FxProtocol.MESSAGE_FLAG);
            int dataLength;
            if(rpcRequest.isTwoWay()){
                requestByte |= FxProtocol.TWO_WAY;
            }
            if(rpcRequest.isHeartBeat()){
                requestByte |=FxProtocol.HEART_BEAT;
            }
            out.writeByte(requestByte);


            out.writeByte(StatusConstants.OK);
            if(rpcRequest.isHeartBeat()){
                dataLength =0;
                out.writeInt(dataLength);
            }else {
                Serialization serialization = SerializationFactory.buildSerialization(serializationEnum);
                Object data = rpcRequest.getData();
                if(Objects.isNull(data)){
                    dataLength=0;
                    out.writeInt(dataLength);
                }else {
                    byte[] bytes = serialization.serialize(data);
                    dataLength = bytes.length;
                    out.writeInt(dataLength);
                    out.writeBytes(bytes);
                }
            }
        }else if(msg instanceof RpcResponse){

        }else{

        }


    }



}
