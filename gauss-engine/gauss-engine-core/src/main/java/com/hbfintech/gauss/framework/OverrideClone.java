package com.hbfintech.gauss.framework;

import org.springframework.stereotype.Component;

import java.lang.annotation.*;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Component
@Documented
public @interface OverrideClone {

    /**
     *
     *
     * @return true if cloneable function is enabled
     */
    boolean value() default true;
}
