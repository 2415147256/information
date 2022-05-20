package com.hd123.baas.sop.remote.uas;

import com.hd123.baas.sop.configuration.BaseConfiguration;
import feign.auth.BasicAuthRequestInterceptor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;

import java.nio.charset.Charset;

/**
 * @author guyahui
 * @date 2021/5/20 10:28
 */
public class BasicAuthConfiguration extends BaseConfiguration {
    @Value("${uas-service.auth.username:guest}")
    private String username;
    @Value("${uas-service.auth.password:guest}")
    private String password;

    @Bean
    public BasicAuthRequestInterceptor basicAuthRequestInterceptor() {
        return new BasicAuthRequestInterceptor(username, password, Charset.forName("UTF-8"));
    }
}
