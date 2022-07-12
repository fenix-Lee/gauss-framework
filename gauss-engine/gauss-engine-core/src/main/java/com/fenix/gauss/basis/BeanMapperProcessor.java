package com.fenix.gauss.basis;

import com.fenix.gauss.framework.*;
import com.fenix.gauss.infrastructure.DefaultProcessor;
import com.fenix.gauss.infrastructure.FieldEngine;
import com.fenix.gauss.infrastructure.FieldMetaData;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Table;
import com.google.common.collect.Tables;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 *
 *
 * @author Chang Su
 * @version 1.0
 * @see FieldMapping
 * @since 4/3/2022
 */
@Component
public class BeanMapperProcessor implements BeanPostProcessor {

    @Override
    public Object postProcessAfterInitialization(Object bean,
                                                 @NonNull String beanName) throws BeansException {
        Class<?> source; Mappers mappers; Mapper mapper;
        if (ObjectUtils.isEmpty((mappers = AnnotationUtils
                .findAnnotation((source = bean.getClass()), Mappers.class)))) {
            if (ObjectUtils.isEmpty((mapper = AnnotationUtils.findAnnotation(source, Mapper.class)))) {
                return bean;
            }
            GaussFieldEngine fieldEngine = GaussFieldEngine.fireUp(source, mapper.target());
            fieldEngine.setFieldMaps(source.getDeclaredFields());
            GaussBeanMapper.addFieldEngine(fieldEngine);
            return bean;
        }

        Arrays.stream(mappers.value())
                .forEach(m -> {
                    GaussFieldEngine fieldEngine = GaussFieldEngine.fireUp(source, m.target());
                    fieldEngine.setFieldMaps(source.getDeclaredFields());
                    GaussBeanMapper.addFieldEngine(fieldEngine);
                });
        return bean;
    }

    private static class GaussFieldEngine implements FieldEngine {

        private final Class<?> sourceType;

        private final Class<?> targetType;

        private Map<String, List<FieldMetaData<?>>> fieldMetaData;

        private GaussFieldEngine(Class<?> sourceType, Class<?> targetType) {
            this.sourceType = sourceType;
            this.targetType = targetType;
        }

        @Override
        public Class<?> getSourceType () {
            return sourceType;
        }

        @Override
        public Class<?> getTargetType () {
            return targetType;
        }

        @Override
        public Map<String, List<FieldMetaData<?>>> getFieldAnnotatedMetaData() {
            return fieldMetaData;
        }

        static GaussFieldEngine fireUp (Class<?> sourceType, Class<?> targetType) {
            return new GaussFieldEngine(sourceType, targetType);
        }

        void setFieldMaps(Field[] fields) {
            fieldMetaData = Maps.newHashMapWithExpectedSize(fields.length);
            Arrays.stream(fields).forEach(f -> {
                Mappings mappings; FieldMapping fieldMapping;
                if (ObjectUtils.isEmpty(mappings = f.getAnnotation(Mappings.class))) {
                    if (ObjectUtils.isEmpty(fieldMapping = f.getAnnotation(FieldMapping.class))) {
                        return;
                    }
                    capFieldMaps(f.getName(), targetType, fieldMapping);
                    return;
                }
                capFieldMaps(f.getName(), targetType, mappings.value());
            });
        }

        private void capFieldMaps(String key, Class<?> targetClazz,
                                  FieldMapping... fieldMappings) {
            Arrays.stream(fieldMappings)
                    .filter(f -> f.scope().equals(targetClazz))
                    .forEach(f -> buildFieldMetaData(key, f));
        }

        private void buildFieldMetaData(String key, FieldMapping fieldMapping) {
            if (fieldMetaData.containsKey(key)) {
                fieldMetaData.computeIfAbsent(key, k -> Lists.newArrayList());
                fieldMetaData.get(key)
                        .add(GaussFieldAnnotatedMetaData.create(fieldMapping.fieldNames(), fieldMapping.processor()));
            } else {
                fieldMetaData.put(key,
                        Lists.newArrayList(GaussFieldAnnotatedMetaData
                                .create(fieldMapping.fieldNames(), fieldMapping.processor())));
            }
        }
    }

    private static class GaussFieldAnnotatedMetaData<T> implements FieldMetaData<T> {

        private final String[] targetFields;

        private final Class<T> processorType;

        private GaussFieldAnnotatedMetaData(String[] targetFields, Class<T> processorType) {
            this.targetFields = targetFields;
            this.processorType = processorType;
            if (!processorType.equals(DefaultProcessor.class)) {
                GaussBeanMapper.addProcessor(processorType);
            }
        }

        static<T> GaussFieldAnnotatedMetaData<T> create (String[] targetFields, Class<T> processorType) {
            return new GaussFieldAnnotatedMetaData<>(targetFields, processorType);
        }

        @Override
        public String[] getTargetFields() {
            return targetFields;
        }

        @Override
        public Class<T> getProcessorType() {
            return processorType;
        }
    }
}
