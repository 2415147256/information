package com.hd123.baas.sop.remote.rsmas2;

import com.hd123.baas.sop.configuration.BaseConfiguration;
import feign.RequestInterceptor;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.MDC;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;

import javax.annotation.Resource;
import java.util.UUID;

/**
 * @author zhangweigang
 */
public class Mas2Configuration extends BaseConfiguration {
    @Resource
    private Environment env;

    private final String TRACE_ID = "trace_id";
    private final String AUTHORIZATION = "Authorization";
    private final String BASIC = "Basic";
    private final String USERNAME = "mas2-service.username";
    private final String PASSWORD = "mas2-service.password";
    private final String DEFAULT_USERNAME = "guest";
    private final String DEFAULT_PASSWORD = "guest";

    @Bean
    public RequestInterceptor mas2Interceptor() {
        return template -> {
            if (StringUtils.isBlank(MDC.get(TRACE_ID))) {
                MDC.put(TRACE_ID, UUID.randomUUID().toString().replace("-", ""));
            }
            String username = env.getProperty(USERNAME, String.class, DEFAULT_USERNAME);
            String password = env.getProperty(PASSWORD, String.class, DEFAULT_PASSWORD);
            template.header(TRACE_ID, MDC.get(TRACE_ID));
            template.header(AUTHORIZATION,
                    BASIC + Base64.encodeBase64String((username + ":" + password).getBytes()));
        };
    }
}
