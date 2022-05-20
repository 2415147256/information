package com.hd123.baas.sop.job.timed.executor;

import com.hd123.baas.config.core.BaasConfigClient;
import com.hd123.baas.sop.service.api.basedata.employee.Employee;
import com.hd123.baas.sop.service.api.basedata.employee.EmployeeFilter;
import com.hd123.baas.sop.service.api.basedata.employee.EmployeeService;
import com.hd123.baas.sop.config.FmsConfig;
import com.hd123.baas.sop.config.MessageConfig;
import com.hd123.baas.sop.config.TaskMessageConfig;
import com.hd123.baas.sop.service.impl.task.message.MessageTag;
import com.hd123.baas.sop.service.api.message.Message;
import com.hd123.baas.sop.service.api.message.MessageAction;
import com.hd123.baas.sop.service.api.message.MessageContentKey;
import com.hd123.baas.sop.service.api.message.MessageService;
import com.hd123.baas.sop.service.api.message.MessageType;
import com.hd123.baas.sop.service.api.task.ShopTask;
import com.hd123.baas.sop.service.api.task.ShopTaskService;
import com.hd123.baas.sop.service.api.task.ShopTaskState;
import com.hd123.baas.sop.evcall.EvCallEventPublisher;
import com.hd123.baas.sop.evcall.exector.fms.FmsSendEvCallExecutor;
import com.hd123.baas.sop.evcall.exector.fms.FmsSendMsg;
import com.hd123.baas.sop.job.entity.TimedJob;
import com.hd123.baas.sop.job.timed.ShopTaskRemindJobParam;
import com.hd123.baas.sop.job.timed.TimedJobExecutor;
import com.hd123.baas.sop.remote.fms.FmsClient;
import com.hd123.baas.sop.remote.fms.bean.AppMessageSaveNewReq;
import com.hd123.baas.sop.remote.fms.bean.MessageConvertUtil;
import com.hd123.baas.sop.utils.BaasJSONUtil;
import com.hd123.baas.sop.utils.SopUtils;
import com.hd123.rumba.commons.biz.query.QueryResult;
import com.qianfan123.baas.common.BaasException;
import com.qianfan123.baas.common.http2.BaasResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Slf4j
public class ShopTaskRemindJobExecutor implements TimedJobExecutor {

  public static final int SUCCESS = 0;
  public static final int FAIL = -1;

  public static final String BEAN_NAME = "shopTaskRemindJobExecutor";

  @Autowired
  private ShopTaskService shopTaskService;

  @Autowired
  private MessageService messageService;


  @Autowired
  private BaasConfigClient baasConfigClient;
  @Autowired
  private EmployeeService employeeService;

  @Autowired
  private EvCallEventPublisher publisher;
  @Autowired
  private FmsClient fmsClient;

  @Override
  public int execute(TimedJob job) throws Exception {
    String params = job.getParams();
    if (params == null) {
      log.info("shoptaskremindjob:{},未找到参数", job.getUuid());
      return SUCCESS;
    }
    try {
      ShopTaskRemindJobParam jobParam = BaasJSONUtil.safeToObject(params, ShopTaskRemindJobParam.class);
      String tenant = jobParam.getTenant();
      String shopTaskId = jobParam.getShopTaskId();
      ShopTask shopTask = shopTaskService.get(tenant, shopTaskId);
      if (shopTask == null) {
        log.info("门店任务不存在,任务ID:{}", shopTaskId);
        return FAIL;
      }
      if (ShopTaskState.UNFINISHED.name().equals(shopTask.getState().name())) {
        TaskMessageConfig config = baasConfigClient.getConfig(tenant, TaskMessageConfig.class);

        Message message = new Message();
        message.setTenant(tenant);
        message.setShop(shopTask.getShop());
        message.setShopCode(shopTask.getShopCode());
        message.setShopName(shopTask.getShopName());
        message.setAction(MessageAction.PAGE);
        message.setActionInfo(config.getUsualTaskPage());
        message.setTitle("日常任务");
        message.setType(MessageType.ALERT);
        message.setTag(MessageTag.日常任务);
        Map<MessageContentKey, String> content = new LinkedHashMap<>();
        content.put(MessageContentKey.TEXT, "您有新的任务要执行: 【" + shopTask.getName() + "】");

        message.setContent(content);
        MessageConfig messageConfig = baasConfigClient.getConfig(tenant, MessageConfig.class);
        if (MessageConfig.FMS.equals(messageConfig.getAppMessageVendor())){
          AppMessageSaveNewReq convert = MessageConvertUtil.MESSAGE_TO_APP_MESSAGE_SAVE_NEW_REQ.convert(message);
          convert.setOperateInfo(SopUtils.getSysOperateInfo());
          BaasResponse<Void> response = fmsClient.batchSave(tenant, message.getOrgId(), Collections.singletonList(convert));
          if (!response.isSuccess()) {
            throw new BaasException("发送失败，code：{}，msg：{}", response.getCode(), response.getMsg());
          }
        } else {
          messageService.create(tenant, message, SopUtils.getSysOperateInfo());
        }
        log.info("任务发送未完成提醒,任务id:{}", shopTaskId);
        pushFmsSendMsg(tenant, message.getShop(), content);
        return SUCCESS;
      }
      log.info("门店任务已完成,任务ID:{}", shopTaskId);
      return SUCCESS;
    } catch (Exception e) {
      log.error("shopTaskRemindJob failure,job id:{}", job.getUuid(), e);
      return FAIL;
    }

  }


  private void pushFmsSendMsg(String tenant, String shop, Map<MessageContentKey, String> content) throws BaasException {
    FmsConfig config = baasConfigClient.getConfig(tenant, FmsConfig.class);
    FmsSendMsg fmsSendMsg = new FmsSendMsg();
    if (config.getAcPushTemplateId() == null) {
      log.info("未配置消息中心发布消息模板id");
      return;
    }
    fmsSendMsg.setTenant(tenant);
    fmsSendMsg.setTemplateId(config.getAcPushTemplateId());

    EmployeeFilter filter = new EmployeeFilter();
    filter.setStoreIdEq(shop);
    QueryResult<Employee> employeeQueryResult = employeeService.query(tenant, filter);
    List<Employee> records = employeeQueryResult.getRecords();
    List<String> mobiles = new ArrayList<>();
    if (CollectionUtils.isNotEmpty(records)) {
      mobiles = records.stream().map(Employee::getMobile).filter(StringUtils::isNotBlank).collect(Collectors.toList());
      if (CollectionUtils.isEmpty(mobiles)) {
        log.info("门店{}员工手机号为空", shop);
        return;
      }
    }
    fmsSendMsg.setTarget(mobiles);
    Map<String, String> templateParams = new HashMap<>();
    String contentText = content.get(MessageContentKey.TEXT);
    templateParams.put(FmsConfig.CONTENT, contentText);
    fmsSendMsg.setTemplateParams(templateParams);
    fmsSendMsg.setTraceId(MDC.get("trace_id"));
    log.info("发布推送fms事件");
    publisher.publishForNormal(FmsSendEvCallExecutor.FMS_SEND_EXECUTOR_ID, fmsSendMsg);
  }

  public static String buildTranId() {
    return "shopTask" + "_" + UUID.randomUUID().toString();
  }

}
