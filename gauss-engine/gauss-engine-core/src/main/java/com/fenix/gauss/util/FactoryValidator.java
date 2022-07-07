package com.fenix.gauss.util;

import com.fenix.gauss.factory.GaussFactory;
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
                    .forName("com.fenix.gauss.factory.GaussFactory");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }

        // check issue for more details
        return Objects.requireNonNull(getAllSuperClasses(sourceClass)).stream()
                .anyMatch(c -> c.getCanonicalName().equals(gaussFactoryClass.getCanonicalName()));
    }

    @SuppressWarnings("unchecked")
    private static List<Class<?>> getAllSuperClasses(Class<?> clazz) {
        if (ObjectUtils.isEmpty(clazz)) {
            return Collections.EMPTY_LIST;
        }

        List<Class<?>> allSuperClasses = new ArrayList<>();
        Class<?> current = clazz;
        while (true) {
            Class<?> superClass = current.getSuperclass();
            if (ObjectUtils.isEmpty(superClass)) {
                break;
            }
            allSuperClasses.add(superClass);
            current = superClass;
        }
        return allSuperClasses;
    }
}
