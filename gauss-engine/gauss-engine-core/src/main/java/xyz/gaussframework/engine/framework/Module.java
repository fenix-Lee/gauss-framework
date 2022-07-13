package xyz.gaussframework.engine.framework;

/**
 *
 * @author Chang Su
 * @since 5/03/2022
 */
public interface Module<T extends ModuleProposal> {

    default void handle(T proposal) {}
}
