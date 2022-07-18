package xyz.gaussframework.engine.infrastructure;

import java.util.List;
import java.util.Map;

public interface FieldEngine {

    Class<?> getSourceType ();

    Class<?> getTargetType ();

    Map<String, List<FieldMetaData<?>>> getFieldAnnotatedMetaData();
}
