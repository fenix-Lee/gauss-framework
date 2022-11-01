package xyz.gaussframework.engine;

import org.springframework.context.annotation.*;
import xyz.gaussframework.engine.framework.*;
import xyz.gaussframework.engine.infrastructure.aspect.GaussCacheAspect;

@Configuration
@ComponentScan
@EnableAspectJAutoProxy
@Import({GaussBeanFactory.class,
        GaussBeanMapper.class,
        BeanCloneableProcessor.class,
        BeanRegisterProcessor.class,
        BeanMapperProcessor.class})
public class GaussAutoConfiguration {

    @Bean
    public GaussCacheAspect gaussCacheProxy () {
        return new GaussCacheAspect();
    }
}
