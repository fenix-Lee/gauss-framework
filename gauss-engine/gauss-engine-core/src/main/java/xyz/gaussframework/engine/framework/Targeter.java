package xyz.gaussframework.engine.framework;

import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;
import org.springframework.util.ObjectUtils;
import xyz.gaussframework.engine.util.ClassValidator;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.HashSet;
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
            return null;
        }

        private Set<String> getTags(String className) {
            Class<?> convertorClass;
            try {
                convertorClass = ClassUtils.forName(className, ClassUtils.getDefaultClassLoader());
            } catch (ClassNotFoundException e) {
                return null;
            }
            if (ClassValidator.ClassTypeValidation(convertorClass,
                    "ma.glasnost.orika.Converter")) {
                return new HashSet<String>(){{add(convertorClass.getName());}};
            }
            Field[] fields = convertorClass.getDeclaredFields();
            Assert.isTrue(!ObjectUtils.isEmpty(fields), "conversion function must be declared...");
            Set<String> tags = new HashSet<>(fields.length);
            Arrays.stream(fields)
                    .filter(f -> f.isAnnotationPresent(GaussConvertor.Role.class))
                    .forEach(f -> tags.add(f.getAnnotation(GaussConvertor.Role.class).tag()));
            return tags;
        }
    }
}
