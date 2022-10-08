package io.fxtahe.rpc.common.proxy;

import javassist.*;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Parameter;
import java.lang.reflect.Type;
import java.security.ProtectionDomain;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author fxtahe
 * @since 2022/10/8 15:10
 */
public class JavassistClass {

    public static final AtomicInteger proxyCount = new AtomicInteger(0);
    private static final Map<ClassLoader, ClassPool> cache = new ConcurrentHashMap<>();

    private ClassLoader classLoader;

    private ClassPool classPool;

    private CtClass ctClass;

    private Set<Class<?>> interfaces;

    private Set<String> fields;
    private Set<String> constructors;
    private Set<String> methods;


    public JavassistClass(ClassLoader classLoader) {
        if (classLoader == null) {
            this.classPool = ClassPool.getDefault();
            this.classLoader = classPool.getClassLoader();
        } else {
            this.classPool = cache.computeIfAbsent(classLoader, (key) -> {
                ClassPool pool = new ClassPool(true);
                pool.insertClassPath(new LoaderClassPath(key));
                return pool;
            });
        }
    }

    public Class<?> toClass(Class<?> neighbor, ProtectionDomain protectionDomain) {
        ctClass.setName(neighbor.getName() + "$proxy" + proxyCount.getAndIncrement());
        if(constructors!=null){
            for(String constructor:constructors){
                ctClass.addConstructor(new CtConstructor());
            }
        }
        if (interfaces != null) {
            for (Class<?> interfaceName : interfaces) {

                ctClass.addInterface(classPool.get(interfaceName.getName()));
            }

        }


        try {
            return classPool.toClass(ctClass, neighbor, classLoader, protectionDomain);
        } catch (CannotCompileException e) {
            throw new RuntimeException(e);
        }

    }

    public void addInterface(Class<?> interfaceClass) {
        if (interfaces == null) {
            interfaces = new HashSet<>();
        }
        interfaces.add(interfaceClass);
    }


    public void addMethod(String code, Method method) {

        String modifier = Modifier.toString(method.getModifiers());

        Type genericReturnType = method.getGenericReturnType();
        String typeName = genericReturnType.getTypeName();
        StringBuilder sb = new StringBuilder(modifier).append(" ").append(typeName).append(" ")
                .append(method.getName()).append("(");
        Parameter[] parameters = method.getParameters();
        if (parameters.length > 0) {
            for (int i = 0; i < parameters.length; i++) {
                if (i > 0) {
                    sb.append(",");
                }
                Parameter parameter = parameters[i];
                sb.append(parameter.getType().getName()).append(" ").append(parameter.getName()).append(",");
            }
        }
        sb.append(")");
        Type[] exceptionTypes = method.getGenericExceptionTypes();
        if (exceptionTypes.length > 0) {
            sb.append("throws ");
            for (int i = 0; i < exceptionTypes.length; i++) {
                if (i > 0) {
                    sb.append(",");
                }
                Type exceptionType = exceptionTypes[i];
                sb.append(exceptionType.getTypeName());
            }
        }
        sb.append("{").append(code).append("}");
        if (fields == null) {
            methods = new HashSet<>();
        }
        methods.add(sb.toString());
    }

    public void addField(String filed) {
        if (fields == null) {
            fields = new HashSet<>();
        }
        fields.add(filed);
    }


    public void addConstructor(String constructor) {
        if (constructors == null) {
            this.constructors = new HashSet<>();
        }
        constructors.add(constructor);
    }


    public static void main(String[] args) throws NoSuchMethodException {

        Method addMethod = JavassistClass.class.getMethod("toClass", Class.class, ProtectionDomain.class);
        Type[] genericParameterTypes = addMethod.getGenericParameterTypes();
        Parameter[] parameters = addMethod.getParameters();
        for (Parameter parameter : parameters) {
            System.out.println(parameter.getType().getName());
            System.out.println(parameter.getName());

        }
        for (Type type : genericParameterTypes) {

            System.out.println(type.getTypeName());

        }


    }

}
