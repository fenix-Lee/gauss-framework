package com.fenix.gauss.framework;

import com.fenix.gauss.GaussAutoConfiguration;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import(GaussAutoConfiguration.class)
public @interface EnableGaussEngine {
}
