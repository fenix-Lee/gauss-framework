package com.hbfintech.gauss.autoconfig;

import com.hbfintech.gauss.util.BeanMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan("com.hbfintech.gauss")
public class GaussAutoConfiguration {

    @Bean(initMethod = "init")
    public BeanMapper getMapper() {
        return new BeanMapper();
    }
}
