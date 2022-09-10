package io.fxtahe.rpc.remoing.netty;

import io.fxtahe.rpc.common.core.RpcRequest;
import io.fxtahe.rpc.common.serialize.Serialization;
import io.fxtahe.rpc.common.serialize.SerializationEnum;
import io.fxtahe.rpc.common.serialize.SerializationFactory;
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
        if ((requestByte & FxProtocol.MESSAGE_FLAG) == 0) {
            //decode response

        } else {
            //decode request
            RpcRequest rpcRequest = new RpcRequest();
            rpcRequest.setTwoWay((requestByte & FxProtocol.TWO_WAY) != 0);
            rpcRequest.setHeartBeat((requestByte & FxProtocol.HEART_BEAT) != 0);
            rpcRequest.setId(id);
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
                byte serializationId = (byte) (requestByte & FxProtocol.SERIALIZATION_MASK);
                Serialization serialization = SerializationFactory.buildSerialization(SerializationEnum.getEnum(serializationId));
                Object deserialize = serialization.deserialize(bytes, Object.class);
                rpcRequest.setData(deserialize);
            }
            out.add(rpcRequest);
        }

    }
}
