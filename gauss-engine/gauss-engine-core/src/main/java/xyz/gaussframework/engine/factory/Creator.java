package xyz.gaussframework.engine.factory;

import org.springframework.stereotype.Component;
import xyz.gaussframework.engine.framework.OverrideClone;

import java.lang.annotation.*;

/**
 * indicated the class annotated with {@code Creator} as a factory for {@link xyz.gaussframework.engine.framework.Module}
 * removed {@link OverrideClone} since 2.4.0 and factories are no longer copied by {@link Cloneable}
 *
 * @author Chang Su
 * @version 2.0
 * @since 5/5/2022
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
@Component
public @interface Creator {

    /**
     * set true if wish to use original instance
     * @return whether it's a singleton or not
     */
    boolean isSingleton() default false;
}
