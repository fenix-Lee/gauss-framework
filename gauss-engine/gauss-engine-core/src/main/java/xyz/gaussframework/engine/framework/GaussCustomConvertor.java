package xyz.gaussframework.engine.framework;

import ma.glasnost.orika.Converter;
import xyz.gaussframework.engine.exception.GaussConvertorException;

public interface GaussCustomConvertor {

    default <S,D> Converter<S, D> getConvertor(String tag) {
        throw new GaussConvertorException("tag: " + tag + " must be casted by handler...");
    }
}
