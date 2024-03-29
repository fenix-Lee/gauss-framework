package xyz.gaussframework.engine.framework;

import xyz.gaussframework.engine.exception.GaussMapperException;
import xyz.gaussframework.engine.infrastructure.FieldEngine;
import xyz.gaussframework.engine.infrastructure.FieldMetaData;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.converter.ConverterFactory;
import ma.glasnost.orika.impl.DefaultMapperFactory;
import ma.glasnost.orika.metadata.ClassMapBuilder;
import org.springframework.lang.Nullable;
import org.springframework.util.ObjectUtils;
import xyz.gaussframework.engine.util.GaussClassTypeUtil;

import javax.annotation.PostConstruct;
import java.util.*;

/**
 * Not only does {@link GaussBeanMapper} copy all fields from one class to another, but does copy object same with
 * {@code Cloneable}
 *
 * @author Chang Su
 * @version 2.2
 * @since 4/3/2022
 */
public class GaussBeanMapper {

    private static final MapperFactory MAPPER_FACTORY = new DefaultMapperFactory.Builder()
            .useAutoMapping(true)
            .build();

    private static void registerGaussConvertor (Class<?> processorClass, String tag, ConverterFactory factory) {
        try {
            if (GaussClassTypeUtil.isMatchInnerConvertor(processorClass)) {
                factory.registerConverter(tag,
                        (ma.glasnost.orika.Converter<?, ?>) GaussBeanFactory.getBean(processorClass));
                return;
            }

            factory.registerConverter(tag, ConvertorAdapter.buildAdapter(tag, processorClass));
        } catch (Exception e) {
            throw new GaussMapperException(e.getMessage());
        }
    }

    /**
     * register a 'temporary' mapping strategy for a readable only class you are not capable of configuring
     * @param source source class
     * @param target target class
     * @param fieldMaps the field mapping to
     * @param <S> source type
     * @param <D> target type
     */
    @SuppressWarnings("unused")
    public static<S,D> void mapperRegister(Class<S> source,
                                           Class<D> target,
                                           @Nullable Map<String, String[]> fieldMaps) {

        if (ObjectUtils.isEmpty(fieldMaps)) {
            registerByDefault(source, target);
            return;
        }
        register(source, target, fieldMaps);
    }

    /**
     * copy {@code source} value to {@code target} provided by client
     * @param source source instance
     * @param target target instance
     * @param <S> source type
     * @param <D> target type
     */
    @SuppressWarnings("unused")
    public static<S, D> void mapping(S source, D target) {
        try {
            MAPPER_FACTORY.getMapperFacade().map(source, target);
        } catch (Exception e) {
            throw new GaussMapperException(e.getMessage());
        }
    }

    /**
     * get target with values copied from {@code source}
     * @param source source instance
     * @param destClazz target class
     * @return target
     * @param <S> source type
     * @param <D> target type
     */
    public static <S, D> D mapping(S source, Class<D> destClazz) {
        try {
            return MAPPER_FACTORY.getMapperFacade()
                    .map(source, destClazz);
        } catch (Exception e) {
            throw new GaussMapperException(e.getMessage());
        }
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
                if (metaData.getProcessorType().equals(Object.class)) {
                    for (String targetField : metaData.getTargetFields()) {
                        classMapBuilder = classMapBuilder.field(entry.getKey(), targetField);
                    }
                } else {
                    for (String targetField : metaData.getTargetFields()) {
                        classMapBuilder = classMapBuilder.fieldMap(entry.getKey(), targetField)
                                .converter(metaData.tag())
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

    static void register(FieldEngine engine) {
        registerMetaData(engine.getSourceType(), engine.getTargetType(), engine.getFieldAnnotatedMetaData());
    }

    static void registerConvertor (Class<?> processClass, String tag) {
        ConverterFactory factory = MAPPER_FACTORY.getConverterFactory();
        if (!factory.hasConverter(tag)) {
            registerGaussConvertor(processClass, tag, MAPPER_FACTORY.getConverterFactory());
        }
    }
}
