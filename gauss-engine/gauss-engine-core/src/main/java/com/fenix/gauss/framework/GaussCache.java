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
@Component
public @interface GaussCache {

    String prefix() default "";

    /**
     * spring EL expression
     * @return custom key
     */
    String key() default "";
}
