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

/**
 *
 * @author Chang Su
 * @version 3.0
 * @see InvocationHandlerFactory
 * @see GaussTargeter
 * @see GaussConversionTargeter
 * @since 2.0.0
 */
interface Targeter {

    <T> T target(Target<T> target);

    @SuppressWarnings("unchecked")
    default <T> T getProxyInstance(Target<T> target, InvocationHandler handler, Class<?>... others) {
        Class<?>[] types = new Class[others.length + 1];
        if (!ObjectUtils.isEmpty(others)) {
            System.arraycopy(others, 0, types, 0, others.length);
        }
        types[others.length] = target.type();
        return (T) Proxy.newProxyInstance(ClassUtils.getDefaultClassLoader(), types, handler);
    }

    /**
     *
     * @author Chang Su
     * @version 3.0
     * @since 2.0.0
     */
    class GaussTargeter implements Targeter {

        @Override
        public <T> T target(Target<T> target) {
            return newInstance(target);
        }

        private <T> T newInstance(Target<T> target) {
            Map<String, GaussConversion<Object,Object>> fieldMetadata = readConversionMetaData(target.type());
            InvocationHandler handler = InvocationHandlerFactory.create(target, fieldMetadata);
            return getProxyInstance(target, handler, GaussConversionFactory.GaussCustomConvertor.class);
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

    /**
     *
     * @author Chang Su
     * @version 3.0
     * @since 2.0.0
     */
    class GaussConversionTargeter implements Targeter {

        @Override
        public <T> T target(Target<T> target) {
            return getCustomConvertorProxy(target);
        }

        private <T> T getCustomConvertorProxy(Target<T> target) {
            InvocationHandler handler = InvocationHandlerFactory.create(target);
            return getProxyInstance(target, handler);
        }
    }
}
