package com.hd123.baas.sop.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.hd123.rumba.snowflake.jdbc.JdbcIidGeneratorFactory;
import com.hd123.rumba.snowflake.spring.DefaultIidGeneratorConfigurer;

/**
 * @author W.J.H.7
 * @since 1.0.0
 **/

@Configuration
public class SnowflakeConfiguration {
    @Bean
    public DefaultIidGeneratorConfigurer defaultIidGenerator() {
        return new DefaultIidGeneratorConfigurer(JdbcIidGeneratorFactory.getSingleton().get());
    }
}

