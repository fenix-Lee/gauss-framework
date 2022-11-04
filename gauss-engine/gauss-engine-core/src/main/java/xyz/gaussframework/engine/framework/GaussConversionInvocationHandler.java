package xyz.gaussframework.engine.framework;

import ma.glasnost.orika.Converter;
import ma.glasnost.orika.metadata.Type;
import org.springframework.util.Assert;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

class GaussConversionInvocationHandler implements InvocationHandler {

    private final Target<?> target;


    private static final Object EMPTY = new Object();

    public GaussConversionInvocationHandler (Target<?> target) {
        this.target = target;
    }

    @Override
    @SuppressWarnings("unchecked")
    public Object invoke(Object proxy, Method method, Object[] args) {
        switch (method.getName()) {
            case "canConvert":
                return true;
            case "convert":
                Assert.isTrue(args.length >= 2, "parameters are invalid.....");
                if (target instanceof Target.GaussConversionTarget) {
                    return ((Target.GaussConversionTarget<Converter<Object,Object>>) target)
                            .getConversion()
                            .convert(args[0], (Type<Object>) args[1]);
                }
                return EMPTY;
            case "setMapperFacade":
                return EMPTY;
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
        return EMPTY;
    }
}
