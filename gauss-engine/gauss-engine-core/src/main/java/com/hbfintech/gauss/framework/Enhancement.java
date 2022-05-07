package com.hbfintech.gauss.framework;

/**
 *
 * @author Chang Su
 * @since 3/03/2022
 */
public interface Enhancement {

    void before(ModuleProposal proposal);

    void after(ModuleProposal proposal);
}
