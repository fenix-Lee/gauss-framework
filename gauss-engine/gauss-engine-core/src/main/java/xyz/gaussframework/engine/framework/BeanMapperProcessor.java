package xyz.gaussframework.engine.framework;

import xyz.gaussframework.engine.infrastructure.FieldEngine;
import xyz.gaussframework.engine.infrastructure.FieldMetaData;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.lang.NonNull;
import org.springframework.util.ObjectUtils;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * Mapping processor for all selected candidates if they are annotated with {@code Mapper} and/or {@code Mappers}
 *
 * @author Chang Su
 * @version 1.0
 * @see FieldMapping
 * @since 4/3/2022
 */
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
            fieldEngine.setFieldMaps(findAllFields(source));
            GaussBeanMapper.register(fieldEngine);
            return bean;
        }

        Arrays.stream(mappers.value())
                .forEach(m -> {
                    GaussFieldEngine fieldEngine = GaussFieldEngine.fireUp(source, m.target());
                    fieldEngine.setFieldMaps(findAllFields(source));
                    GaussBeanMapper.register(fieldEngine);
                });
        return bean;
    }

    /**
     * get all fields from source type
     * @param source source type
     * @return all fields including ones in super class
     */
    private Field[] findAllFields (Class<?> source) {
        List<Field> fields = Lists.newArrayList(source.getDeclaredFields());
        Class<?> superClass = source.getSuperclass();
        while (!ObjectUtils.isEmpty(superClass)) {
            fields.addAll(Lists.newArrayList(superClass.getDeclaredFields()));
            superClass = superClass.getSuperclass();
        }
        return fields.toArray(new Field[0]);
    }

    static class GaussFieldEngine implements FieldEngine {

        private final Class<?> sourceType;

        private final Class<?> targetType;

        protected Map<String, List<FieldMetaData<?>>> fieldMetaData;

        GaussFieldEngine(Class<?> sourceType, Class<?> targetType) {
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
            if (ObjectUtils.isEmpty(fields)) {
                return;
            }
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
                        .add(GaussFieldAnnotatedMetaData.create(fieldMapping.fieldNames(),
                                fieldMapping.processor(),
                                fieldMapping.tag()));
            } else {
                fieldMetaData.put(key,
                        Lists.newArrayList(GaussFieldAnnotatedMetaData
                                .create(fieldMapping.fieldNames(), fieldMapping.processor(), fieldMapping.tag())));
            }
        }
    }

    private static class GaussFieldAnnotatedMetaData<T> implements FieldMetaData<T> {

        private final String[] targetFields;

        private final Class<T> processorType;

        private final String tag;

        private GaussFieldAnnotatedMetaData(String[] targetFields, Class<T> processorType, String tag) {
            this.targetFields = targetFields;
            this.processorType = processorType;
            this.tag = tag;
            if (!processorType.equals(Object.class)) {
                GaussBeanMapper.registerConvertor(processorType, tag);
            }
        }

        static<T> GaussFieldAnnotatedMetaData<T> create (String[] targetFields, Class<T> processorType, String tag) {
            return new GaussFieldAnnotatedMetaData<>(targetFields, processorType, tag);
        }

        @Override
        public String[] getTargetFields() {
            return targetFields;
        }

        @Override
        public Class<T> getProcessorType() {
            return processorType;
        }

        @Override
        public String tag() {
            return tag;
        }
    }
}
