package xyz.gaussframework.engine.framework;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Map;

class GaussInvocationHandler implements InvocationHandler {

    private final Target<?> target;

    private final Map<String, GaussConversion<Object,Object>> dispatch;

    public GaussInvocationHandler(Target<?> target, Map<String, GaussConversion<Object,Object>> dispatch) {
        this.target = target;
        this.dispatch = dispatch;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) {
        String methodName = method.getName();
        switch (methodName) {
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
        if (methodName.equals("getConvertor")) {
            return new Targeter.GaussConversionTargeter()
                    .target(new GaussConversionTarget<>(ma.glasnost.orika.Converter.class,
                            String.join("#","customConvertor",target.name()),
                            dispatch.get(String.valueOf(args[0]))));
        }
        return null;
    }
}
