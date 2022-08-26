package xyz.gaussframework.engine.framework;

import xyz.gaussframework.engine.exception.GaussFactoryException;
import xyz.gaussframework.engine.factory.Creator;
import xyz.gaussframework.engine.factory.GaussFactory;
import xyz.gaussframework.engine.util.GaussFactoryUtil;
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
        if (!GaussFactoryUtil.checkIfFactory(clazz)) {
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
            throw new GaussFactoryException("Gauss factory generator is not ready");
        }
        return GaussBeanFactory.copyObject(GaussBeanFactory.getObject(clazz));
    }
}
