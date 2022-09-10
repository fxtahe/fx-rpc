package io.fxtahe.rpc.common.ext;

import io.fxtahe.rpc.common.util.ClassUtil;

/**
 * @author fxtahe
 * @since 2022/8/19 9:05
 */
public class ExtensionClass<T> {

    private String name;

    private int order;

    private Class<T> instanceClass;

    private Class<T> interfaceClass;

    private boolean singleton;

    private T instance;

    private String group;

    public ExtensionClass(String name, int order, Class<T> instanceClass, boolean singleton,String group) {
        this.name = name;
        this.order = order;
        this.instanceClass = instanceClass;
        this.singleton = singleton;
        this.group = group;
    }

    public Class<T> getInstanceClass() {
        return instanceClass;
    }

    public void setInstanceClass(Class<T> instanceClass) {
        this.instanceClass = instanceClass;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }

    public Class<T> getInterfaceClass() {
        return interfaceClass;
    }

    public void setInterfaceClass(Class<T> interfaceClass) {
        this.interfaceClass = interfaceClass;
    }

    public boolean isSingleton() {
        return singleton;
    }

    public void setSingleton(boolean singleton) {
        this.singleton = singleton;
    }

    public T getInstance() {
        T result;
        if(singleton){
            if(instance!=null){
                return instance;
            }
            result = instance = ClassUtil.newInstance(instanceClass);
        }else {
            result = ClassUtil.newInstance(instanceClass);
        }
        return result;

    }
    public T getInstance(Class<?>[] parameterTypes,Object[] parameters) {
        T result;
        if(singleton){
            if(instance!=null){
                return instance;
            }
            result = instance = ClassUtil.newInstance(instanceClass,parameterTypes,parameters);
        }else {
            result = ClassUtil.newInstance(instanceClass,parameterTypes,parameters);
        }
        return result;

    }


    public void setInstance(T instance) {
        this.instance = instance;
    }


    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }
}
