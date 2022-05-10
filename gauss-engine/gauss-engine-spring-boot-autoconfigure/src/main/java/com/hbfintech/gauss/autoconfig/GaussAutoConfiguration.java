package com.hbfintech.gauss.autoconfig;

import com.hbfintech.gauss.basis.BeanMapper;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan("com.hbfintech.gauss")
public class GaussAutoConfiguration {

    @Bean(initMethod = "init")
    @ConditionalOnMissingBean
    public BeanMapper getMapper() {
        return new BeanMapper();
    }
}
