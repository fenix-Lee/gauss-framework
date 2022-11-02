package xyz.gaussframework.engine.framework;

@GaussConvertor
@SuppressWarnings("unused")
public interface DefaultRegistryConvertor {

    @GaussConvertor.Role(tag = "default")
    GaussConversion<Object, Object> BEAN_REGISTRY = (s, d) -> GaussBeanFactory.containsBean(s.getClass().getName())?
            GaussBeanFactory.getBean(s.getClass()) : s;
}
