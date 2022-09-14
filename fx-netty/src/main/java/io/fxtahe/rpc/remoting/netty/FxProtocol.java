package io.fxtahe.rpc.remoting.netty;

/**
 *
 *
 * @author fxtahe
 * @since 2022/9/9 16:04
 */
public class FxProtocol {

    /**
     * header length
     */
     static final int HEADER_LENGTH = 16;
    /**
     * magic num 17
     * lucky number
     */
     static final byte MAGIC_NUM = 0x11;

    /**
     * rpc version
     */
    static final byte VERSION = 0x1;

    /**
     * request or response flag
     */
     static final byte MESSAGE_FLAG = (byte) 0x80;
    /**
     * two way flag
     */
     static final byte TWO_WAY = (byte) 0x40;
    /**
     * heart beat flag
     */
     static final byte HEART_BEAT = (byte) 0x20;
    /**
     * serialization mask
     */
     static final byte SERIALIZATION_MASK = (byte) 0x1f;

    






}
