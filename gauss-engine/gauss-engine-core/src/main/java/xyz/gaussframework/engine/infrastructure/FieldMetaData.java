package xyz.gaussframework.engine.infrastructure;

public interface FieldMetaData<T> {

    String[] getTargetFields();

    Class<T> getProcessorType();

    String tag();
}
