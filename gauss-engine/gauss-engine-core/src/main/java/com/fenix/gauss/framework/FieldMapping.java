package com.fenix.gauss.framework;

import org.springframework.stereotype.Component;

import java.lang.annotation.*;
import java.util.function.Function;

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

    /**
     * setup for the result class type
     * @return The type of conversion class
     */
    Class<?> scope();

    /**
     *
     * @return for which specific fields needs to be mapped
     */
    String[] fieldNames();

    /**
     * for which two fields need to be converted by custom convertor
     * @return conversion processor
     */
    Class<?> processor();

    /**
     * mark of the specific function for conversion
     * @return the tag of conversion function
     */
    String tag();
}
