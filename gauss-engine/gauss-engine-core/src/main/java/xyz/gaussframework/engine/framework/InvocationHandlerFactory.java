package xyz.gaussframework.engine.framework;

import java.lang.reflect.InvocationHandler;
import java.util.Map;

public interface InvocationHandlerFactory {

    InvocationHandler create(Target<?> target, Map<String, GaussConversion<Object,Object>> dispatch);

    final class GaussDefaultHandlerFactory implements InvocationHandlerFactory {

        @Override
        public InvocationHandler create(Target<?> target, Map<String, GaussConversion<Object,Object>> dispatch) {
            return new GaussInvocationHandler(target, dispatch);
        }

        public InvocationHandler create(Target<?> target) {
            return new GaussConversionInvocationHandler<>(target);
        }
    }
}
