package com.hbfintech.gauss.factory;

import org.springframework.stereotype.Component;

import java.lang.annotation.*;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Component
public @interface Creator {

    boolean isSingleton() default false;
}
