package com.fenix.gauss.infrastructure;

public interface FieldMetaData<T> {

    String getField();

    String[] getTargetFields();

    Class<T> getProcessorType();
}
