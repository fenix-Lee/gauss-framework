package xyz.gaussframework.engine.util;

import org.springframework.util.ClassUtils;

public class GaussClassTypeUtil {

    private static final String INNER_CONVERTOR_PATH = "ma.glasnost.orika.Converter";

    public static boolean classTypeMatch(Class<?> source, Class<?> assignable) {
        return ClassUtils.isAssignable(assignable, source);
    }

    public static boolean classTypeMatch(String sourceName, Class<?> assignable) {
        try {
            return classTypeMatch(ClassUtils
                    .forName(sourceName, ClassUtils.getDefaultClassLoader()), assignable);
        } catch (ClassNotFoundException e) {
            return false;
        }
    }

    public static boolean classTypeMatch(Class<?> source, String assignableName) {
        try {
            return classTypeMatch(source, ClassUtils.forName(assignableName, ClassUtils.getDefaultClassLoader()));
        } catch (ClassNotFoundException e) {
            return false;
        }
    }

    public static boolean classTypeMatch(String sourceName, String assignableName) {
        try {
            return classTypeMatch(sourceName,
                    ClassUtils.forName(assignableName, ClassUtils.getDefaultClassLoader()));
        } catch (ClassNotFoundException e) {
            return false;
        }
    }

    public static boolean isMatchInnerConvertor (Class<?> source) {
        return classTypeMatch(source, INNER_CONVERTOR_PATH);
    }

    public static boolean isMatchInnerConvertor (String sourceName) {
        return classTypeMatch(sourceName, INNER_CONVERTOR_PATH);
    }
}
