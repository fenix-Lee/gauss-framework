package com.fenix.gauss.basis;

import com.fenix.gauss.factory.Creator;
import com.fenix.gauss.factory.GaussFactory;
import com.fenix.gauss.util.FactoryValidator;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.util.Assert;

/**
 * This is a factory generator aimed to create(or copy) factory which extends {@link GaussFactory}
 *
 * @author Chang Su
 * @version 2.0
 * @since 7/5/2022
 */
public enum GaussFactoryGenerator {

    INSTANCE;

    public <T> T getFactory(Class<T> clazz) {
        if (!FactoryValidator.checkIfFactory(clazz)) {
            throw new IllegalArgumentException(" cannot create non-factory class in factory generator");
        }

        Creator creatorAnnotation = AnnotationUtils.findAnnotation(clazz, Creator.class);
        Assert.notNull(creatorAnnotation, "cannot create this factory without @Creator annotation");
        return processFactory(clazz, creatorAnnotation);
    }

    <T> T processFactory(Class<T> clazz, Creator creatorAnnotation) {
        if (creatorAnnotation.isSingleton()) {
            if (GaussBeanFactory.isReady()) {
                return GaussBeanFactory.getObject(clazz);
            }
            throw new RuntimeException("Gauss factory generator is not ready");
        }
        return GaussBeanFactory.copyObject(GaussBeanFactory.getObject(clazz));
    }
}
