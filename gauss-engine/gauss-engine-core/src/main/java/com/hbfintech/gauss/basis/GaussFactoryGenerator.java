package com.hbfintech.gauss.basis;

import com.hbfintech.gauss.factory.Creator;
import com.hbfintech.gauss.factory.GaussFactory;
import com.hbfintech.gauss.util.FactoryValidator;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.util.Assert;

import java.lang.annotation.Annotation;

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
            if (BeanFactory.isReady()) {
                return BeanFactory.getObject(clazz);
            }
            throw new RuntimeException("Gauss factory generator is not ready");
        }
        return BeanFactory.copyObject(BeanFactory.getObject(clazz));
    }
}
