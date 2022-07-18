package xyz.gaussframework.engine;

import org.springframework.context.annotation.*;
import xyz.gaussframework.engine.infrastructure.aspect.GaussCacheAspect;

@Configuration
@ComponentScan
@EnableAspectJAutoProxy
public class GaussAutoConfiguration {

    @Bean
    public GaussCacheAspect gaussCacheProxy () {
        return new GaussCacheAspect();
    }
}
