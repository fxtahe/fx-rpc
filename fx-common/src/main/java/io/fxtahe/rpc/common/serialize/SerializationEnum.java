package io.fxtahe.rpc.common.serialize;

/**
 * @author fxtahe
 * @since 2022/9/9 17:40
 */
public enum SerializationEnum {

    /**
     * java serialization
     */
    JAVA("java", (byte) 1),
    /**
     * hessian serialization
     */
    HESSIAN("hessian", (byte) 2),
    /**
     * json serialization
     */
    JSON("json", (byte) 3),
    /**
     * protobuf serialization
     */
    PROTOBUF("protobuf", (byte) 4);
    /**
     * name
     */
    private final String name;
    /**
     * id
     */
    private final byte id;


    SerializationEnum(String name, byte id) {
        this.id = id;
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public byte getId() {
        return id;
    }

    public static SerializationEnum getEnum(String value) {
        for(SerializationEnum v : values()){
            if(v.getName().equalsIgnoreCase(value)) {
                return v;
            }
        }
        throw new IllegalArgumentException();
    }

    public static SerializationEnum getEnum(byte id) {
        for(SerializationEnum v : values()){
            if(v.getId() ==id ) {
                return v;
            }
        }
        throw new IllegalArgumentException();
    }




}
