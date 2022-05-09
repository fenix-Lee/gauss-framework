package com.hbfintech.gauss.basis;

import com.hbfintech.gauss.factory.Creator;
import com.hbfintech.gauss.factory.GaussFactory;
import org.springframework.util.Assert;

public enum GaussFactoryGenerator {

    INSTANCE;

    public <T> T getFactory(Class<T> clazz) {
        return getFactory(clazz, false);
    }

    @SuppressWarnings("unchecked")
    public <T> T getFactory(Class<T> clazz, boolean ifOrigin) {
        Class<GaussFactory<?,?>> gaussFactoryClass;
        try {
            gaussFactoryClass = (Class<GaussFactory<?, ?>>) Class
                    .forName("com.hbfintech.gauss.factory.GaussFactory");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }

        if (clazz.getCanonicalName().equals(gaussFactoryClass.getCanonicalName())) {
            throw new RuntimeException("abstract class cannot be instantiated.....");
        }
        if (ifOrigin) {
            return BeanFactory.originalInstantiation(clazz);
        }
        Creator creatorAnnotation = clazz.getAnnotation(Creator.class);
        Assert.notNull(creatorAnnotation, "cannot create this factory without @Creator annotation");
        if (creatorAnnotation.isSingleton()) {
            return BeanFactory.getObject(clazz);
        }
        return BeanMapper.mapping(BeanFactory.getObject(clazz), clazz);
    }
}
