package com.fenix.gauss.framework;

import org.springframework.stereotype.Component;

import java.lang.annotation.*;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface GaussConvertor {

    @Target({ElementType.FIELD, ElementType.METHOD})
    @Retention(RetentionPolicy.RUNTIME)
    @interface Role {
        String tag();
    }
}
