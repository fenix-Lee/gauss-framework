package com.fenix.gauss.framework;

public abstract class ModuleProposal {

    public static final boolean FLOW_SUCCESS_STATE = Boolean.TRUE;

    public static final boolean FLOW_FAIL_STATE = Boolean.FALSE;

    protected boolean moduleState = FLOW_SUCCESS_STATE;

    public boolean validateState() {
        return moduleState;
    }

    public void reset(boolean state) {
        moduleState = state;
    }

}
