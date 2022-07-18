package xyz.gaussframework.engine.util;

import xyz.gaussframework.engine.factory.GaussFactory;
import org.springframework.util.ObjectUtils;

/**
 *
 * @author Chang Su
 * @version 1.0
 * @since 19/05/2022
 */
public class GaussFactoryUtil {

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

        return GaussClassTypeUtil.classTypeMatch(sourceClass, gaussFactoryClass);
    }
}
