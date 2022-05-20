package com.hd123.baas.sop.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 记录每个方法请求的参数
 *
 * 记录的格式：【全类名#方法名】-{标题}- parameter：[{参数=xxx}]
 *
 * @author W.J.H.7
 * @since 1.0.0
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface LogRequestPraras {
  /**
   * 标题
   *
   */
  String note() default "";
}
