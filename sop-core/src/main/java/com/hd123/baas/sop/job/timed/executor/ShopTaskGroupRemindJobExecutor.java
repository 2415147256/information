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
import com.hd123.baas.sop.service.api.task.ShopTaskGroup;
import com.hd123.baas.sop.service.api.task.ShopTaskGroupService;
import com.hd123.baas.sop.service.api.task.ShopTaskGroupState;
import com.hd123.baas.sop.evcall.EvCallEventPublisher;
import com.hd123.baas.sop.evcall.exector.fms.FmsSendEvCallExecutor;
import com.hd123.baas.sop.evcall.exector.fms.FmsSendMsg;
import com.hd123.baas.sop.job.entity.TimedJob;
import com.hd123.baas.sop.job.timed.ShopTaskGroupRemindJobParam;
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
public class ShopTaskGroupRemindJobExecutor implements TimedJobExecutor {
  public static final int SUCCESS = 0;
  public static final int FAIL = -1;

  public static final String BEAN_NAME = "shopTaskGroupRemindJobExecutor";

  @Autowired
  private ShopTaskGroupService shopTaskGroupService;

  @Autowired
  private BaasConfigClient baasConfigClient;

  @Autowired
  private MessageService messageService;
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
      ShopTaskGroupRemindJobParam param = BaasJSONUtil.safeToObject(params, ShopTaskGroupRemindJobParam.class);
      String tenant = param.getTenant();
      String shopTaskGroupId = param.getShopTaskGroupId();
      ShopTaskGroup shopTaskGroup = shopTaskGroupService.get(tenant, shopTaskGroupId);
      if (shopTaskGroup == null) {
        log.info("门店任务组不存在,任务组id:{}", shopTaskGroupId);
        return SUCCESS;
      }
      if (ShopTaskGroupState.UNFINISHED.name().equals(shopTaskGroup.getState().name())) {

        TaskMessageConfig config = baasConfigClient.getConfig(tenant, TaskMessageConfig.class);

        Message message = new Message();
        message.setTenant(tenant);
        message.setShop(shopTaskGroup.getShop());
        message.setShopCode(shopTaskGroup.getShopCode());
        message.setShopName(shopTaskGroup.getShopName());
        message.setAction(MessageAction.PAGE);
        message.setActionInfo(config.getDailyTaskPage());
        message.setTitle("门店日结");
        message.setType(MessageType.ALERT);
        message.setTag(MessageTag.日结任务);
        Map<MessageContentKey, String> content = new LinkedHashMap<>();
        content.put(MessageContentKey.TEXT, "您今日的门店日结尚未完成");
        message.setContent(content);
        MessageConfig messageConfig = baasConfigClient.getConfig(tenant, MessageConfig.class);
        if (MessageConfig.FMS.equals(messageConfig.getAppMessageVendor())) {
          AppMessageSaveNewReq convert = MessageConvertUtil.MESSAGE_TO_APP_MESSAGE_SAVE_NEW_REQ.convert(message);
          convert.setOperateInfo(SopUtils.getSysOperateInfo());
          BaasResponse<Void> response = fmsClient.batchSave(tenant, message.getOrgId(), Collections.singletonList(convert));
          if (!response.isSuccess()) {
            throw new BaasException("发送失败，code：{}，msg：{}", response.getCode(), response.getMsg());
          }
        } else {
          messageService.create(tenant, message, SopUtils.getSysOperateInfo());
        }
        pushFmsSendMsg(tenant, shopTaskGroup.getShop(), message.getContent());
        log.info("任务组发送未完成提醒,任务组id:{}", shopTaskGroupId);
        return SUCCESS;
      }
      log.info("任务组以完成,任务组id:{}", shopTaskGroupId);
      return SUCCESS;
    } catch (Exception e) {
      log.error("shopTaskGroupRemindJob failure,job id:{}", job.getUuid(), e);
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
        log.info("门店{0}员工手机号为空", shop);
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
    return "shopTaskGroup" + "_" + UUID.randomUUID().toString();
  }

}
