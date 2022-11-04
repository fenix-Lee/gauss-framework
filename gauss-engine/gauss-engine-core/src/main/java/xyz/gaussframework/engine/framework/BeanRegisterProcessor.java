package xyz.gaussframework.engine.framework;

import com.google.common.collect.Lists;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.lang.NonNull;
import org.springframework.util.ObjectUtils;
import xyz.gaussframework.engine.infrastructure.FieldEngine;
import xyz.gaussframework.engine.infrastructure.FieldMetaData;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;

public class BeanRegisterProcessor implements BeanPostProcessor {

    @Override
    public Object postProcessAfterInitialization(@NonNull Object bean,
                                                 @NonNull String beanName) throws BeansException {
        Arrays.stream(bean.getClass().getDeclaredFields())
                .forEach(f -> {
                    if (f.isAnnotationPresent(Register.class)) {
                        GaussBeanMapper.registerConvertor(DefaultRegistryConvertor.class, "default");
                        GaussRegistryFieldEngine fieldEngine = GaussRegistryFieldEngine.fireUp(bean.getClass(), bean.getClass());
                        fieldEngine.setFieldMaps(f.getName());
                        GaussBeanMapper.register(fieldEngine);
                    }
                });
        return bean;
    }

    private static class GaussRegistryFieldEngine extends BeanMapperProcessor.GaussFieldEngine {

        GaussRegistryFieldEngine(Class<?> sourceType, Class<?> targetType) {
            super(sourceType, targetType);
        }

        static GaussRegistryFieldEngine fireUp(Class<?> sourceType, Class<?> targetType) {
            return new GaussRegistryFieldEngine(sourceType, targetType);
        }

        void setFieldMaps(final String fieldName) {
            fieldMetaData = new HashMap<>();
            fieldMetaData.put(fieldName, Lists.newArrayList(new FieldMetaData<DefaultRegistryConvertor>() {
                @Override
                public String[] getTargetFields() {
                    return new String[]{fieldName};
                }

                @Override
                public Class<DefaultRegistryConvertor> getProcessorType() {
                    return DefaultRegistryConvertor.class;
                }

                @Override
                public String tag() {
                    return "default";
                }
            }));
        }
    }

}
