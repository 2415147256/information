package com.lzh.serviceshop;


import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(value = "com.lzh")
@MapperScan(value = "com.lzh.serviceshop.mapper")
public class ServiceShopApplication {

    public static void main(String[] args) {
        SpringApplication.run(ServiceShopApplication.class, args);
    }

}
