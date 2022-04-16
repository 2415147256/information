package com.lzh.servicejob;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = {"com.lzh"})
public class ServiceJobApplication {

    public static void main(String[] args) {
        SpringApplication.run(ServiceJobApplication.class, args);
    }

}
