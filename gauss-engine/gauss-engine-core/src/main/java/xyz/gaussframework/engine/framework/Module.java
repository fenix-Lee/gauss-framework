package xyz.gaussframework.engine.framework;

/**
 * This is the base type of chain operation
 *
 * @author Chang Su
 * @version 2.0
 * @since 5/03/2022
 */
public interface Module<T extends ModuleProposal> {

    void handle(T proposal);
}
