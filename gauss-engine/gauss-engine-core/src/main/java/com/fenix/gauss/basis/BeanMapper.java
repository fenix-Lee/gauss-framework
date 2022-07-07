package com.fenix.gauss.basis;

import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.converter.builtin.CloneableConverter;
import ma.glasnost.orika.impl.DefaultMapperFactory;
import ma.glasnost.orika.metadata.ClassMapBuilder;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import javax.annotation.PostConstruct;
import java.util.Map;

/**
 * Not only does {@link BeanMapper} copy all fields from one class to another, but does copy object same with
 * {@code Cloneable}
 *
 * @author Chang Su
 * @version 1.1
 * @see MapperFactory
 * @since 4/3/2022
 */
@Component
public class BeanMapper {

    private static final MapperFactory MAPPER_FACTORY = new DefaultMapperFactory.Builder()
            .useAutoMapping(true)
            .build();

    @PostConstruct
    public void init() {
        MAPPER_FACTORY.getConverterFactory()
                .registerConverter(new CloneableConverter(BeanFactory.getCloneableClass()));
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
        classMapBuilder.byDefault()
                .register();
    }

    private static<S, D> void registerByDefault(Class<S> source, Class<D> target) {
        MAPPER_FACTORY.classMap(source, target)
                .byDefault()
                .register();
    }
}
