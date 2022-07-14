package xyz.gaussframework.engine.framework;

import com.google.common.collect.Maps;
import org.springframework.cglib.core.ReflectUtils;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.util.ReflectionUtils;
import xyz.gaussframework.engine.util.ClassValidator;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public interface Targeter {

    <T> T target(Target<T> target);

    class GaussTargeter implements Targeter {

        @Override
        public <T> T target(Target<T> target) {
            return newInstance(target);
        }

        private <T> T newInstance(Target<T> target) {
            // dynamic proxy
            Map<String, GaussConversion<?,?>> fieldMetadata = readConversionMetaData(target.type());
            return null;
        }

        private Map<String, GaussConversion<?,?>> readConversionMetaData(Class<?> convertorClass) {
            Field[] fields = convertorClass.getDeclaredFields();
            Assert.isTrue(!ObjectUtils.isEmpty(fields), "conversion function must be declared...");
            Map<String, GaussConversion<?,?>> fieldMetadata = Maps.newHashMap();
            Arrays.stream(fields)
                    .filter(f -> f.isAnnotationPresent(GaussConvertor.Role.class))
                    .forEach(f -> fieldMetadata.put(f.getAnnotation(GaussConvertor.Role.class).tag(),
                            (GaussConversion<?, ?>) ReflectionUtils.getField(f, null)));
            return fieldMetadata;
        }
    }
}
