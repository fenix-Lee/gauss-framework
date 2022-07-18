package xyz.gaussframework.engine.infrastructure;

import xyz.gaussframework.engine.framework.GaussConversion;
import xyz.gaussframework.engine.framework.GaussConvertor;

@GaussConvertor
public interface DefaultProcessor {

    @GaussConvertor.Role(tag = "default")
    GaussConversion<Object, Object> defaultConversion = (s, d) -> null;
}
