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

    public static boolean checkIfFactory(Class<?> sourceClass) {
        if (ObjectUtils.isEmpty(sourceClass)) {
            return false;
        }

        return GaussClassTypeUtil.classTypeMatch(sourceClass, GaussFactory.class);
    }
}
