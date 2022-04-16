package com.lzh.servicebase.config;

import com.google.common.base.Predicates;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

/**
 * @author 卢正豪
 * @version 1.0
 */
@Configuration
@EnableSwagger2   //表示swagger的注解
public class SwaggerConfig {


    @Bean
    public Docket webApiConfig(){

        return new Docket(DocumentationType.SWAGGER_2)
                .groupName("webApi")
                .apiInfo(webApiInfo())
                .select()
                .paths(Predicates.not(PathSelectors.regex("/admin/.*")))
                .paths(Predicates.not(PathSelectors.regex("/error.*")))
                .paths(PathSelectors.any())
                .build();
    }


    private ApiInfo webApiInfo(){

        return new ApiInfoBuilder()
                .title("网站-供求信息网API文档")
                .description("本文档描述了供求信息网中心微服务接口定义")
                .version("1.0")
                .contact(new Contact("lzh", "https://localhost:8080.com", "2415147256@qq.com"))
                .build();

    }

}
