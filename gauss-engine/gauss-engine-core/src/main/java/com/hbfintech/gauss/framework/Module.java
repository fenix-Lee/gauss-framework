package com.hbfintech.gauss.framework;

/**
 *
 * @author Chang Su
 * @since 5/03/2022
 */
public interface Module extends Cloneable {

    default void handle(ModuleProposal proposal) {}
}
