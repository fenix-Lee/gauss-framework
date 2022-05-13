package com.hbfintech.gauss.basis;

import com.hbfintech.gauss.factory.GaussFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.util.ClassUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.util.ReflectionUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

/**
 *
 *
 * @author Chang Su
 * @version 1.0
 * @since 4/3/2022
 * @see ApplicationContext
 */
@Component
public class BeanFactory implements ApplicationContextAware {

    private static ApplicationContext context;

    private static final List<Class<?>> cloneableClazz = new ArrayList<>();

    @SuppressWarnings("unchecked")
    public static <T> T acquireBean(Class<T> clazz) {
        if (checkIfFactory(clazz)) {
            return GaussFactoryGenerator.INSTANCE.getFactory(clazz);
        }
        return getObject(clazz);
    }

    public static Object acquireBean(String name) {
        return context.getBean(name);
    }

    static <T> T getObject(Class<T> clazz) {
        return context.getBean(clazz);
    }

    public static <T> T getObjectCopy(Class<T> clazz) {
        if (checkIfFactory(clazz)) {
            return GaussFactoryGenerator.INSTANCE.getFactory(clazz);
        }
        return copyObject(acquireBean(clazz));
    }

    public static <T> T getObjectCopy(Class<T> clazz, Consumer<T> consumer) {
        T copy = getObjectCopy(clazz);
        consumer.accept(copy);
        return copy;
    }

    @SuppressWarnings("unchecked")
    public static<T> T originalCopy(T source) {
        if (source instanceof Cloneable) {
            // use clone method
            try {
                Method cloneMethod = Object.class.getDeclaredMethod("clone");
                cloneMethod.setAccessible(true);
                return (T)ReflectionUtils
                        .invokeMethod(cloneMethod, source);
            } catch (NoSuchMethodException e) {
                throw new RuntimeException(e);
            }
        }
        T copy = (T) originalInstantiation(source.getClass());
        BeanUtils.copyProperties(source, copy);
        return copy;
    }

    @SuppressWarnings("unchecked")
    private static boolean checkIfFactory(Class<?> sourceClass) {
        Class<GaussFactory<?,?>> gaussFactoryClass;
        try {
            gaussFactoryClass = (Class<GaussFactory<?, ?>>) Class
                    .forName("com.hbfintech.gauss.factory.GaussFactory");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }

        if (sourceClass.getSuperclass().getCanonicalName().equals(gaussFactoryClass.getCanonicalName())) {
            return true;
        }
        return false;
    }

    private static<T> T originalInstantiation(Class<T> clazz) {
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

    @Override
    public void setApplicationContext(@NonNull ApplicationContext applicationContext)
            throws BeansException {
        context = applicationContext;
    }
}
