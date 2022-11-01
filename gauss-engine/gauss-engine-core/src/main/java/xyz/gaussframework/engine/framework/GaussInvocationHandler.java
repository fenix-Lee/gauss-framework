package xyz.gaussframework.engine.framework;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Map;

/**
 *
 * @author Chang Su
 * @version 2.1
 * @since 15/7/2022
 */
class GaussInvocationHandler extends AbstractGaussHandler implements InvocationHandler {

    private final Target<?> target;

    private final Map<String, GaussConversion<Object,Object>> dispatch;

    public GaussInvocationHandler(Target<?> target, Map<String, GaussConversion<Object,Object>> dispatch) {
        this.target = target;
        this.dispatch = dispatch;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) {
        if (method.getName().equals("getConvertor")) {
            return new Targeter.GaussConversionTargeter()
                    .target(new Target.GaussConversionTarget<>(ma.glasnost.orika.Converter.class,
                            String.join("#","customConvertor",target.name()),
                            dispatch.get(String.valueOf(args[0]))));
        }
        return objectMethod(method, args);
    }
}
