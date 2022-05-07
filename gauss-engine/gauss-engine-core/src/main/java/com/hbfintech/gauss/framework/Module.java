package com.hbfintech.gauss.framework;

/**
 *
 * @author Chang Su
 * @since 5/03/2022
 */
@FunctionalInterface
public interface Module extends Cloneable {

    void handle(ModuleProposal proposal);
}
