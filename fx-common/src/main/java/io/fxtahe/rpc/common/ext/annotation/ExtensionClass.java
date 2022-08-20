package io.fxtahe.rpc.common.ext.annotation;

/**
 * @author XE-墨菲特(魏子阳)
 * @since 2022/8/18 17:19
 */
public class ExtensionClass<T> {

    private String name;

    private int order;

    private Class<T> interfaceClass;

    private boolean singleton;

    private T instance;



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
        return instance;
    }

    public void setInstance(T instance) {
        this.instance = instance;
    }
}
