package xyz.gaussframework.engine.framework;

@FunctionalInterface
public interface Validation {

    boolean validate(ModuleProposal proposal);
}
