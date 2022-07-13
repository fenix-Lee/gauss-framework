package xyz.gaussframework.engine.framework;

import org.springframework.stereotype.Component;

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
@Repeatable(Mappers.class)
public @interface Mapper {

    Class<?> target();
}
