package io.fxtahe.rpc.common.proxy;



import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author fxtahe
 * @since 2022/10/8 11:20
 */
public class JavassistProxy {

    /**
     * cache proxy
     * key: classloader value:{key:interfaceName value:proxy}
     */
    private static final Map<ClassLoader, Map<String, Class<?>>> proxyCache = new WeakHashMap<>();

    public static final int limit = 65535;


    public static Object newProxyInstance(ClassLoader classLoader, Class<?>[] interfaces, InvocationHandler invocationHandler) {
        if (interfaces.length > limit) {
            throw new IllegalArgumentException("interface limit exceeded");
        }

        Map<String, Class<?>> proxyMap = proxyCache.computeIfAbsent(classLoader, (key) -> new ConcurrentHashMap<>());
        String interfaceKey = buildInterfaceKey(classLoader, interfaces);

        Class<?> targetClass = proxyMap.get(interfaceKey);
        if (targetClass == null) {
            synchronized (interfaces[0]) {
                targetClass = proxyMap.get(interfaceKey);
                if (targetClass == null) {
                    targetClass = buildProxyClass(classLoader, interfaces);
                    proxyMap.put(interfaceKey, targetClass);
                }
            }
        }
        try {
            Constructor<?> constructor = targetClass.getConstructor(InvocationHandler.class);
            return constructor.newInstance(invocationHandler);
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException(e);
        }
    }

    private static Class<?> buildProxyClass(ClassLoader classLoader, Class<?>[] interfaces) {
        Class<?> neighbor = interfaces[0];
        JavassistClass javassistClass = new JavassistClass(classLoader, neighbor);
        Set<String> methodSet = new HashSet<>();
        List<Method> methodList = new ArrayList<>();
        int methodInx = 0;
        for (Class<?> clazz : interfaces) {
            javassistClass.addInterface(clazz);
            Method[] methods = clazz.getMethods();
            for (Method method : methods) {
                if (methodSet.contains(method.getName()) || Modifier.isStatic(method.getModifiers())) {
                    continue;
                }
                methodSet.add(method.getName());
                Class<?>[] parameterTypes = method.getParameterTypes();
                Class<?> returnType = method.getReturnType();
                StringBuilder sb = new StringBuilder("Object[] args = new Object[").append(parameterTypes.length).append("];");
                for (int j = 0; j < parameterTypes.length; j++) {
                    sb.append("args[").append(j).append("]=($w)$").append(j + 1).append(";");
                }
                sb.append("Object result = handler.invoke(this, methods[").append(methodInx).append("], args);");
                if (!Void.TYPE.equals(returnType)) {
                    //sb.append(" return ").append(asArgument(returnType, "result")).append(';');
                    sb.append("return ").append("($r)result;");
                }
                javassistClass.addMethod(sb.toString(), method);
                methodList.add(method);
                methodInx++;
            }
        }
        try {
            javassistClass.addField("private " + InvocationHandler.class.getName() + " handler;");
            javassistClass.addField("public static java.lang.reflect.Method[] methods;");
            javassistClass.addConstructor(Modifier.PUBLIC, new Class[]{InvocationHandler.class}, new Class[0], "this.handler=$1;");
            Class<?> aClass = javassistClass.toClass(neighbor, neighbor.getProtectionDomain());
            aClass.getField("methods").set(null, methodList.toArray(new Method[0]));
            return aClass;
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            javassistClass.release();
        }

    }

    private static String asArgument(Class<?> cl, String name) {
        if (cl.isPrimitive()) {
            if (Boolean.TYPE == cl) {
                return name + "==null?false:((Boolean)" + name + ").booleanValue()";
            }
            if (Byte.TYPE == cl) {
                return name + "==null?(byte)0:((Byte)" + name + ").byteValue()";
            }
            if (Character.TYPE == cl) {
                return name + "==null?(char)0:((Character)" + name + ").charValue()";
            }
            if (Double.TYPE == cl) {
                return name + "==null?(double)0:((Double)" + name + ").doubleValue()";
            }
            if (Float.TYPE == cl) {
                return name + "==null?(float)0:((Float)" + name + ").floatValue()";
            }
            if (Integer.TYPE == cl) {
                return name + "==null?(int)0:((Integer)" + name + ").intValue()";
            }
            if (Long.TYPE == cl) {
                return name + "==null?(long)0:((Long)" + name + ").longValue()";
            }
            if (Short.TYPE == cl) {
                return name + "==null?(short)0:((Short)" + name + ").shortValue()";
            }
            throw new RuntimeException(name + " is unknown primitive type.");
        }
        return "(" + getName(cl) + ")" + name;
    }

    public static String getName(Class<?> c) {
        if (c.isArray()) {
            StringBuilder sb = new StringBuilder();
            do {
                sb.append("[]");
                c = c.getComponentType();
            }
            while (c.isArray());

            return c.getName() + sb.toString();
        }
        return c.getName();
    }

    private static String buildInterfaceKey(ClassLoader classLoader, Class<?>[] classes) {
        StringBuilder sb = new StringBuilder();
        for (Class<?> clazz : classes) {

            String name = clazz.getName();
            if (!clazz.isInterface()) {
                throw new RuntimeException(name + " is not a interface class");
            }
            Class<?> tmp = null;
            try {
                tmp = Class.forName(name, false, classLoader);
            } catch (ClassNotFoundException ignored) {
            }

            if (tmp != clazz) {
                throw new IllegalArgumentException(name + " is not visible from class loader");
            }

            sb.append(name).append(";");
        }
        return sb.toString();
    }

    public static void main(String[] args) {

    }

}
