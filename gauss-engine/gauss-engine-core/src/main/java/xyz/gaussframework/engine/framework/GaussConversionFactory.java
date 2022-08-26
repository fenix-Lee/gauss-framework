package xyz.gaussframework.engine.framework;

import ma.glasnost.orika.Converter;
import org.springframework.beans.factory.FactoryBean;
import xyz.gaussframework.engine.exception.GaussConvertorException;

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

    interface GaussCustomConvertor {

        default <S,D> Converter<S, D> getConvertor(String tag) {
            throw new GaussConvertorException("tag: " + tag + " must be casted by handler...");
        }
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
