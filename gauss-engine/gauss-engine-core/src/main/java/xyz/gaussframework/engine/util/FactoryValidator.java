package xyz.gaussframework.engine.util;

import org.springframework.util.ClassUtils;
import xyz.gaussframework.engine.factory.GaussFactory;
import org.springframework.util.ObjectUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 *
 * @author Chang Su
 * @version 1.0
 * @since 19/05/2022
 */
public class FactoryValidator {

    @SuppressWarnings("unchecked")
    public static boolean checkIfFactory(Class<?> sourceClass) {
        if (ObjectUtils.isEmpty(sourceClass)) {
            return false;
        }

        Class<GaussFactory<?,?>> gaussFactoryClass;
        try {
            gaussFactoryClass = (Class<GaussFactory<?, ?>>) Class
                    .forName("xyz.gaussframework.engine.factory.GaussFactory");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }

        return ClassValidator.ClassTypeValidation(sourceClass, gaussFactoryClass);
    }
}
