package xyz.gaussframework.engine.infrastructure.listener;

import org.springframework.context.ApplicationListener;
import xyz.gaussframework.engine.factory.GaussFactory;
import xyz.gaussframework.engine.util.GaussClassTypeUtil;

import java.util.Map;

/**
 * A listener for {@link GaussFactory} initialization
 *
 * @author Chang Su
 * @version 1.0
 * @since 29/8/2022
 */
public class GaussListener implements ApplicationListener<GaussEvent> {

    @Override
    @SuppressWarnings("unchecked")
    public void onApplicationEvent(GaussEvent event) {
        Map<String, Object> sources = (Map<String, Object>) event.getSource();
        sources.values().stream()
                .filter(g -> GaussClassTypeUtil.classTypeMatch(g.getClass(), GaussFactory.class))
                .forEach(g -> ((GaussFactory<?,?>)g).init());
    }
}
