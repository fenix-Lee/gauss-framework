package xyz.gaussframework.engine.framework;

import org.springframework.stereotype.Component;

import java.lang.annotation.*;

/**
 *
 * @author Chang Su
 * @since 10/03/2022
 */
@Target({ElementType.TYPE,ElementType.TYPE_USE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Component
@Inherited
@SuppressWarnings("unused")
public @interface Indicator {
    int value();
}
