package xyz.gaussframework.engine.framework;

import org.springframework.stereotype.Component;

import java.lang.annotation.Target;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Repeatable;

/**
 * This annotation indicates meta information of which module is being used under specific chain class.
 * Multiple-chain are allowed by {@link Chains} annotation.
 *
 * Remember that the sequence field is also mandatory and DO NOT use same order between modules in same
 * factory class
 *
 * @author Chang Su
 * @see Chains
 * @since 2020/7/16
 *
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Component
@Repeatable(Chains.class)
public @interface Chain {

    /**
     * indicates modules by which specific chain class are used. If this module is referred to
     * other chain class, multiple annotations should be claimed on top of module class.
     * @return chain class
     */
    Class<?> factory();

    /**
     * indicates modules under which position are used
     * @return module rank
     */
    int sequence() default 0;
}
