package xyz.gaussframework.engine.factory;

import org.springframework.lang.NonNull;
import xyz.gaussframework.engine.framework.DomainFactory;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Base factory class for being extended by custom-defined factory if wish to 'combine' its own
 * chain classes
 *
 * @author Chang Su
 * @version 1.0
 * @since 4/5/2022
 * @param <T> chain type
 * @param <R> result type
 */
public abstract class GaussFactory<T, R> extends GaussChain<T>
        implements DomainFactory<T, R>, Cloneable {

    @SuppressWarnings("unused")
    public List<T> produce() {
        return getModules();
    }

    @Override
    public List<R> produce(@NonNull Function<? super T, ? extends R> mapper) {
        return getModules().stream()
                .map(mapper)
                .collect(Collectors.toList());
    }

    @Override
    public R manufacture(@NonNull Function<List<T>, ? extends R> mapper) {
        return mapper.apply(getModules());
    }

    @Override
    @SuppressWarnings("unchecked")
    public GaussFactory<T, R> clone() {
        try {
            GaussFactory<T, R> copy = (GaussFactory<T, R>) super.clone();
            copy.init();
            return copy;
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }
}
