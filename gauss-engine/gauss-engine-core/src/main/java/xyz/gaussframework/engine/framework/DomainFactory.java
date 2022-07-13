package xyz.gaussframework.engine.framework;

import java.util.List;
import java.util.function.Function;

/**
 *
 *
 *
 * @author Chang Su
 * @version 1.0
 * @since 4/03/2022
 * @param <T> type of chains
 * @param <R> type of bean
 */
public interface DomainFactory<T, R> {

    /**
     *
     *
     * @param mapper function of creating chains
     * @return list of chain
     */
    @SuppressWarnings("unused")
    default List<R> produce(Function<? super T, ? extends R> mapper) {return null;}

    /**
     *
     *
     * @param mapper function of creating bean
     * @return R type bean
     */
    @SuppressWarnings("unused")
    default R manufacture(Function<List<T>, ? extends R> mapper) { return null;}
}
