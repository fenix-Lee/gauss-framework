package xyz.gaussframework.engine.factory;

import org.springframework.stereotype.Component;
import xyz.gaussframework.engine.framework.OverrideClone;

import java.lang.annotation.*;

/**
 *
 * @author Chang Su
 * @version 1.0
 * @since 5/5/2022
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
@Component
public @interface Creator {

    /**
     *
     * @return whether it's a singleton or not
     */
    boolean isSingleton() default false;
}
