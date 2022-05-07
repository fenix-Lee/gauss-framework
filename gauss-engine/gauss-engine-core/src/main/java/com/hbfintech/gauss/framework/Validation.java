package com.hbfintech.gauss.framework;

@FunctionalInterface
public interface Validation extends Module{

    boolean validate(ModuleProposal proposal);
}
