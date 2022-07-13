package xyz.gaussframework.engine;

import xyz.gaussframework.engine.infrastructure.aspect.GaussCacheAspect;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@Configuration
@ComponentScan
@EnableAspectJAutoProxy
public class GaussAutoConfiguration {

    @Bean
    public GaussCacheAspect gaussCacheProxy () {
        return new GaussCacheAspect();
    }
}
