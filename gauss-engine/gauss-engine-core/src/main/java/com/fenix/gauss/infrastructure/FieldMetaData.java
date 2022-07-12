package com.fenix.gauss.infrastructure;

public interface FieldMetaData<T> {

    String[] getTargetFields();

    Class<T> getProcessorType();
}
