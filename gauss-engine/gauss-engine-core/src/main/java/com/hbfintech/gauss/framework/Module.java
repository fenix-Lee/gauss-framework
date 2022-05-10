package com.hbfintech.gauss.framework;

/**
 *
 * @author Chang Su
 * @since 5/03/2022
 */
public interface Module<T extends ModuleProposal> extends Cloneable {

    default void handle(T proposal) {}
}
