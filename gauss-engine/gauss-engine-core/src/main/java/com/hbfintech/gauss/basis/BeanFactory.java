package com.hbfintech.gauss.basis;

import com.hbfintech.gauss.util.Validator;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * BeanFactory is a new dynamic way to acquire bean from Application Context(not only
 * assigned from Spring) as a utility tool. Furthermore, it has been enhanced by a
 * capability of "object copy".
 *
 * <b>Please use wisely of {@code BeanFactory#getBean} method coz it gives you the
 * singleton by default if context is from Spring framework</b>
 *
 * @author Chang Su
 * @version 1.0
 * @see ApplicationContext
 * @see org.springframework.context.ApplicationContextAware
 * @since 4/3/2022
 */
@Component
public class BeanFactory implements ApplicationContextAware {

    private static ApplicationContext context;

    private static final List<Class<?>> cloneableClazz = new ArrayList<>();

    /**
     * Get an instance of the object that client acquires through application context. Please use
     * it wisely only if you are familiar with the underlying context container or use {@code BeanFactory#create}
     * instead if you are in unknown situation.
     *
     * @param clazz class type the object
     * @return an instance of the object
     */
    public static <T> T getBean(Class<T> clazz) {
        if (Validator.checkIfFactory(clazz)) {
            return GaussFactoryGenerator.INSTANCE.getFactory(clazz);
        }
        return getObject(clazz);
    }

    /**
     * Get an instance of the object that client acquires through application context. Please use
     * it wisely only if you are familiar with the underlying context container or use {@code BeanFactory#create}
     * instead if you are in unknown situation.
     *
     * @param name name of the object
     * @return an instance of the object
     */
    public static Object getBean(String name) {
        return context.getBean(name);
    }

    static <T> T getObject(Class<T> clazz) {
        return context.getBean(clazz);
    }

    public static <T> T create(Class<T> clazz) {
        if (Validator.checkIfFactory(clazz)) {
            return GaussFactoryGenerator.INSTANCE.processFactory(clazz, false);
        }
        return copyObject(getBean(clazz));
    }

    @SuppressWarnings("unused")
    public static <T> T create(Class<T> clazz, Consumer<T> action) {
        T copy = create(clazz);
        action.accept(copy);
        return copy;
    }

    public static<T> T originalCopy(T source) {
        @SuppressWarnings("unchecked")
        T copy = (T) originalInstantiation(source.getClass());
        BeanUtils.copyProperties(source, copy);
        return copy;
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

    public static boolean isReady() {
        return !ObjectUtils.isEmpty(context);
    }

    /**
     * This method is inherited from Spring-aware component and leave here for client to replace the context
     * container if client possible change application context implementation by its own.
     *
     * @param applicationContext bean container
     * @throws BeansException see {@code ApplicationContextException}
     */
    @Override
    public void setApplicationContext(@NonNull ApplicationContext applicationContext)
            throws BeansException {
        if (ObjectUtils.isEmpty(context)) {
            context = applicationContext;
        }
    }
}
