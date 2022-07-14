package xyz.gaussframework.engine.util;

import org.springframework.util.ClassUtils;

public class ClassValidator {

    public static boolean ClassTypeValidation (Class<?> source, Class<?> assignable) {
        return ClassUtils.isAssignable(assignable, source);
    }

    public static boolean ClassTypeValidation (String sourceName, Class<?> assignable) {
        try {
            return ClassTypeValidation(ClassUtils
                    .forName(sourceName, ClassUtils.getDefaultClassLoader()), assignable);
        } catch (ClassNotFoundException e) {
            return false;
        }
    }

    public static boolean ClassTypeValidation (Class<?> source, String assignableName) {
        try {
            return ClassTypeValidation(source, ClassUtils.forName(assignableName, ClassUtils.getDefaultClassLoader()));
        } catch (ClassNotFoundException e) {
            return false;
        }
    }

    public static boolean ClassTypeValidation (String sourceName, String assignableName) {
        try {
            return ClassTypeValidation(sourceName,
                    ClassUtils.forName(assignableName, ClassUtils.getDefaultClassLoader()));
        } catch (ClassNotFoundException e) {
            return false;
        }
    }
}
