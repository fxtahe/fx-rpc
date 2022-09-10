package io.fxtahe.rpc.common.ext;

import com.google.common.collect.Lists;
import io.fxtahe.rpc.common.ext.annotation.Extension;
import io.fxtahe.rpc.common.util.ClassUtil;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * @author fxtahe
 * @since 2022/8/18 17:05
 */
public class ExtensionLoader<T> {


    private static final String LOAD_PATH = "fx-service";

    private final Class<T> extensionClass;

    private List<String> extensionClassNames;

    private Map<String, ExtensionClass<T>> cache;


    public ExtensionLoader(Class<T> extensionClass) {
        this.extensionClass = extensionClass;
        cache = loadExtensions(extensionClass);
        this.extensionClassNames = Lists.newArrayList(cache.keySet());
    }


    public T getInstance(String name) {
        ExtensionClass<T> extensionClass = cache.get(name);
        if (extensionClass == null) {
            return null;
        }
        return extensionClass.getInstance();
    }


    public List<T> getExtensions() {
        return cache.values().stream().sorted(Comparator.comparing(ExtensionClass::getOrder)).map(ExtensionClass::getInstance).collect(Collectors.toList());
    }

    public List<T> getExtensionsGroup(String group){
        return cache.values().stream().filter(el->el.getGroup().equals(group)).sorted(Comparator.comparing(ExtensionClass::getOrder)).map(ExtensionClass::getInstance).collect(Collectors.toList());
    }


    private Map<String, ExtensionClass<T>> loadExtensions(Class<T> clazz) {
        ClassLoader classLoader = ClassUtil.getClassLoader(clazz);
        String name = clazz.getName();
        try {
            Enumeration<URL> urls = classLoader.getResources(LOAD_PATH.concat("/").concat(name));
            Map<String, ExtensionClass<T>> extensionClasses = new ConcurrentHashMap<>(8);
            while (urls.hasMoreElements()) {
                URL url = urls.nextElement();
                    try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(url.openStream()))) {
                        String line;
                        Class<T> aClass;
                        while ((line = bufferedReader.readLine()) != null) {
                            aClass = (Class<T>) ClassUtil.forName(line, classLoader);
                            if (aClass.isInterface()) {
                                throw new RuntimeException("extension class must not be interface, class name " + line);
                            }
                            if (!clazz.isAssignableFrom(aClass)) {
                                throw new RuntimeException(line + " class not assign from " + name);
                            }
                            // only Extension annotation exist to processing
                            if (aClass.isAnnotationPresent(Extension.class)) {
                                Extension annotation = aClass.getAnnotation(Extension.class);
                                String alias = annotation.alias();
                                if (extensionClasses.containsKey(alias)) {
                                    throw new RuntimeException("exist same alias extension for " + alias + ", make sure extension alias is unique");
                                }
                                boolean singleton = annotation.singleton();
                                int order = annotation.order();
                                String group = annotation.group();
                                ExtensionClass<T> extensionClass = new ExtensionClass<T>(alias, order, aClass, singleton,group);
                                extensionClasses.put(alias, extensionClass);
                            }
                        }
                    } catch (IOException | ClassNotFoundException ex) {
                        throw new IllegalArgumentException("load extensions error from location [" + LOAD_PATH + "] with class " + name, ex);
                    }
            }
            return extensionClasses;
        } catch (IOException e) {
            throw new IllegalArgumentException("Unable to load extensions from location [" + LOAD_PATH + "] with class " + name, e);
        }
    }



}
