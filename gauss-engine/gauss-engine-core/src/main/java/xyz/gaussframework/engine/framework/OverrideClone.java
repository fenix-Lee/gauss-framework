package xyz.gaussframework.engine.framework;

import org.springframework.stereotype.Component;

import java.lang.annotation.*;

/**
 *
 * @author Chang Su
 * @version 1.0
 * @since 7/5/2022
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Component
@Documented
public @interface OverrideClone {

    /**
     *
     *
     * @return true if cloneable function is enabled
     */
    boolean value() default true;
}
