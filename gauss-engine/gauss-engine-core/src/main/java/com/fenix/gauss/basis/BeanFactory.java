package com.fenix.gauss.basis;

import com.google.common.collect.Maps;
import com.fenix.gauss.util.FactoryValidator;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import java.lang.reflect.Constructor;
import java.util.*;
import java.util.function.Consumer;

/**
 * BeanFactory is a new dynamic way to acquire bean from Application Context(not only
 * assigned from Spring) as a utility tool. Furthermore, it has been enhanced by a
 * capability of "object copy".
 *
 * <b>Please use wisely of {@link BeanFactory#getBean} method coz it gives you the
 * singleton by default if context is from Spring framework</b>
 *
 * @author Chang Su
 * @version 2.0
 * @see ApplicationContext
 * @see org.springframework.context.ApplicationContextAware
 * @since 4/3/2022
 */
@Component
public class BeanFactory implements ApplicationContextAware {

    private static ApplicationContext context;

    private static final List<Class<?>> cloneableClazz = new ArrayList<>();

    private static final Map<Class<?>, Object> objectCache = Maps.newConcurrentMap();

    /**
     * Get an instance of the object that client acquires through application context. Please use it wisely
     * only if you are familiar with the underlying context container or use {@code BeanFactory#create}
     * instead if you are in unknown situation.
     * @param clazz class type the object
     * @return an instance of the object
     */
    @SuppressWarnings("unused")
    public static <T> T getBean(Class<T> clazz) {
        if (FactoryValidator.checkIfFactory(clazz)) {
            return GaussFactoryGenerator.INSTANCE.getFactory(clazz);
        }
        return getObject(clazz);
    }

    /**
     * Get an instance of the object that client acquires through application context. Please use it wisely
     * only if you are familiar with the underlying context container or use {@code BeanFactory#create}
     * instead if you are in unknown situation.
     * @param name name of the object
     * @return an instance of the object
     */
    public static Object getBean(String name) {
        Object obj = context.getBean(name);
        if (ObjectUtils.isEmpty(obj)) {
            return null;
        }
        if (FactoryValidator.checkIfFactory(obj.getClass())) {
            return GaussFactoryGenerator.INSTANCE.getFactory(obj.getClass());
        }
        return obj;
    }

    static <T> T getObject(Class<T> clazz) {
        return context.getBean(clazz);
    }

    public static <T> T create(Class<T> clazz) {
        if (FactoryValidator.checkIfFactory(clazz)) {
            return GaussFactoryGenerator.INSTANCE.getFactory(clazz);
        }
        try {
            return copyObject(getObject(clazz));
        } catch (BeansException e) {
            return copyObject(createObject(clazz));
        }
    }

    /**
     * use declared constructor to instantiate an object
     * @param clazz the class type of object
     * @param args constructor parameters
     * @return instance of object
     * @param <T> the type of object
     */
    @SuppressWarnings({"unchecked","unused"})
    public static <T> T create(Class<T> clazz, Object...args) {
        if (FactoryValidator.checkIfFactory(clazz)) {
            return GaussFactoryGenerator.INSTANCE.getFactory(clazz);
        }
        Constructor<T>[] ctors = (Constructor<T>[]) clazz.getDeclaredConstructors();
        Optional<Constructor<T>> properCtor = Arrays.stream(ctors)
                .filter(c -> Arrays.equals(c.getParameterTypes(), Arrays.stream(args)
                        .map(Object::getClass).toArray()))
                .findAny();
        return copyObject(createObjectWithCtor(properCtor.orElseThrow(IllegalArgumentException::new), args));
    }

    @SuppressWarnings("unchecked")
    private static<T> T createObject(Class<T> clazz) {
        if (objectCache.containsKey(clazz)) {
            return (T) objectCache.get(clazz);
        }
        T obj = originalInstantiation(clazz);
        objectCache.put(clazz, obj);
        return obj;
    }

    private static<T> T createObjectWithCtor(Constructor<T> ctor, Object... args) {
        return BeanUtils.instantiateClass(ctor, args);
    }

    @SuppressWarnings("unused")
    public static <T> T create(Class<T> clazz, Consumer<T> action) {
        T copy = create(clazz);
        action.accept(copy);
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

    static void addCloneableClazz(Class<?> clazz) {
        if (cloneableClazz.contains(clazz))
            return;
        cloneableClazz.add(clazz);
    }

    static Class<?>[] getCloneableClass() {
        return cloneableClazz.toArray(new Class<?>[0]);
    }

    public static boolean isReady() {
        return !ObjectUtils.isEmpty(context);
    }

    /**
     * This method is inherited from Spring-aware component and leave here for client to replace the context
     * container if client wishes to change context implementation by its own.
     * @param applicationContext bean container
     * @throws BeansException see {@code ApplicationContextException}
     */
    @Override
    public void setApplicationContext(@NonNull ApplicationContext applicationContext)
            throws BeansException {
        context = applicationContext;
    }
}
