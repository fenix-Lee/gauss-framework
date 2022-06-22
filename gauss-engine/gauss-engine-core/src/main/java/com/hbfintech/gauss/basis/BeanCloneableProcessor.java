package com.hbfintech.gauss.basis;

import com.hbfintech.gauss.framework.OverrideClone;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

/**
 *
 *
 * @author Chang Su
 * @version 1.0
 * @since 4/3/2022
 */
@Component
public class BeanCloneableProcessor implements BeanPostProcessor {

    @Override
    public Object postProcessBeforeInitialization(@NonNull Object bean,
                                                  @NonNull String beanName) throws BeansException {
        OverrideClone overrideClone = AnnotationUtils.findAnnotation(bean.getClass(), OverrideClone.class);
        if (ObjectUtils.isEmpty(overrideClone)) {
            return bean;
        }
        else if (overrideClone.value()) {
            BeanFactory.addCloneableClazz(bean.getClass());
        }
        return bean;
    }
}
