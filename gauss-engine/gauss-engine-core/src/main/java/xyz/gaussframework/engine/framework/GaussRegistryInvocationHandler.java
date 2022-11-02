package xyz.gaussframework.engine.framework;

import ma.glasnost.orika.MappingContext;
import ma.glasnost.orika.metadata.Type;
import org.springframework.util.Assert;
import org.springframework.util.ObjectUtils;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

class GaussRegistryInvocationHandler extends AbstractGaussHandler implements InvocationHandler {

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) {
        if (method.getName().equals("create")) {
            MappingContext context;
            Assert.isTrue(args.length == 2, "");
            Assert.isTrue(!ObjectUtils.isEmpty((context=(MappingContext) args[1])), "");
            return context.getResolvedDestinationType().getRawType().equals(Object.class)? new Object() :
                    GaussBeanFactory.getBean(context.getResolvedDestinationType().getRawType());
        }
        return objectMethod(method, args);
    }
}
