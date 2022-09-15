package io.fxtahe.rpc.common.util;

import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.util.*;

/**
 * @author fxtahe
 * @since 2022/8/18 15:22
 */
public class ClassUtil {


    /**
     * Suffix for array class names: "[]"
     */
    public static final String ARRAY_SUFFIX = "[]";

    /**
     * Prefix for internal array class names: "["
     */
    private static final String INTERNAL_ARRAY_PREFIX = "[";

    /**
     * Prefix for internal non-primitive array class names: "[L"
     */
    private static final String NON_PRIMITIVE_ARRAY_PREFIX = "[L";

    /**
     * 原始类型
     */
    private static final Map<String, Class<?>> primitiveTypeNameMap = new HashMap<>(32);

    /**
     * 缓存
     */
    private static final Map<String, Class<?>> commonClassCache = new HashMap<>(64);

    static {
        primitiveTypeNameMap.put(boolean.class.getName(), boolean.class);
        primitiveTypeNameMap.put(byte.class.getName(), byte.class);
        primitiveTypeNameMap.put(short.class.getName(), short.class);
        primitiveTypeNameMap.put(int.class.getName(), int.class);
        primitiveTypeNameMap.put(long.class.getName(), long.class);
        primitiveTypeNameMap.put(float.class.getName(), float.class);
        primitiveTypeNameMap.put(double.class.getName(), double.class);
        primitiveTypeNameMap.put(char.class.getName(), char.class);


        registerCommonClasses(Boolean[].class, Byte[].class, Character[].class, Double[].class,
                Float[].class, Integer[].class, Long[].class, Short[].class);
        registerCommonClasses(Number.class, Number[].class, String.class, String[].class,
                Class.class, Class[].class, Object.class, Object[].class);
        registerCommonClasses(Throwable.class, Exception.class, RuntimeException.class,
                Error.class, StackTraceElement.class, StackTraceElement[].class);
        registerCommonClasses(Enum.class, Iterable.class, Iterator.class, Enumeration.class,
                Collection.class, List.class, Set.class, Map.class, Map.Entry.class, Optional.class);
    }
    public static Class<?> forName(String name) throws ClassNotFoundException {
        return forName(name,null);
    }

    public static Class<?> forName(String name, ClassLoader classLoader) throws ClassNotFoundException {
        if (name == null) {
            throw new IllegalArgumentException("name must not be null");
        }
        Class<?> result = commonClassCache.get(name);
        if (result != null) {
            return result;
        }
        result = resolvePrimitiveClassName(name);
        if (result != null) {
            return result;
        }

        // "java.lang.String[]" style arrays
        if (name.endsWith(ARRAY_SUFFIX)) {
            String elementClassName = name.substring(0, name.length() - ARRAY_SUFFIX.length());
            Class<?> elementClass = forName(elementClassName, classLoader);
            return Array.newInstance(elementClass, 0).getClass();
        }

        // "[Ljava.lang.String;" style arrays
        if (name.startsWith(NON_PRIMITIVE_ARRAY_PREFIX) && name.endsWith(";")) {
            String elementName = name.substring(NON_PRIMITIVE_ARRAY_PREFIX.length(), name.length() - 1);
            Class<?> elementClass = forName(elementName, classLoader);
            return Array.newInstance(elementClass, 0).getClass();
        }

        // "[[I" or "[[Ljava.lang.String;" style arrays
        if (name.startsWith(INTERNAL_ARRAY_PREFIX)) {
            String elementName = name.substring(INTERNAL_ARRAY_PREFIX.length());
            Class<?> elementClass = forName(elementName, classLoader);
            return Array.newInstance(elementClass, 0).getClass();
        }

        if (classLoader == null) {
            classLoader = getClassLoader();
        }
        return (classLoader != null ? classLoader.loadClass(name) : Class.forName(name));

    }

    public static Class<?> resolvePrimitiveClassName(String name) {
        Class<?> result = null;
        if (name != null && name.length() <= 8) {
            result = primitiveTypeNameMap.get(name);
        }
        return result;
    }

    public static <T> T newInstance(Class<T> clazz) {
        return newInstance(clazz,null,null);
    }


    @SuppressWarnings(value = "unchecked")
    public static <T> T newInstance(Class<T> clazz, Class<?>[] parameterTypes, Object[] parameters) {
        if (clazz.isInterface()) {
            throw new IllegalArgumentException("class must not be interface");
        }
        if (clazz.isPrimitive()) {
            return (T) getDefaultPrimitiveValue(clazz);
        }
        try{
            Constructor<T> ctor = null;
            ctor = clazz.getDeclaredConstructor(parameterTypes);

            if ((!Modifier.isPublic(ctor.getModifiers()) ||
                    !Modifier.isPublic(ctor.getDeclaringClass().getModifiers()))
                    && !ctor.isAccessible()) {
                ctor.setAccessible(true);
            }
            return ctor.newInstance(parameters);
        }catch (NoSuchMethodException | IllegalAccessException | InstantiationException | InvocationTargetException ex){
            ex.printStackTrace();
        }

        return null;
    }

    public static Object getDefaultPrimitiveValue(Class<?> clazz) {
        if (clazz == int.class) {
            return 0;
        } else if (clazz == boolean.class) {
            return false;
        } else if (clazz == long.class) {
            return 0L;
        } else if (clazz == byte.class) {
            return (byte) 0;
        } else if (clazz == double.class) {
            return 0d;
        } else if (clazz == short.class) {
            return (short) 0;
        } else if (clazz == float.class) {
            return 0f;
        } else if (clazz == char.class) {
            return (char) 0;
        } else {
            return null;
        }
    }

    private static void registerCommonClasses(Class<?>... commonClasses) {
        for (Class<?> clazz : commonClasses) {
            commonClassCache.put(clazz.getName(), clazz);
        }
    }

    /**
     * @return ClassLoader
     */
    public static ClassLoader getClassLoader() {
        return getClassLoader(ClassUtil.class);
    }

    /**
     * @param clazz
     * @return ClassLoader
     */
    public static ClassLoader getClassLoader(Class<?> clazz) {
        ClassLoader classLoader = null;
        try {
            classLoader = Thread.currentThread().getContextClassLoader();
        } catch (Exception e) {
            //ignore
        }
        if (classLoader == null) {
            classLoader = clazz.getClassLoader();
        }
        if (classLoader == null) {
            classLoader = ClassLoader.getSystemClassLoader();
        }
        return classLoader;
    }


    public static String getDesc(Class<?>[] classes){
        StringBuilder stringBuilder = new StringBuilder();
        for(Class<?> clazz:classes){
            stringBuilder.append(clazz.getName()).append(",");
        }
        String result = stringBuilder.toString();
        return result.substring(0, result.length() - 1);
    }




}
