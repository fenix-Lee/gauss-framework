package xyz.gaussframework.engine;

import org.springframework.context.annotation.*;
import xyz.gaussframework.engine.basis.BeanCloneableProcessor;
import xyz.gaussframework.engine.basis.BeanMapperProcessor;
import xyz.gaussframework.engine.basis.GaussBeanFactory;
import xyz.gaussframework.engine.basis.GaussBeanMapper;
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
