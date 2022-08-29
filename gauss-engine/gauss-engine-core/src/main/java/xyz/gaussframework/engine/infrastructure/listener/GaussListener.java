package xyz.gaussframework.engine.infrastructure.listener;

import org.springframework.context.ApplicationListener;
import xyz.gaussframework.engine.factory.GaussChain;
import xyz.gaussframework.engine.factory.GaussFactory;

import java.util.Map;

public class GaussListener implements ApplicationListener<GaussEvent> {

    @Override
    @SuppressWarnings("unchecked")
    public void onApplicationEvent(GaussEvent event) {
        Map<String, GaussFactory<?, ?>> sources = (Map<String, GaussFactory<?, ?>>)event.getSource();
        sources.values().forEach(GaussChain::init);
    }
}
