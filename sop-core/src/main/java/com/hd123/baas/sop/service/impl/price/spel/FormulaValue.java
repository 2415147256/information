package com.hd123.baas.sop.service.impl.price.spel;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.core.annotation.AliasFor;

/**
 * @author zhengzewang on 2020/11/23.
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface FormulaValue {

  @AliasFor("name")
  String value() default "";

  @AliasFor("value")
  String name() default "";

}
