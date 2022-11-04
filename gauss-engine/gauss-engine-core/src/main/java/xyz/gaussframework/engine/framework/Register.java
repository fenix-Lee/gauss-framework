package xyz.gaussframework.engine.framework;

import org.springframework.context.annotation.Conditional;
import org.springframework.stereotype.Component;

import java.lang.annotation.*;
import java.lang.annotation.Target;

/**
 * This annotation indicates only for which object should be created by {@link GaussBeanFactory} or
 * {@link org.springframework.context.ApplicationContext} directly rather than default constructor
 *
 * @author Chang Su
 * @version 1.1
 * @since 29/8/2022
 */
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Component
public @interface Register {
}
