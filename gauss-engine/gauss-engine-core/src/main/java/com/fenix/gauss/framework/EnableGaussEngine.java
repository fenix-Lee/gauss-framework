package com.fenix.gauss.framework;

import com.fenix.gauss.GaussContextConfiguration;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import(GaussContextConfiguration.class)
public @interface EnableGaussEngine {
}
