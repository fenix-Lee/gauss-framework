package com.fenix.gauss.framework;

import org.springframework.stereotype.Component;

import java.lang.annotation.*;

/**
 *
 *
 * @author Chang Su
 * @version 1.0
 * @since 8/7/2022
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface GaussCache {

    /**
     * customize the prefix of cache key
     * @return prefix of cache key
     */
    String prefix() default "";

    /**
     * springEL expression
     * @return custom key
     */
    String key() default "";
}
