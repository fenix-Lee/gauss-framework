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
 * @version 1.0
 * @since 4/3/2022
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Repeatable(Mappers.class)
public @interface Mapper {

    Class<?> target();
}
