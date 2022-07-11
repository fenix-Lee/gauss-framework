package com.fenix.gauss.infrastructure;

import com.fenix.gauss.basis.BeanFactory;
import com.fenix.gauss.basis.BeanMapper;
import com.fenix.gauss.framework.GaussConversion;
import com.fenix.gauss.framework.GaussConvertor;

@GaussConvertor
public interface DefaultProcessor {

    @GaussConvertor.Role(tag = "default")
    GaussConversion<Object, Object> defaultConversion = (s, d) -> null;
}
