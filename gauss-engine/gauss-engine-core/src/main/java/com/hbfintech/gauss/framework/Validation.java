package com.hbfintech.gauss.framework;

@FunctionalInterface
public interface Validation {

    boolean validate(ModuleProposal proposal);
}
