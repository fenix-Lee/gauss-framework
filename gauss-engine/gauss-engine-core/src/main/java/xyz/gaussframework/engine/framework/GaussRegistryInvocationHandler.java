package xyz.gaussframework.engine.framework;

import ma.glasnost.orika.metadata.Type;
import org.springframework.util.Assert;
import org.springframework.util.ObjectUtils;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

class GaussRegistryInvocationHandler extends AbstractGaussHandler implements InvocationHandler {

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) {
        if (method.getName().equals("create")) {
            Type<?> targetType;
            Assert.isTrue(args.length == 2, "");
            Assert.isTrue(!ObjectUtils.isEmpty((targetType=(Type<?>) args[1])), "");
            return GaussBeanFactory.getBean(targetType.getRawType());
        }
        return objectMethod(method, args);
    }
}
