package io.fxtahe.rpc.common.ext.annotation;


import io.fxtahe.rpc.common.ext.ExtensionClass;

import java.lang.annotation.*;

/**
 * {@link ExtensionClass}
 * @author fxtahe
 * @since 2022/8/18 17:01
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Inherited
@Documented
public @interface Extension {


    /**
     * extension name
     */
    String alias();

    /**
     * extension order
     */
    int order() default -1;

    /**
     * extension singleton
     */
    boolean singleton() default true;

    /**
     * extension group
     */
    String group() default "";
}
