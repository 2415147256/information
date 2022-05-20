package com.hd123.baas.sop.utils;

import com.hd123.rumba.commons.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class SpringUtils implements ApplicationContextAware {
 
    private static ApplicationContext applicationContext;
 
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        SpringUtils.applicationContext = applicationContext;
    }
 
    public static <T> T getBean(String beanName) {
        if(applicationContext.containsBean(beanName)){
            return (T) applicationContext.getBean(beanName);
        }else{
            return null;
        }
    }
    public static String getProperty(String name) {
        return applicationContext.getEnvironment().getProperty(name);
    }

    public static <T> T getBeansOfType(Class<T> baseType){
        return applicationContext.getBean(baseType);
    }
}