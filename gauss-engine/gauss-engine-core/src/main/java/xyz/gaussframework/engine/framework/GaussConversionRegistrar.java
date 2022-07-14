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
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.lang.NonNull;
import org.springframework.util.*;
import xyz.gaussframework.engine.basis.GaussBeanMapper;
import xyz.gaussframework.engine.util.ClassValidator;

import java.lang.reflect.Field;
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
        ClassPathScanningCandidateComponentProvider scanner = getScanner();
        scanner.setResourceLoader(this.resourceLoader);

        Set<String> basePackages;

        Map<String, Object> attrs = metadata
                .getAnnotationAttributes(EnableGaussEngine.class.getName());
        AnnotationTypeFilter annotationTypeFilter = new AnnotationTypeFilter(GaussConvertor.class);
        scanner.addIncludeFilter(annotationTypeFilter);
        basePackages = getBasePackages(metadata, attrs);
        basePackages.remove("com.fenix.gauss"); // filter default convertor from base packages
        for (String basePackage : basePackages) {
            Set<BeanDefinition> candidateComponents = scanner.findCandidateComponents(basePackage);
            for (BeanDefinition candidateComponent : candidateComponents) {
                if (candidateComponent instanceof AnnotatedBeanDefinition) {
                    AnnotatedBeanDefinition beanDefinition = (AnnotatedBeanDefinition) candidateComponent;
                    AnnotationMetadata annotationMetadata = beanDefinition.getMetadata();
                    if (annotationMetadata.isConcrete()) {
                        // check super classes if any with 'ma.glasnost.orika.CustomConverter'
                        if (!checkConcreteConversion(annotationMetadata)) {
                            Assert.isTrue(annotationMetadata.isInterface(),
                                    "@GaussConvertor should be specified on an interface");
                        }
                    } else {
                        Assert.isTrue(annotationMetadata.isInterface(),
                                "@GaussConvertor should be specified on an interface");
                    }

                    Map<String, Object> attributes = metadata
                            .getAnnotationAttributes(GaussConvertor.class.getCanonicalName());
                    registerGaussConvertor(registry, annotationMetadata, attributes);
                }
            }
        }
    }

    private void registerGaussConvertor(BeanDefinitionRegistry registry,
                                        AnnotationMetadata annotationMetadata, Map<String, Object> attributes) {
        String name = createName(annotationMetadata, attributes);
        String className = annotationMetadata.getClassName();
        BeanDefinitionBuilder definition = BeanDefinitionBuilder
                .genericBeanDefinition(GaussConversionFactory.class);

        definition.addPropertyValue("name", name);
        definition.addPropertyValue("type", className);
//        definition.addPropertyValue("tags", getTags(className));

        AbstractBeanDefinition beanDefinition = definition.getBeanDefinition();
        BeanDefinitionHolder holder = new BeanDefinitionHolder(beanDefinition, className,
                new String[] {});

        BeanDefinitionReaderUtils.registerBeanDefinition(holder, registry);
    }

//    private Object getTags(String className) {
//        Class<?> convertorClass;
//        try {
//            convertorClass = ClassUtils.forName(className, ClassUtils.getDefaultClassLoader());
//        } catch (ClassNotFoundException e) {
//            return null;
//        }
//        if (ClassValidator.ClassTypeValidation(convertorClass,
//                "ma.glasnost.orika.Converter")) {
//            return new HashSet<String>(){{add(convertorClass.getName());}};
//        }
//        Field[] fields = convertorClass.getDeclaredFields();
//        Assert.isTrue(!ObjectUtils.isEmpty(fields), "conversion function must be declared...");
//        Set<String> tags = new HashSet<>(fields.length);
//        Arrays.stream(fields)
//                .filter(f -> f.isAnnotationPresent(GaussConvertor.Role.class))
//                .forEach(f -> tags.add(f.getAnnotation(GaussConvertor.Role.class).tag()));
//        return tags;
//    }

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

    protected ClassPathScanningCandidateComponentProvider getScanner() {
        return new ClassPathScanningCandidateComponentProvider(false, this.environment) {
            @Override
            protected boolean isCandidateComponent(@NonNull AnnotatedBeanDefinition beanDefinition) {
                boolean isCandidate = false;
                if (beanDefinition.getMetadata().isIndependent()) {
                    if (!beanDefinition.getMetadata().isAnnotation()) {
                        isCandidate = true;
                    }
                }
                return isCandidate;
            }
        };
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
        return ClassValidator.ClassTypeValidation(metadata.getClassName(),
                "ma.glasnost.orika.Converter");
    }

    private String resolve(String value) {
        if (StringUtils.hasText(value)) {
            return this.environment.resolvePlaceholders(value);
        }
        return value;
    }
}
