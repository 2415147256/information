package com.hd123.baas.sop.evcall;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import com.hd123.baas.sop.utils.JsonUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.hd123.rumba.commons.lang.Assert;
import com.hd123.rumba.evcall.EvCallException;
import com.hd123.rumba.evcall.EvCallManager;

/**
 * @author youjiawei
 **/
@Component
public class EvCallEventPublisher {

  @Autowired
  private EvCallManager evCallManager;

  /**
   * 提交调用请求。
   *
   * @param executorId
   *          执行器ID。
   * @param msg
   *          执行参数。
   * @return 返回为调用请求分配的ID。
   * @throws EvCallException
   *           若指定的执行器尚未注册时抛出。
   */
  public <T extends AbstractEvCallMessage> void publishForNormal(String executorId, T msg) {
    Assert.hasText(executorId, "executorId");
    if (msg instanceof AbstractTenantEvCallMessage) {
      Assert.hasText(((AbstractTenantEvCallMessage) msg).getTenant(), "租户id");
    }

    evCallManager.submit(executorId, encodeMessage(msg));
  }

  /**
   * 提交调用请求。
   *
   * @param executorId
   *          执行器ID。
   * @param msg
   *          执行参数。
   * @param delay
   *          延迟到指定时长后执行，单位毫秒。
   * @return 返回为调用请求分配的ID。
   * @throws EvCallException
   *           若指定的执行器尚未注册时抛出。
   */
  public <T extends AbstractEvCallMessage> void publishForNormal(String executorId, T msg, long delay) {
    Assert.hasText(executorId, "executorId");
    if (msg instanceof AbstractTenantEvCallMessage) {
      Assert.hasText(((AbstractTenantEvCallMessage) msg).getTenant(), "租户id");
    }

    evCallManager.submit(executorId, encodeMessage(msg), delay);
  }

  /**
   * 提交调用请求。
   *
   * @param executorId
   *          执行器ID。
   * @param msg
   *          执行参数。
   * @param priority
   *          优先级。取值越大表示优先级越高。
   * @return 返回为调用请求分配的ID。
   * @throws EvCallException
   *           若指定的执行器尚未注册时抛出。
   */
  public <T extends AbstractEvCallMessage> void publishForNormal(String executorId, T msg, int priority) {
    Assert.hasText(executorId, "executorId");
    if (msg instanceof AbstractTenantEvCallMessage) {
      Assert.hasText(((AbstractTenantEvCallMessage) msg).getTenant(), "租户id");
    }

    evCallManager.submit(executorId, encodeMessage(msg), priority);
  }

  /**
   * 提交调用请求。
   *
   * @param executorId
   *          执行器ID。
   * @param msg
   *          执行参数。
   * @param after
   *          延迟到指定时刻之后执行。允许传入null，表示立即执行。
   * @return 返回为调用请求分配的ID。
   * @throws EvCallException
   *           若指定的执行器尚未注册时抛出。
   */
  public <T extends AbstractEvCallMessage> void publishForNormal(String executorId, T msg, Date after) {
    Assert.hasText(executorId, "executorId");
    if (msg instanceof AbstractTenantEvCallMessage) {
      Assert.hasText(((AbstractTenantEvCallMessage) msg).getTenant(), "租户id");
    }

    evCallManager.submit(executorId, encodeMessage(msg), after);
  }

  private <T extends AbstractEvCallMessage> String encodeMessage(T msg) {
    Map<String,Object> map= JsonUtil.jsonToObject(JsonUtil.objectToJson(msg), HashMap.class);
    map.put("traceId", getTraceId());
    return JsonUtil.objectToJson(map);
  }

  private String getTraceId() {
    // String traceId = TracingPool.getTracingId();
    String traceId = null;
    if (StringUtils.isBlank(traceId)) {
      traceId = MDC.get("trace_id");
    }
    if (StringUtils.isBlank(traceId)) {
      traceId = UUID.randomUUID().toString();
    }
    return traceId;
  }
}
