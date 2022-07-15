package xyz.gaussframework.engine.framework;

import org.springframework.beans.factory.FactoryBean;

class GaussConversionFactory implements FactoryBean<Object> {

    private Class<?> type;

    private String name;

    @Override
    public Object getObject() {
        return getTarget();
    }

    @SuppressWarnings("unchecked")
    <T> T getTarget() {
        return (T) new Targeter.GaussTargeter()
                .target(new Target.GaussCodedTarget<>(type, name));
    }

    @Override
    public Class<?> getObjectType() {
        return type;
    }

    public String getName() {
        return name;
    }

    @SuppressWarnings("unused")
    public void setName(String name) {
        this.name = name;
    }

    public void setType(Class<?> type) {
        this.type = type;
    }
}