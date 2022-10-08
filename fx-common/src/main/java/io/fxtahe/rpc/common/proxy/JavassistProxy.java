package io.fxtahe.rpc.common.proxy;



import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author fxtahe
 * @since 2022/10/8 11:20
 */
public class JavassistProxy{

    /**
     * cache proxy
     * key: classloader value:{key:interfaceName value:proxy}
     */
    private static final Map<ClassLoader, Map<String,JavassistProxy>> proxyCache = new WeakHashMap<>();

    public static final int limit= 65535;


    public static Object newProxyInstance(ClassLoader classLoader, Class<?>[] interfaces, InvocationHandler invocationHandler) {
        if (interfaces.length > limit) {
            throw new IllegalArgumentException("interface limit exceeded");
        }

        Map<String, JavassistProxy> proxyMap = proxyCache.computeIfAbsent(classLoader, (key) -> new ConcurrentHashMap<>());
        String interfaceKey = buildInterfaceKey(classLoader, interfaces);
        Class<?> neighbor = interfaces[0];
        JavassistClass javassistClass = new JavassistClass(classLoader);
        Set<String> methodSet = new HashSet<>();
        for(int i=0;i<interfaces.length;i++){
            Class<?> clazz=interfaces[i];
            javassistClass.addInterface(clazz);
            Method[] methods = clazz.getMethods();
            for(Method method:methods){
                if(methodSet.contains(method.getName()) || Modifier.isStatic(method.getModifiers())){
                    continue;
                }
                methodSet.add(method.getName());
                Class<?>[] parameterTypes = method.getParameterTypes();
                Class<?> returnType = method.getReturnType();
                StringBuilder sb = new StringBuilder("Object[] args = new Object[").append(parameterTypes.length).append("];");
                for(int j=0;j< parameterTypes.length;j++){
                    sb.append("args[").append(j).append("]=($w)$").append(j+1).append(";");
                }
                sb.append("Object result = this.handler.invoke(this,methods[").append(i).append("],args);");
                if(!Void.TYPE.equals(returnType)){
                    sb.append("return ").append("($r)ret;");
                }
                javassistClass.addMethod(sb.toString(),method);
            }
        }
        javassistClass.addField("private "+InvocationHandler.class.getName()+" handler;");
        javassistClass.addField("public static java.lang.reflect.Method[] methods;");
        javassistClass.addConstructor("public ");
        Class<?> aClass = javassistClass.toClass(neighbor, neighbor.getProtectionDomain());
        aClass.getField("methods").set();
        try {
            Constructor<?> constructor = aClass.getConstructor(InvocationHandler.class);
            return constructor.newInstance(invocationHandler);
        } catch (NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }

    }


    private static String buildInterfaceKey(ClassLoader classLoader,Class<?>[] classes){
        StringBuilder sb = new StringBuilder();
        for(Class<?> clazz:classes){

            String name = clazz.getName();
            if(!clazz.isInterface()){
                throw new RuntimeException(name+" is not a interface class");
            }
            Class<?> tmp = null;
            try {
                tmp = Class.forName(name,false,classLoader);
            } catch (ClassNotFoundException ignored) {
            }

            if(tmp!=clazz){
                throw new IllegalArgumentException(name + " is not visible from class loader");
            }

            sb.append(name).append(";");
        }
        return sb.toString();
    }
}
