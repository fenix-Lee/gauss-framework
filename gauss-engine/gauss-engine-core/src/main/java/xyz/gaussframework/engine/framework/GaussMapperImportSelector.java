package xyz.gaussframework.engine.framework;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.context.annotation.ImportSelector;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.core.type.classreading.MetadataReader;
import org.springframework.core.type.classreading.MetadataReaderFactory;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.lang.NonNull;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;
import org.springframework.util.ObjectUtils;
import xyz.gaussframework.engine.util.GaussUtil;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.*;
import java.util.stream.Collectors;

class GaussMapperImportSelector implements ImportSelector,
        ResourceLoaderAware, EnvironmentAware {

    private static final List<Class<? extends Annotation>> REQUIRED_ANNOTATIONS = new ArrayList<>();

    private ResourceLoader resourceLoader;

    private Environment environment;

    static {
        REQUIRED_ANNOTATIONS.add(Mappers.class);
        REQUIRED_ANNOTATIONS.add(Mapper.class);
        REQUIRED_ANNOTATIONS.add(Register.class);
//        REQUIRED_ANNOTATIONS.add(OverrideClone.class);
    }

    @Override
    @NonNull
    public String[] selectImports(@NonNull AnnotationMetadata importingClassMetadata) {
        return getRequiredCandidates(importingClassMetadata)
                .toArray(new String[0]);
    }

    private Set<String> getRequiredCandidates (AnnotationMetadata metadata) {
        Assert.isTrue(REQUIRED_ANNOTATIONS.size() > 0, "");
        Set<String> classNames = new HashSet<>();
        REQUIRED_ANNOTATIONS.forEach(a -> classNames.addAll(scanWithAnnotation(metadata, a)));
        return classNames;
    }

    private Set<String> scanWithAnnotation (AnnotationMetadata metadata, Class<? extends Annotation> annotationClass) {
        ClassPathScanningCandidateComponentProvider scanner = GaussUtil.getScanner(environment);
        scanner.setResourceLoader(this.resourceLoader);
        scanner.addIncludeFilter(new GaussAnnotationTypeFilter(annotationClass));
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

    /**
     * @author Chang su
     * @version 1.0
     * @since 2/11/2022
     */
    static class GaussAnnotationTypeFilter extends AnnotationTypeFilter {

        public GaussAnnotationTypeFilter(Class<? extends Annotation> annotationType) {
            super(annotationType);
        }

        @Override
        public boolean match(@NonNull MetadataReader metadataReader, @NonNull MetadataReaderFactory metadataReaderFactory)
                throws IOException {
            if (!super.match(metadataReader, metadataReaderFactory)) {
                // check field
                String className = metadataReader.getClassMetadata().getClassName();
                Class<?> clazz = ClassUtils.resolveClassName(className, ClassUtils.getDefaultClassLoader());
                Assert.isTrue(!ObjectUtils.isEmpty(clazz), "");
                Field[] fields = clazz.getDeclaredFields();
                return Arrays.stream(fields).anyMatch(f -> f.isAnnotationPresent(getAnnotationType()));
            }
            return true;
        }
    }

}
