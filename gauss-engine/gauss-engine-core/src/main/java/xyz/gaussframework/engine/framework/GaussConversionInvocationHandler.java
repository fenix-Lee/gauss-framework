package xyz.gaussframework.engine.framework;

import ma.glasnost.orika.metadata.Type;
import org.springframework.util.Assert;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

class GaussConversionInvocationHandler implements InvocationHandler {

    private final Target<?> target;

    public GaussConversionInvocationHandler (Target<?> target) {
        this.target = target;
    }

    @Override
    @SuppressWarnings("unchecked")
    public Object invoke(Object proxy, Method method, Object[] args) {
        String methodName = method.getName();
        switch (methodName) {
            case "canConvert":
                return true;
            case "convert":
                Assert.isTrue(args.length >= 2, "parameters are invalid.....");
                if (target instanceof GaussConversionTarget) {
                    return ((GaussConversionTarget<ma.glasnost.orika.Converter<Object,Object>>) target)
                            .getConversion()
                            .convert(args[0], (Type<Object>) args[1]);
                }
                return null;
            case "setMapperFacade":
                return null;
            case "equals":
                try {
                    Object otherHandler =
                            args.length > 0 && args[0] != null ? Proxy.getInvocationHandler(args[0]) : null;
                    return equals(otherHandler);
                } catch (IllegalArgumentException e) {
                    return false;
                }
            case "hashCode":
                return hashCode();
            case "toString":
                return toString();
        }
        return null;
    }
}
