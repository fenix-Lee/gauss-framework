package xyz.gaussframework.engine.framework;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.context.annotation.ImportSelector;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.lang.NonNull;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;
import xyz.gaussframework.engine.util.GaussUtil;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class GaussMapperImportSelector implements ImportSelector,
        ResourceLoaderAware, EnvironmentAware {

    private static final List<Class<? extends Annotation>> REQUIRED_ANNOTATION_CLASSES = new ArrayList<>();

    private ResourceLoader resourceLoader;

    private Environment environment;

    static {
        REQUIRED_ANNOTATION_CLASSES.add(Mappers.class);
        REQUIRED_ANNOTATION_CLASSES.add(Mapper.class);
        REQUIRED_ANNOTATION_CLASSES.add(OverrideClone.class);
    }

    @Override
    public String[] selectImports(@NonNull AnnotationMetadata importingClassMetadata) {
        return getRequiredCandidates(importingClassMetadata)
                .toArray(new String[0]);
    }

    private Set<String> getRequiredCandidates (AnnotationMetadata metadata) {
        Assert.isTrue(REQUIRED_ANNOTATION_CLASSES.size() > 0, "");
        Set<String> classNames = new HashSet<>();
        REQUIRED_ANNOTATION_CLASSES.forEach(a -> classNames.addAll(scanWithAnnotation(metadata, a)));
        return classNames;
    }

    private Set<String> scanWithAnnotation (AnnotationMetadata metadata, Class<? extends Annotation> annotationClass) {
        ClassPathScanningCandidateComponentProvider scanner = GaussUtil.getScanner(environment);
        scanner.setResourceLoader(this.resourceLoader);
        scanner.addIncludeFilter(new AnnotationTypeFilter(annotationClass));
        Set<String> basePackages = new HashSet<>();
        basePackages.add(ClassUtils.getPackageName(metadata.getClassName()));
        Set<String> candidateComponentsClassNames = new HashSet<>();
        for (String basePackage : basePackages) {
            candidateComponentsClassNames.addAll(scanner.findCandidateComponents(basePackage)
                    .stream().map(BeanDefinition::getBeanClassName).collect(Collectors.toSet()));
        }
        return candidateComponentsClassNames;
    }

    @Override
    public void setResourceLoader(@NonNull ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }

    @Override
    public void setEnvironment(@NonNull Environment environment) {
        this.environment = environment;
    }
}
