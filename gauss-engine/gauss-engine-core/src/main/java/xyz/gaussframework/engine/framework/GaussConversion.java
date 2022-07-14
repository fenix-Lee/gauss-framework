package xyz.gaussframework.engine.framework;

import ma.glasnost.orika.Converter;
import ma.glasnost.orika.metadata.Type;

@FunctionalInterface
public interface GaussConversion<S, D> {

    D convert(S source, Type<? extends D> targetType);

    default Converter<S, D> getConverter(String tag) {
        throw new RuntimeException("tag: " + tag + " must be casted by handler...");
    }
}
