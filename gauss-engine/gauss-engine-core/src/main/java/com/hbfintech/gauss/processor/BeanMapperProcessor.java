package com.hbfintech.gauss.processor;

import com.hbfintech.gauss.framework.FieldMapping;
import com.hbfintech.gauss.framework.Mapper;
import com.hbfintech.gauss.framework.Mappers;
import com.hbfintech.gauss.framework.Mappings;
import com.hbfintech.gauss.util.BeanMapper;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.annotation.DependsOn;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@Component
public class BeanMapperProcessor implements BeanPostProcessor {

    @Override
    public Object postProcessAfterInitialization(Object bean,
                                                 @NonNull String beanName) throws BeansException {
        Class<?> source; Mappers mappers; Mapper mapper;
        if (ObjectUtils.isEmpty((mappers = AnnotationUtils
                .findAnnotation((source = bean.getClass()), Mappers.class)))) {
            if (ObjectUtils.isEmpty((mapper = AnnotationUtils.findAnnotation(source, Mapper.class))))
                return bean;

            BeanMapper.mapperRegister(source, mapper.target(),
                    getFieldMaps(source.getDeclaredFields(),
                            mapper.target()));
            return bean;
        }

        Arrays.stream(mappers.value())
                .forEach(m -> BeanMapper.mapperRegister(source, m.target(),
                        getFieldMaps(source.getDeclaredFields(), m.target())));
        return bean;
    }

    private Map<String, String[]> getFieldMaps(Field[] fields, Class<?> targetClazz) {
        Map<String, String[]> fieldMaps = new HashMap<>(fields.length);
        Arrays.stream(fields).forEach(f -> {
            Mappings mappings; FieldMapping fieldMapping;
            if (ObjectUtils.isEmpty(mappings= f.getAnnotation(Mappings.class))) {
                if (ObjectUtils.isEmpty(fieldMapping = f.getAnnotation(FieldMapping.class)))
                    return;

                capFieldMaps(fieldMaps, f.getName(), targetClazz, fieldMapping);
                return;
            }

            capFieldMaps(fieldMaps, f.getName(), targetClazz, mappings.value());
        });
        return fieldMaps;
    }

    private void capFieldMaps(Map<String, String[]> fieldMaps, String key, Class<?> targetClazz,
                              FieldMapping... fieldMappings) {
        Arrays.stream(fieldMappings)
                .forEach(f -> {
                    if (f.scope().equals(targetClazz))
                        fieldMaps.put(key, f.fieldNames());
                });

    }
}
