package xyz.gaussframework.engine.framework;

import org.springframework.beans.factory.annotation.AnnotatedBeanDefinition;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionReaderUtils;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.lang.NonNull;
import org.springframework.util.*;
import xyz.gaussframework.engine.util.GaussClassTypeUtil;
import xyz.gaussframework.engine.util.GaussUtil;

import java.util.*;

/**
 *
 * @author Chang Su
 * @version 1.0
 * @see ImportBeanDefinitionRegistrar
 * @since 8/7/2022
 */
class GaussConversionRegistrar implements ImportBeanDefinitionRegistrar,
        ResourceLoaderAware, EnvironmentAware {

    private ResourceLoader resourceLoader;

    private Environment environment;

    @Override
    public void registerBeanDefinitions(@NonNull AnnotationMetadata metadata,
                                        @NonNull BeanDefinitionRegistry registry) {
        registerGaussConvertors(metadata, registry);
    }

    @Override
    public void setEnvironment(@NonNull Environment environment) {
        this.environment = environment;
    }

    @Override
    public void setResourceLoader(@NonNull ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }

    private void registerGaussConvertors(AnnotationMetadata metadata, BeanDefinitionRegistry registry) {
        ClassPathScanningCandidateComponentProvider scanner = GaussUtil.getScanner(this.environment);
        scanner.setResourceLoader(this.resourceLoader);

        Set<String> basePackages;

        Map<String, Object> attrs = metadata
                .getAnnotationAttributes(EnableGaussEngine.class.getName());
        AnnotationTypeFilter annotationTypeFilter = new AnnotationTypeFilter(GaussConvertor.class);
        scanner.addIncludeFilter(annotationTypeFilter);
        basePackages = getBasePackages(metadata, Objects.requireNonNull(attrs));
        basePackages.remove("xyz.gaussframework.engine"); // filter default convertor from base packages
        for (String basePackage : basePackages) {
            Set<BeanDefinition> candidateComponents = scanner.findCandidateComponents(basePackage);
            for (BeanDefinition candidateComponent : candidateComponents) {
                if (candidateComponent instanceof AnnotatedBeanDefinition) {
                    AnnotatedBeanDefinition beanDefinition = (AnnotatedBeanDefinition) candidateComponent;
                    AnnotationMetadata annotationMetadata = beanDefinition.getMetadata();
                    validateMetadata(annotationMetadata); // check convertor type
                    Map<String, Object> attributes = annotationMetadata
                            .getAnnotationAttributes(GaussConvertor.class.getName());
                    registerGaussConvertor(registry, annotationMetadata, Objects.requireNonNull(attributes));
                }
            }
        }
    }

    private void validateMetadata(AnnotationMetadata annotationMetadata) {
        if (annotationMetadata.isConcrete()) {
            if (!checkConcreteConversion(annotationMetadata)) {
                Assert.isTrue(annotationMetadata.isInterface(),
                        "@GaussConvertor should be specified on an interface");
            }
        } else {
            Assert.isTrue(annotationMetadata.isInterface(),
                    "@GaussConvertor should be specified on an interface");
        }
    }

    private void registerGaussConvertor(BeanDefinitionRegistry registry,
                                        AnnotationMetadata annotationMetadata, Map<String, Object> attributes) {
        String name = createName(annotationMetadata, attributes);
        String className = annotationMetadata.getClassName();
        BeanDefinitionBuilder definition = BeanDefinitionBuilder
                .genericBeanDefinition(GaussConversionFactory.class);

        definition.addPropertyValue("type", className);
        definition.addPropertyValue("name", name);

//        definition.setPrimary(true); // spring-context that below 5.3.x may have no setPrimary method
        AbstractBeanDefinition beanDefinition = definition.getBeanDefinition();
        BeanDefinitionHolder holder = new BeanDefinitionHolder(beanDefinition, className, new String[] {name});

        BeanDefinitionReaderUtils.registerBeanDefinition(holder, registry);
    }

    private String createName(AnnotationMetadata metadata, Map<String, Object> attributes) {
        if (ObjectUtils.isEmpty(attributes)) {
            return metadata.getClassName();
        }

        String value = (String)attributes.get("name");
        if (!StringUtils.hasText(value)) {
            return metadata.getClassName();
        }
        return resolve(value);
    }

    protected Set<String> getBasePackages(AnnotationMetadata importingClassMetadata,
                                          Map<String, Object> attributes) {
        Set<String> basePackages = new HashSet<>();
        for (String pkg : (String[]) Objects.requireNonNull(attributes).get("value")) {
            if (StringUtils.hasText(pkg)) {
                basePackages.add(pkg);
            }
        }
        for (String pkg : (String[]) attributes.get("basePackages")) {
            if (StringUtils.hasText(pkg)) {
                basePackages.add(pkg);
            }
        }
        for (Class<?> clazz : (Class<?>[]) attributes.get("basePackageClasses")) {
            basePackages.add(ClassUtils.getPackageName(clazz));
        }

        if (basePackages.isEmpty()) {
            basePackages.add(
                    ClassUtils.getPackageName(importingClassMetadata.getClassName()));
        }
        return basePackages;
    }

    private boolean checkConcreteConversion(AnnotationMetadata metadata) {
        return GaussClassTypeUtil.isMatchInnerConvertor(metadata.getClassName());
    }

    private String resolve(String value) {
        if (StringUtils.hasText(value)) {
            return this.environment.resolvePlaceholders(value);
        }
        return value;
    }
}
