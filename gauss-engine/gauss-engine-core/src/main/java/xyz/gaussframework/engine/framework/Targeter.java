package xyz.gaussframework.engine.framework;

import com.google.common.collect.Maps;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;
import java.util.Arrays;
import java.util.Map;

interface Targeter {

    <T> T target(Target<T> target);

    @SuppressWarnings("unchecked")
    default <T> T getProxyInstance(Target<T> target, InvocationHandler handler) {
        return (T) Proxy.newProxyInstance(ClassUtils.getDefaultClassLoader(),
                new Class[]{target.type()}, handler);
    }

    class GaussTargeter implements Targeter {

        private final InvocationHandlerFactory factory = new InvocationHandlerFactory.GaussDefaultHandlerFactory();

        @Override
        public <T> T target(Target<T> target) {
            return newInstance(target);
        }

        private <T> T newInstance(Target<T> target) {
            Map<String, GaussConversion<Object,Object>> fieldMetadata = readConversionMetaData(target.type());
            InvocationHandler handler = factory.create(target, fieldMetadata);
            return getProxyInstance(target, handler);
        }

        @SuppressWarnings("unchecked")
        private Map<String, GaussConversion<Object,Object>> readConversionMetaData(Class<?> convertorClass) {
            Field[] fields = convertorClass.getDeclaredFields();
            Assert.isTrue(!ObjectUtils.isEmpty(fields), "conversion function must be declared...");
            Map<String, GaussConversion<Object,Object>> fieldMetadata = Maps.newHashMap();
            Arrays.stream(fields)
                    .filter(f -> f.isAnnotationPresent(GaussConvertor.Role.class))
                    .forEach(f -> fieldMetadata.put(f.getAnnotation(GaussConvertor.Role.class).tag(),
                            (GaussConversion<Object, Object>) ReflectionUtils.getField(f, null)));
            return fieldMetadata;
        }
    }

    class GaussConversionTargeter implements Targeter {

        private final InvocationHandlerFactory factory = new InvocationHandlerFactory.GaussDefaultHandlerFactory();

        @Override
        public <T> T target(Target<T> target) {
            return getCustomConvertorProxy(target);
        }

        private <T> T getCustomConvertorProxy(Target<T> target) {
            InvocationHandler handler = ((InvocationHandlerFactory.GaussDefaultHandlerFactory)factory)
                    .create(target);
            return getProxyInstance(target, handler);
        }
    }
}
