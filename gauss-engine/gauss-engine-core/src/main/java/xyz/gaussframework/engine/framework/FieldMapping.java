package xyz.gaussframework.engine.framework;

import java.lang.annotation.Target;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Repeatable;

/**
 *
 * @author Chang Su
 * @since 4/03/2022
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
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
    Class<?> processor() default Object.class;

    /**
     * which field should be used for conversion
     * @return the position of conversion implementation
     */
    String tag() default "";
}
