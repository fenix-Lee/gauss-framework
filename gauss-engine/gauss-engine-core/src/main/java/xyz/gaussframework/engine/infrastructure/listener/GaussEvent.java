package xyz.gaussframework.engine.infrastructure.listener;

import org.springframework.context.ApplicationEvent;

public class GaussEvent extends ApplicationEvent {

    public GaussEvent(Object source) {
        super(source);
    }
}
