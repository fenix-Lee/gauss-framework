package xyz.gaussframework.engine.framework;

import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

abstract class AbstractGaussHandler {

    protected Object objectMethod(Method method, Object[] args) {
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
        return null;
    }
}
