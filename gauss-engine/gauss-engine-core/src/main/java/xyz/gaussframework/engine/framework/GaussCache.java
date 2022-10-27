package xyz.gaussframework.engine.framework;

import java.lang.annotation.Target;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Annotation for local cache to store method return-value for 30 seconds by default(may change within this annotation)
 * remember this annotation cannot guarantee data consistence
 *
 * @author Chang Su
 * @version 1.0
 * @since 8/7/2022
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface GaussCache {

    /**
     * customize the prefix of cache key
     * @return prefix of cache key
     */
    String prefix() default "";

    /**
     * springEL expression
     * @return custom key
     */
    String key() default "";

    /**
     * for how long gauss engine hold the result
     * @return delay time
     */
    long expire() default 30000;
}
