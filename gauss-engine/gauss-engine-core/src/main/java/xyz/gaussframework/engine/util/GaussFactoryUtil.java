package xyz.gaussframework.engine.util;

import xyz.gaussframework.engine.factory.GaussFactory;
import org.springframework.util.ObjectUtils;

public class GaussFactoryUtil {

    public static boolean checkIfFactory(Class<?> sourceClass) {
        if (ObjectUtils.isEmpty(sourceClass)) {
            return false;
        }

        return GaussClassTypeUtil.classTypeMatch(sourceClass, GaussFactory.class);
    }
}
