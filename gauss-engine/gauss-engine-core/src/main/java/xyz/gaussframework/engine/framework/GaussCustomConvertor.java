package xyz.gaussframework.engine.framework;

import ma.glasnost.orika.Converter;

public interface GaussCustomConvertor {

    default <S,D> Converter<S, D> getConvertor(String tag) {
        throw new RuntimeException("tag: " + tag + " must be casted by handler...");
    }
}
