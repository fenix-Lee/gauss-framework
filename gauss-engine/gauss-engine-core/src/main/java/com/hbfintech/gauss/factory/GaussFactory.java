package com.hbfintech.gauss.factory;

import com.hbfintech.gauss.framework.DomainFactory;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 *
 * @author Chang Su
 * @version 1.0
 * @since 4/5/2022
 * @param <T> chain type
 * @param <R> result type
 */
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
