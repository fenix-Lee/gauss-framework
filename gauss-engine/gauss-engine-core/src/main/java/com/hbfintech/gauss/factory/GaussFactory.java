package com.hbfintech.gauss.factory;
;
import com.hbfintech.gauss.framework.DomainFactory;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

public abstract class GaussFactory<T, R> extends GaussChain<T>
        implements DomainFactory<T, R> {

    @Override
    public List<R> wrap(Function<? super T, ? extends R> mapper) {
        return getModules().stream()
                .map(mapper)
                .collect(Collectors.toList());
    }

    @Override
    public R manufacture(Function<List<T>, ? extends R> mapper) {
        return mapper.apply(getModules());
    }
}
