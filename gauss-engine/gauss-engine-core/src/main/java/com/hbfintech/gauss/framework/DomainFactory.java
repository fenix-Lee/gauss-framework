package com.hbfintech.gauss.framework;

import java.util.List;
import java.util.function.Function;

public interface DomainFactory<T, R> {

    default List<R> wrap(Function<? super T, ? extends R> mapper) {return null;}

    default R manufacture(Function<List<T>, ? extends R> mapper) { return null;}
}
