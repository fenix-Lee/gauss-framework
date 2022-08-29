package xyz.gaussframework.engine.framework;

import org.springframework.stereotype.Component;

import java.lang.annotation.*;
import java.lang.annotation.Target;

/**
 * This annotation indicates only for which object should be created by {@link GaussBeanFactory} or
 * {@link org.springframework.context.ApplicationContext} directly rather than default constructor
 *
 * @author Chang Su
 * @version 1.0
 * @since 29/8/2022
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Component
public @interface Register {
}
