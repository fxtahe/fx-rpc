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

import static java.lang.reflect.Modifier.*;

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
    private Set<String> interfaces;
    private Set<String> fields;
    private Set<String> constructors;
    private Set<String> methods;
    private final String className;

    public JavassistClass(ClassLoader classLoader, Class<?> neighbor) {
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
        className =  neighbor.getName() + "proxy" + proxyCount.getAndIncrement();
        ctClass = classPool.makeClass(className);

    }

    public Class<?> toClass(Class<?> neighbor, ProtectionDomain protectionDomain) {
        try {
            if (interfaces != null) {
                for (String interfaceName : interfaces) {
                    this.ctClass.addInterface(classPool.get(interfaceName));
                }
            }
            if(fields!=null){
                for(String field:fields){
                    this.ctClass.addField(CtField.make(field,ctClass));
                }
            }
            if (constructors != null) {
                for (String constructor : constructors) {
                    this.ctClass.addConstructor(CtNewConstructor.make(constructor, ctClass));
                }
            }
            this.ctClass.addConstructor(CtNewConstructor.defaultConstructor(ctClass));

            if(methods!=null){
                for(String method:methods){
                    this.ctClass.addMethod(CtNewMethod.make(method,ctClass));
                }
            }
            this.ctClass.writeFile(JavassistClass.class.getResource(".").getFile());
            return ctClass.toClass(classLoader,protectionDomain);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public void addInterface(Class<?> interfaceClass) {
        if (interfaces == null) {
            interfaces = new HashSet<>();
        }
        interfaces.add(interfaceClass.getName());
    }


    public void addMethod(String code, Method method) {
        if(methods==null){
            methods = new HashSet<>();
        }
        String modifier = modifier(method.getModifiers());
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
                sb.append(parameter.getType().getName()).append(" ").append(parameter.getName());
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


    public void addConstructor(int mod, Class<?>[] parameters, Class<?>[] exceptionThrows, String body) {
        if (constructors == null) {
            this.constructors = new HashSet<>();
        }
        StringBuilder sb = new StringBuilder(Modifier.toString(mod)).append(" ").append(ctClass.getSimpleName()).append("(");
        for(int i=0;i<parameters.length;i++){
            if(i>0){
                sb.append(",");
            }
            Class<?> parameter = parameters[i];
            sb.append(parameter.getName()).append(" ").append("arg").append(i);
        }
        sb.append(")");
        for(int i=0;i<exceptionThrows.length;i++){
            if(i>0){
                sb.append(",");
            }
            Class<?> exception = exceptionThrows[i];
            sb.append(exception.getName());
        }
        sb.append("{").append(body).append("}");
        constructors.add(sb.toString());
    }

    public void release(){
        if(interfaces!=null){
            interfaces.clear();
        }
        if(fields!=null){
            fields.clear();
        }
        if(constructors!=null){
            constructors.clear();
        }
        if(methods!=null){
            methods.clear();
        }

    }

    public static String modifier(int mod){
        StringBuilder sb = new StringBuilder();
        int len;
        if ((mod & PUBLIC) != 0)        sb.append("public ");
        if ((mod & PROTECTED) != 0)     sb.append("protected ");
        if ((mod & PRIVATE) != 0)       sb.append("private ");
        /* Canonical order */
        if ((mod & STATIC) != 0)        sb.append("static ");

        if ((len = sb.length()) > 0)    /* trim trailing space */
            return sb.toString().substring(0, len-1);
        return "";
    }

}
