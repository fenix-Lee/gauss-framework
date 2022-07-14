package xyz.gaussframework.engine.framework;

import org.springframework.beans.factory.config.BeanDefinition;

import java.lang.annotation.Target;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 *
 * @author Chang Su
 * @version 1.0
 * @see org.springframework.beans.factory.support.GenericBeanDefinition
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface GaussConvertor {
    /**
     * Alias for {@link BeanDefinition#getBeanClassName()}
     * @return bean name of this convertor
     */
    String name() default "";

    @Target({ElementType.FIELD, ElementType.METHOD})
    @Retention(RetentionPolicy.RUNTIME)
    @interface Role {
        String tag();
    }
}
