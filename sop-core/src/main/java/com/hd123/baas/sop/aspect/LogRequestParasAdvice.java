package com.hd123.baas.sop.aspect;

import com.hd123.baas.sop.annotation.LogRequestPraras;
import com.hd123.baas.sop.utils.BaasJSONUtil;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

/**
 * 描述
 *
 * @author W.J.H.7
 * @since 1.0.0
 */
@Aspect
@Component
@Slf4j
public class LogRequestParasAdvice {
  @Pointcut("@annotation(com.hd123.baas.sop.annotation.LogRequestPraras)")
  private void aspectjMethod() {
  }

  @Around(value = "aspectjMethod()")
  public Object doAround(ProceedingJoinPoint point) throws Throwable {
    Long sts = System.currentTimeMillis();
    // 日志
    String methodName = log(point);
    // 调用核心逻辑
    Object obj = point.proceed();
    Long ets = System.currentTimeMillis();
    if (log.isDebugEnabled()) {
      log.debug("测试[{}]记录日志耗时统计：{} MS", methodName, (ets - sts));
    }
    return obj;
  }

  /**
   * 记录日志
   *
   * @param point
   */
  private String log(ProceedingJoinPoint point) {
    String methodName = getMethodName(point);
    log.info("【" + methodName + "】-" + getNote(point) + "- parameter：" + getParameterString(point));
    return methodName;
  }

  private String getMethodName(ProceedingJoinPoint point) {
    Method method = getMethod(point);
    if (method != null) {
      return method.getDeclaringClass().getName() + "#" + method.getName();
    }
    return "";
  }

  private String getNote(ProceedingJoinPoint point) {
    Method method = getMethod(point);
    if (method != null) {
      LogRequestPraras logRequestPraras = method.getAnnotation(LogRequestPraras.class);
      return logRequestPraras.note();
    }
    return "";
  }

  private String getParameterString(ProceedingJoinPoint point) {
    Object[] args = point.getArgs();
    if (null != args && args.length > 0) {
      try {
        return BaasJSONUtil.safeToJson(args);
      } catch (Exception ex) {
        ex.printStackTrace();
      }
    }
    return "";
  }

  /**
   * 获取添加注解的方法
   *
   * @param pjp
   *          pjp
   * @return 添加注解的方法
   */
  private Method getMethod(ProceedingJoinPoint pjp) {
    try {
      MethodSignature methodSignature = (MethodSignature) pjp.getSignature();
      return methodSignature.getMethod();
    } catch (Exception ex) {
      return null;
    }
  }
}
