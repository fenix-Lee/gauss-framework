package com.fenix.gauss.framework;

@FunctionalInterface
public interface Validation {

    boolean validate(ModuleProposal proposal);
}
