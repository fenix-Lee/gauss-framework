package xyz.gaussframework.engine.framework;

import ma.glasnost.orika.CustomConverter;
import ma.glasnost.orika.MappingContext;
import ma.glasnost.orika.metadata.Type;

/**
 * A convertor adapter for all user-declared convertor with @{link GaussConvertor} implemented by
 * {@code CustomConvertor}
 *
 * @author Chang Su
 * @version 1.0
 * @since 3/11/2022
 */
public class ConvertorAdapter extends CustomConverter<Object, Object> {

    private final String tag;

    private final Class<?> processClass;

    public ConvertorAdapter(String tag, Class<?> processClass) {
        this.tag = tag;
        this.processClass = processClass;
    }

    public static ConvertorAdapter buildAdapter(String tag, Class<?> processClass) {
        return new ConvertorAdapter(tag, processClass);
    }

    @Override
    public Object convert(Object source, Type<?> destinationType, MappingContext mappingContext) {
        return ((GaussConversionFactory.GaussCustomConvertor)GaussBeanFactory.getBean(processClass))
                .getConvertor(tag)
                .convert(source, destinationType, mappingContext);
    }
}
