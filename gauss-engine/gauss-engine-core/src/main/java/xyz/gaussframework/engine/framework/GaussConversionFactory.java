package xyz.gaussframework.engine.framework;

import org.springframework.beans.factory.FactoryBean;

import java.util.Set;

class GaussConversionFactory implements FactoryBean<Object> {

    private Class<?> type;

    private String name;

    private Set<String> tags;

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

    public void setName(String name) {
        this.name = name;
    }

    public Set<String> getTags() {
        return tags;
    }

    public void setTags(Set<String> tags) {
        this.tags = tags;
    }

    public void setType(Class<?> type) {
        this.type = type;
    }
}
