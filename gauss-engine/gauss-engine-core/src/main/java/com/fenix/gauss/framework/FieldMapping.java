package com.fenix.gauss.framework;

import org.springframework.stereotype.Component;

import java.lang.annotation.*;

/**
 *
 * @author Chang Su
 * @since 4/03/2022
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Component
@Repeatable(Mappings.class)
public @interface FieldMapping {

    Class<?> scope();

    String[] fieldNames();
}
