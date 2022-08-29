package xyz.gaussframework.engine;

import org.springframework.context.annotation.*;
import xyz.gaussframework.engine.framework.BeanCloneableProcessor;
import xyz.gaussframework.engine.framework.BeanMapperProcessor;
import xyz.gaussframework.engine.framework.GaussBeanFactory;
import xyz.gaussframework.engine.framework.GaussBeanMapper;
import xyz.gaussframework.engine.infrastructure.aspect.GaussCacheAspect;

@Configuration
@ComponentScan
@EnableAspectJAutoProxy
@Import({GaussBeanFactory.class,
        GaussBeanMapper.class,
        BeanCloneableProcessor.class,
        BeanMapperProcessor.class})
public class GaussAutoConfiguration {

    @Bean
    public GaussCacheAspect gaussCacheProxy () {
        return new GaussCacheAspect();
    }
}
