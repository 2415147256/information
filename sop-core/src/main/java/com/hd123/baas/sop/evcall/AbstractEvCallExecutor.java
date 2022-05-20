package com.hd123.baas.sop.evcall;

import java.text.MessageFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;

import com.hd123.baas.sop.utils.JsonUtil;
import com.hd123.pms.util.ElapsedTimer;
import com.hd123.rumba.commons.biz.entity.OperateInfo;
import com.hd123.rumba.commons.biz.entity.Operator;
import com.hd123.rumba.evcall.EvCallExecutionContext;
import com.hd123.rumba.evcall.EvCallExecutor;
import com.qianfan123.baas.common.BaasException;

import lombok.extern.slf4j.Slf4j;

//import com.hd123.dcommons.log.TracingPool;

/**
 * @author W.J.H.7
 **/
@Slf4j
public abstract class AbstractEvCallExecutor<T extends AbstractEvCallMessage> implements EvCallExecutor {

  protected abstract void doExecute(T message, EvCallExecutionContext context) throws Exception;

  protected abstract T decodeMessage(String arg) throws BaasException;

  protected ElapsedTimer timer;

  @Autowired
  private ApplicationContext applicationContext;

  protected <T> T getBean(Class<T> clazz) {
    try {
      return this.applicationContext.getBean(clazz);
    } catch (Exception e) {
      throw new RuntimeException(MessageFormat.format("{0}未注入", clazz.getSimpleName()), e);
    }
  }

  protected <T> T getBean(String beanName, Class<T> clazz) {
    try {
      return this.applicationContext.getBean(beanName, clazz);
    } catch (Exception e) {
      throw new RuntimeException(MessageFormat.format("{0}未注入", clazz.getSimpleName()), e);
    }
  }

  @Override
  public void execute(String arg, EvCallExecutionContext context) throws Exception {
    T message = null;
    try {
      Map<String, Object> map = JsonUtil.jsonToObject(arg, HashMap.class);
      String traceId = (String) map.get("traceId");
      if (StringUtils.isBlank(traceId)) {
        traceId = UUID.randomUUID().toString();
      }
      map.remove("traceId");
      MDC.put("trace_id", traceId);
      message = decodeMessage(JsonUtil.objectToJson(map));
    } catch (Exception e) {
      log.error("EvCall解析参数失败，忽略。{}", this.getClass().getSimpleName(), e);
      return;
    }
    log.info("收到EvCall处理。{}-参数:{}", this.getClass().getSimpleName(), arg);
    try {
      if (this.timer == null) {
        this.timer = ElapsedTimer.getThreadInstance();
      }
      doExecute(message, context);
      log.info("EvCall处理耗时：{}", timer.toString());
    } catch (Exception e) {
      log.error("EvCall处理失败。{}", this.getClass().getSimpleName(), e);
      throw e;
    }
    log.info("EvCall处理完毕");
  }

  protected OperateInfo getSysOperateInfo() {
    OperateInfo operateInfo = new OperateInfo();
    Operator operator = new Operator();
    operateInfo.setTime(new Date());
    operator.setId("system");
    operator.setFullName("系统用户");
    operator.setNamespace("system");
    operateInfo.setOperator(operator);
    return operateInfo;
  }
}
