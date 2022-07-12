package com.fenix.gauss.basis;

import com.fenix.gauss.framework.GaussConversion;
import com.fenix.gauss.infrastructure.DefaultProcessor;
import com.fenix.gauss.infrastructure.FieldEngine;
import com.fenix.gauss.infrastructure.FieldMetaData;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import ma.glasnost.orika.Converter;
import ma.glasnost.orika.CustomConverter;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.converter.ConverterFactory;
import ma.glasnost.orika.converter.builtin.CloneableConverter;
import ma.glasnost.orika.impl.DefaultMapperFactory;
import ma.glasnost.orika.metadata.ClassMapBuilder;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import javax.annotation.PostConstruct;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * Not only does {@link GaussBeanMapper} copy all fields from one class to another, but does copy object same with
 * {@code Cloneable}
 *
 * @author Chang Su
 * @version 2.1
 * @since 4/3/2022
 */
@Component
public class GaussBeanMapper {

    private static final MapperFactory MAPPER_FACTORY = new DefaultMapperFactory.Builder()
            .useAutoMapping(true)
            .build();

    private static final List<FieldEngine> fieldEngines = Lists.newCopyOnWriteArrayList();

    private static final Map<String, Class<?>> processorMap = Maps.newConcurrentMap();

    @PostConstruct
    public void init() {
        ConverterFactory converterFactory = MAPPER_FACTORY.getConverterFactory();
        // register convertor
        converterFactory.registerConverter(new CloneableConverter(GaussBeanFactory.getCloneableClass()));
        if (!ObjectUtils.isEmpty(processorMap)) {
            processorMap.forEach((k,v) -> converterFactory
                    .registerConverter(k, (Converter<?,?>)GaussBeanFactory.getBean(v)));
        }
        if (!fieldEngines.isEmpty()) {
            fieldEngines.forEach(engine -> registerMetaData(engine.getSourceType(),
                    engine.getTargetType(), engine.getFieldAnnotatedMetaData()));
        }
    }

    public static<S,D> void mapperRegister(Class<S> source,
                                           Class<D> target,
                                           @Nullable Map<String, String[]> fieldMaps) {

        if (ObjectUtils.isEmpty(fieldMaps)) {
            registerByDefault(source, target);
            return;
        }
        register(source, target, fieldMaps);
    }

    @SuppressWarnings("unused")
    public static<S, D> void mapping(S source, D target) {
        MAPPER_FACTORY.getMapperFacade().map(source, target);
    }

    public static <S, D> D mapping(S source, Class<D> destClazz) {
        return MAPPER_FACTORY.getMapperFacade()
                .map(source, destClazz);
    }

    private static<S,D> void register(Class<S> source, Class<D> target,
                                      Map<String, String[]> fieldMaps) {
        ClassMapBuilder<S, D> classMapBuilder = MAPPER_FACTORY.classMap(source, target);
        for (Map.Entry<String, String[]> entry : fieldMaps.entrySet()) {
            String[] names = entry.getValue();
            for (String name : names) {
                classMapBuilder = classMapBuilder.field(entry.getKey(), name);
            }
        }
        classMapBuilder.byDefault().register();
    }

    private static<S, D> void registerMetaData(Class<S> sourceType, Class<D> targetType,
                                         Map<String, List<FieldMetaData<?>>> fieldMaps) {
        ClassMapBuilder<S, D> classMapBuilder = MAPPER_FACTORY.classMap(sourceType, targetType);
        for (Map.Entry<String, List<FieldMetaData<?>>> entry : fieldMaps.entrySet()) {
            for (FieldMetaData<?> metaData : entry.getValue()) {
                if (ObjectUtils.isEmpty(metaData.getProcessorType())
                        || metaData.getProcessorType().equals(DefaultProcessor.class)) {
                    for (String targetField : metaData.getTargetFields()) {
                        classMapBuilder = classMapBuilder.field(entry.getKey(), targetField);
                    }
                } else {
                    for (String targetField : metaData.getTargetFields()) {
                        classMapBuilder = classMapBuilder.fieldMap(entry.getKey(), targetField)
                                .converter(metaData.getProcessorType().getName())
                                .add();
                    }
                }
            }
        }
        classMapBuilder.byDefault().register();
    }

    private static<S, D> void registerByDefault(Class<S> source, Class<D> target) {
        MAPPER_FACTORY.classMap(source, target)
                .byDefault()
                .register();
    }

    static void addFieldEngine (FieldEngine fieldEngine) {
        fieldEngines.add(fieldEngine);
    }

    static void addProcessor (Class<?> processorClass) {
        processorMap.putIfAbsent(processorClass.getName(), processorClass);
    }
}
