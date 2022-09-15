package io.fxtahe.rpc.common.core;

import io.fxtahe.rpc.common.util.ClassUtil;

import java.io.Serializable;

/**
 * @author fxtahe
 * @since 2022/8/19 10:45
 */
public class Invocation implements Serializable {

    private String methodName;

    private String interfaceName;

    private Object[] arguments;

    private transient Class<?>[] parameterTypes;

    private String parameterTypesDesc;

    private Class<?> returnType;

    public Invocation() {
    }

    public Invocation(String methodName, String interfaceName, Object[] arguments, Class<?>[] parameterTypes, Class<?> returnType) {
        this.methodName = methodName;
        this.interfaceName = interfaceName;
        this.arguments = arguments;
        this.returnType = returnType;
        this.parameterTypes = parameterTypes == null ? new Class[0] : parameterTypes;
        this.parameterTypesDesc = parameterTypes == null ? "" : ClassUtil.getDesc(parameterTypes);
    }


    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    public String getInterfaceName() {
        return interfaceName;
    }

    public void setInterfaceName(String interfaceName) {
        this.interfaceName = interfaceName;
    }

    public Object[] getArguments() {
        return arguments;
    }

    public void setArguments(Object[] arguments) {
        this.arguments = arguments;
    }

    public Class<?>[] getParameterTypes() {
        return parameterTypes;
    }

    public void setParameterTypes(Class<?>[] parameterTypes) {
        this.parameterTypes = parameterTypes;
    }

    public String getParameterTypesDesc() {
        return parameterTypesDesc;
    }

    public void setParameterTypesDesc(String parameterTypesDesc) {
        this.parameterTypesDesc = parameterTypesDesc;
    }

    public Class<?> getReturnType() {
        return returnType;
    }

    public void setReturnType(Class<?> returnType) {
        this.returnType = returnType;
    }


}
