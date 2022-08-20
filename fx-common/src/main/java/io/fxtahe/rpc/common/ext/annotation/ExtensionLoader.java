package io.fxtahe.rpc.common.ext.annotation;

import io.fxtahe.rpc.common.util.ClassUtil;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

/**
 * @author fxtahe
 * @since 2022/8/18 17:05
 */
public class ExtensionLoader<T> {


    private static final String LOAD_PATH = "fx-service";

    private final List<T> extensionInstances = new ArrayList<>();

    private final List<String> extensionNames = new ArrayList<>();


    public ExtensionLoader(Class<T> clazz){


    }


    public T getInstance(String name){

    }


    public List<T> getExtensions(){



    }


    private void loadExtensions(Class<T> clazz){
        ClassLoader classLoader = ClassUtil.getClassLoader(clazz);
        String name = clazz.getName();
        try {

            Enumeration<URL> urls = classLoader.getResources(LOAD_PATH.concat("/").concat(name));
            while (urls.hasMoreElements()) {
                URL url = urls.nextElement();
                try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(url.openStream()))) {
                    String line;
                    Class<?> aClass;
                    while((line = bufferedReader.readLine()) !=null){
                        aClass = ClassUtil.forName(line,classLoader);
                        if(aClass.isAnnotationPresent(Extension.class)){
                            Extension annotation = aClass.getAnnotation(Extension.class);
                            String value = annotation.alias();
                            if(extensionClasses.containsKey(value)){
                                throw new IllegalStateException("exist same alias extension for "+value+", make sure extension alias is unique");
                            }
                            boolean singleton = annotation.singleton();
                            int order = annotation.order();
                            ExtensionClass<?> extensionClass = new ExtensionClass<>(aClass, value,singleton,order);
                            extensionClasses.put(value,extensionClass);
                        }

                    }

                }catch (IOException e){

                } catch (ClassNotFoundException e) {



                }


            }

        }catch (IOException e){
            throw new IllegalArgumentException("Unable to load extensions from location ["+LOAD_PATH+"] with class "+name, e);
        }



    }


}
