package xyz.gaussframework.engine.framework;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.lang.NonNull;
import org.springframework.util.ObjectUtils;

import java.lang.annotation.Annotation;

public class BeanRegisterProcessor implements BeanPostProcessor {

    @Override
    public Object postProcessAfterInitialization(@NonNull Object bean,
                                                 @NonNull String beanName) throws BeansException {
        Annotation annotation = AnnotationUtils.findAnnotation(bean.getClass(), Register.class);
        if (ObjectUtils.isEmpty(annotation)) {
            return bean;
        } else {
            GaussBeanFactory.addRegistrableClazz(bean.getClass());
        }
        return bean;
    }
}
