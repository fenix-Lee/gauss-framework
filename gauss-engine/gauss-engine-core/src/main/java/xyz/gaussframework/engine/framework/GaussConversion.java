package xyz.gaussframework.engine.framework;

import ma.glasnost.orika.metadata.Type;

@FunctionalInterface
public interface GaussConversion<S, D> {

    D convert(S source, Type<? extends D> targetType);
}
