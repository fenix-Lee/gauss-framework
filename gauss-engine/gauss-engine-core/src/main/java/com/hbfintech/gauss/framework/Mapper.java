package com.hbfintech.gauss.framework;

import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

import java.lang.annotation.*;

/**
 *
 * @author Chang Su
 * @version 1.0
 * @since 4/3/2022
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Component
@Validated
@Repeatable(Mappers.class)
public @interface Mapper {

    Class<?> target();
}
