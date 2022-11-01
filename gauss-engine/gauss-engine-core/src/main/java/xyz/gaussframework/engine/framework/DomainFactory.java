package xyz.gaussframework.engine.framework;

import org.springframework.lang.NonNull;
import xyz.gaussframework.engine.exception.GaussFactoryException;

import java.util.List;
import java.util.function.Function;

/**
 * A template for the creation of object related to gauss factory
 *
 * @author Chang Su
 * @version 1.0
 * @since 4/03/2022
 * @param <T> type of chains
 * @param <R> type of bean
 */
public interface DomainFactory<T, R> {

    /**
     * produce a single object by the type of module
     * @param mapper function of creating chains
     * @return list of chain
     */
    @SuppressWarnings("unused")
    default List<R> produce(@NonNull Function<? super T, ? extends R> mapper) {
        throw new GaussFactoryException("produce method in DomainFactory must be override...");
    }

    /**
     * manufacture a single object by chains of modules
     * @param mapper function of creating bean
     * @return R type bean
     */
    @SuppressWarnings("unused")
    default R manufacture(@NonNull Function<List<T>, ? extends R> mapper) {
        throw new GaussFactoryException("manufacture method in DomainFactory must be override...");
    }
}
