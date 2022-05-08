package com.hbfintech.gauss.basis;

import com.hbfintech.gauss.factory.GaussFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

@Component
public class BeanFactory implements ApplicationContextAware {

    private static ApplicationContext context;

    private static final List<Class<?>> cloneableClazz = new ArrayList<>();

    @SuppressWarnings("unchecked")
    public static <T> T acquireBean(Class<T> clazz) {
        Class<GaussFactory<?,?>> gaussFactoryClass;
        try {
            gaussFactoryClass = (Class<GaussFactory<?, ?>>) Class
                    .forName("com.hbfintech.gauss.factory.GaussFactory");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        if (clazz.getSuperclass().getCanonicalName().equals(gaussFactoryClass.getCanonicalName())) {
            throw new RuntimeException("factory class cannot be acquired in BeanFactory....");
        }
        return getObject(clazz);
    }

    static <T> T getObject(Class<T> clazz) {
        return context.getBean(clazz);
    }

    public static <T> T getObjectCopy(Class<T> clazz) {
        return copyObject(acquireBean(clazz));
    }

    public static <T> T getObjectCopy(Class<T> clazz, Consumer<T> consumer) {
        T copy = getObjectCopy(clazz);
        consumer.accept(copy);
        return copy;
    }

    @SuppressWarnings("unchecked")
    public static<T> T originalCopy(T source) {
        T copy = (T) originalInstantiation(source.getClass());
        BeanUtils.copyProperties(source, copy);
        return copy;
    }

    public static<T> T originalInstantiation(Class<T> clazz) {
        return BeanUtils.instantiateClass(clazz);
    }

    @SuppressWarnings("unchecked")
    public static<T> T copyObject(T source) {
        try {
            return (T) BeanMapper.mapping(source, source.getClass());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void addCloneableClazz(Class<?> clazz) {
        if (cloneableClazz.contains(clazz))
            return;
        cloneableClazz.add(clazz);
    }

    public static Class<?>[] getCloneableClass() {
        return cloneableClazz.toArray(new Class<?>[0]);
    }

    public static Map<String, Object> getBeansWithAnnotation(Class<? extends Annotation> annotationType) {
        return context.getBeansWithAnnotation(annotationType);
    }

    @Override
    public void setApplicationContext(@NonNull ApplicationContext applicationContext)
            throws BeansException {
        context = applicationContext;
    }
}
