package com.hbfintech.gauss.basis;

import com.hbfintech.gauss.factory.Creator;
import com.hbfintech.gauss.factory.GaussFactory;
import com.hbfintech.gauss.util.Validator;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.util.Assert;

/**
 * This is a factory generator aimed to create(or copy) factory which extends {@link GaussFactory}
 *
 * @author Chang Su
 * @version 1.0
 * @since 7/5/2022
 */
public enum GaussFactoryGenerator {

    INSTANCE;

    public <T> T getFactory(Class<T> clazz) {
        return getFactory(clazz, false);
    }

    public <T> T getFactory(Class<T> clazz, boolean ifOrigin) {
        if (!Validator.checkIfFactory(clazz)) {
            throw new RuntimeException(" cannot create non-factory class in factory generator");
        }

        return processFactory(clazz, ifOrigin);
    }

    <T> T processFactory(Class<? extends T> clazz, boolean ifOrigin) {
        Creator creatorAnnotation = AnnotationUtils.findAnnotation(clazz, Creator.class);
        Assert.notNull(creatorAnnotation, "cannot create this factory without @Creator annotation");
        if (ifOrigin) {
            return BeanFactory.originalCopy(BeanFactory.getObject(clazz));
        }

        if (creatorAnnotation.isSingleton()) {
            if (BeanFactory.isReady()) {
                return BeanFactory.getObject(clazz);
            }
            throw new RuntimeException("Gauss factory generator is not ready");
        }
        return BeanMapper.mapping(BeanFactory.getObject(clazz), clazz);
    }
}
