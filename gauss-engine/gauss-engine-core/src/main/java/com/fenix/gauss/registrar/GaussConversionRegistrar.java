package com.fenix.gauss.registrar;

import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

@Component
public class GaussConversionRegistrar implements ImportBeanDefinitionRegistrar,
        ResourceLoaderAware, EnvironmentAware {

    private ResourceLoader resourceLoader;

    private Environment environment;

    @Override
    public void registerBeanDefinitions(@NonNull AnnotationMetadata metadata,
                                        @NonNull BeanDefinitionRegistry registry) {
        registerGaussConversion(metadata, registry);
    }

    @Override
    public void setEnvironment(@NonNull Environment environment) {
        this.environment = environment;
    }

    @Override
    public void setResourceLoader(@NonNull ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }

    private void registerGaussConversion(AnnotationMetadata metadata, BeanDefinitionRegistry registry) {

    }
}
