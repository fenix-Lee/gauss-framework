package xyz.gaussframework.engine.framework;

import xyz.gaussframework.engine.exception.GaussFactoryException;
import xyz.gaussframework.engine.factory.Creator;
import xyz.gaussframework.engine.infrastructure.aspect.GaussCacheAspect;
import com.google.common.collect.Maps;
import xyz.gaussframework.engine.infrastructure.listener.GaussEvent;
import xyz.gaussframework.engine.util.GaussFactoryUtil;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ApplicationContextException;
import org.springframework.lang.NonNull;
import org.springframework.util.ObjectUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.util.*;
import java.util.function.Consumer;

/**
 * GaussBeanFactory is a new dynamic way to acquire bean from Application Context(not only
 * assigned from Spring) as a utility tool. Furthermore, it has been enhanced by a
 * capability of "object copy".
 *
 * <b>Please use wisely of {@link GaussBeanFactory#getBean} method coz it gives you the
 * singleton by default if context is from Spring framework</b>
 *
 * @author Chang Su
 * @version 2.3
 * @see ApplicationContext
 * @see org.springframework.context.ApplicationContextAware
 * @since 4/3/2022
 */
public class GaussBeanFactory implements ApplicationContextAware {

    private static final Log logger = LogFactory.getLog(GaussCacheAspect.class);

    private static ApplicationContext context;

    private static final List<Class<?>> CLONEABLE_CLASS = new ArrayList<>();

    private static final Map<Class<?>, Object> OBJECT_CACHE = Maps.newConcurrentMap();

    /**
     * Get an instance of the object that client acquires through application context. Please use it wisely
     * only if you are familiar with the underlying context container or use {@link GaussBeanFactory#create}
     * instead if you are in unknown situation.
     * @param clazz class type the object
     * @return an instance of the object
     */
    @SuppressWarnings("unused")
    public static <T> T getBean(Class<T> clazz) throws GaussFactoryException {
        if (GaussFactoryUtil.checkIfFactory(clazz)) {
            return GaussFactoryGenerator.INSTANCE.getFactory(clazz);
        }
        if (isReady()) {
            return getObject(clazz);
        } else {
            logger.error("GaussBeanFactory is not ready yet...");
            throw new ApplicationContextException("GaussBeanFactory cannot get required bean....") ;
        }
    }

    public static Map<String, Object> getBeansWithAnnotation (Class<? extends Annotation> annotationClass) {
        return context.getBeansWithAnnotation(annotationClass);
    }

    /**
     * Get an instance of the object that client acquires through application context. Please use it wisely
     * only if you are familiar with the underlying context container or use {@link GaussBeanFactory#create}
     * instead if you are in unknown situation.
     * @param name name of the object
     * @return an instance of the object
     */
    public static Object getBean(String name) {
        if (!isReady()) {
            logger.error("GaussBeanFactory is not ready yet...");
            throw new ApplicationContextException("GaussBeanFactory cannot get required bean....") ;
        }
        Object obj = context.getBean(name);
        if (ObjectUtils.isEmpty(obj)) {
            return null;
        }
        if (GaussFactoryUtil.checkIfFactory(obj.getClass())) {
            return GaussFactoryGenerator.INSTANCE.getFactory(obj.getClass());
        }
        return obj;
    }

    static <T> T getObject(Class<T> clazz) {
        return context.getBean(clazz);
    }

    public static <T> T create(Class<T> clazz) {
        if (GaussFactoryUtil.checkIfFactory(clazz)) {
            return GaussFactoryGenerator.INSTANCE.getFactory(clazz);
        }
        try {
            return copyObject(getObject(clazz));
        } catch (BeansException e) {
            logger.info(clazz + " is not in application context and will be instantiated originally...");
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
        if (GaussFactoryUtil.checkIfFactory(clazz)) {
            return GaussFactoryGenerator.INSTANCE.getFactory(clazz);
        }
        Constructor<T>[] ctors = (Constructor<T>[]) clazz.getDeclaredConstructors();
        Class<?>[] argTypes = (Class<?>[]) Arrays.stream(args)
                .map(Object::getClass).toArray();
        Optional<Constructor<T>> properCtor = Arrays.stream(ctors)
                .filter(c -> filterConstructors(c.getParameterTypes(), argTypes))
                .findAny();
        return createObjectWithCtor(properCtor.orElseThrow(IllegalArgumentException::new), args);
    }

    /**
     * use specific constructor to create an object
     * @param ctor the specific constructor of object
     * @param args constructor parameters
     * @return an instance of object
     * @param <T> the type of object
     */
    @SuppressWarnings("unused")
    public static <T> T create(Constructor<T> ctor, Object...args) {
        Class<T> clazz = ctor.getDeclaringClass();
        if (GaussFactoryUtil.checkIfFactory(clazz)) {
            return GaussFactoryGenerator.INSTANCE.getFactory(clazz);
        }
        return createObjectWithCtor(ctor, args);
    }

    @SuppressWarnings("unchecked")
    static<T> T createObject(Class<T> clazz) {
        if (OBJECT_CACHE.containsKey(clazz)) {
            return (T) OBJECT_CACHE.get(clazz);
        }
        T obj = originalInstantiation(clazz);
        OBJECT_CACHE.put(clazz, obj);
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

    static<T> T originalInstantiation(Class<T> clazz) {
        return BeanUtils.instantiateClass(clazz);
    }

    @SuppressWarnings("unchecked")
    public static<T> T copyObject(T source) {
        try {
            return (T) GaussBeanMapper.mapping(source, source.getClass());
        } catch (Exception e) {
           logger.error(e.getMessage());
        }
        return null;
    }

    static void addCloneableClazz(Class<?> clazz) {
        if (CLONEABLE_CLASS.contains(clazz))
            return;
        CLONEABLE_CLASS.add(clazz);
    }

    static Class<?>[] getCloneableClass() {
        return CLONEABLE_CLASS.toArray(new Class<?>[0]);
    }

    public static boolean isReady() {
        return !ObjectUtils.isEmpty(context);
    }

    private static boolean filterConstructors (Class<?>[] fromParameterTypes, Class<?>[] toParameterTypes) {
        if (!Arrays.equals(fromParameterTypes, toParameterTypes)) {
            if (fromParameterTypes.length == toParameterTypes.length) {
                return filterAssignable(fromParameterTypes, toParameterTypes);
            }
            return false;
        }
        return true;
    }

    private static boolean filterAssignable (Class<?>[] fromParameterTypes, Class<?>[] toParameterTypes) {
        for (int i = 0; i < fromParameterTypes.length; i++) {
            if (!fromParameterTypes[i].isAssignableFrom(toParameterTypes[i]) &&
                    !toParameterTypes[i].isAssignableFrom(fromParameterTypes[i])) {
                return false;
            }
        }
        return true;
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
        // publish listener for gauss chain
        context.publishEvent(new GaussEvent(getBeansWithAnnotation(Creator.class)));
    }
}
