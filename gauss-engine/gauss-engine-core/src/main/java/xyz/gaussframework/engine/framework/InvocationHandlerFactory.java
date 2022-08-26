package xyz.gaussframework.engine.framework;

import java.lang.reflect.InvocationHandler;
import java.util.Map;

/**
 *
 * @author Chang Su
 * @version 3.0
 * @since 2.0.0
 */
public interface InvocationHandlerFactory {

    static InvocationHandler create(Target<?> target, Map<String, GaussConversion<Object,Object>> dispatch) {
        return new GaussInvocationHandler(target, dispatch);
    }

    static InvocationHandler create(Target<?> target) {
        return new GaussConversionInvocationHandler(target);
    }
}
