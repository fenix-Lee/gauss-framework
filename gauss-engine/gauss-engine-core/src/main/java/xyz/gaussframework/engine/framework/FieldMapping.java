package xyz.gaussframework.engine.framework;

import xyz.gaussframework.engine.infrastructure.DefaultProcessor;
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
    Class<?> processor() default DefaultProcessor.class;

    /**
     * which field should be used for conversion
     * @return the position of conversion implementation
     */
    String tag() default "default";
}
