package xyz.gaussframework.engine.framework;

import java.lang.annotation.Target;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 *
 * @author Chang Su
 * @version 1.0
 * @since 7/5/2022
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface OverrideClone {

    /**
     *
     *
     * @return true if cloneable function is enabled
     */
    boolean value() default true;
}
