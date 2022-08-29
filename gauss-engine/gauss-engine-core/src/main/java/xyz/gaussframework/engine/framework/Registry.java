package xyz.gaussframework.engine.framework;

/**
 * @author Chang Su
 * @version 1.0
 * @since 29/8/2022
 * @param <T> object type for being created
 */
public interface Registry<T> {

    <S> T create(S source);
}
