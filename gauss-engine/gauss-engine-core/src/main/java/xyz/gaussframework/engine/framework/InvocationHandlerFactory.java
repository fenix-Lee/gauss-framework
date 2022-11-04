package xyz.gaussframework.engine.framework;

import java.lang.reflect.InvocationHandler;
import java.util.Map;

/**
 *
 * @author Chang Su
 * @version 3.0
 * @since 2.0.0
 */
interface InvocationHandlerFactory {

    static InvocationHandler createDispatcherHandler(Target<?> target, Map<String,
            GaussConversion<Object,Object>> dispatch) {
        return new GaussInvocationHandler(target, dispatch);
    }

    static InvocationHandler createConversionHandler(Target<?> target) {
        return new GaussConversionInvocationHandler(target);
    }

    static InvocationHandler createRegistryHandler() {
        return new GaussRegistryInvocationHandler();
    }
}
