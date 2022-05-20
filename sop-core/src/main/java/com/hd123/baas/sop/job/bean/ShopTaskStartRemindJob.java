package com.hd123.baas.sop.job.bean;

import com.hd123.baas.config.core.BaasConfigClient;
import com.hd123.baas.sop.config.MessageConfig;
import com.hd123.baas.sop.config.ShopTaskConfig;
import com.hd123.baas.sop.config.TaskMessageConfig;
import com.hd123.baas.sop.service.impl.task.message.MessageTag;
import com.hd123.baas.sop.service.api.message.*;
import com.hd123.baas.sop.service.api.task.ShopTask;
import com.hd123.baas.sop.service.api.task.ShopTaskLog;
import com.hd123.baas.sop.service.api.task.ShopTaskService;
import com.hd123.baas.sop.service.api.task.ShopTaskState;
import com.hd123.baas.sop.remote.fms.FmsClient;
import com.hd123.baas.sop.remote.fms.bean.AppMessageSaveNewReq;
import com.hd123.baas.sop.remote.fms.bean.MessageConvertUtil;
import com.hd123.baas.sop.utils.JsonUtil;
import com.hd123.rumba.commons.biz.entity.OperateInfo;
import com.hd123.rumba.commons.biz.entity.Operator;
import com.hd123.rumba.commons.biz.query.Cop;
import com.hd123.rumba.commons.biz.query.QueryDefinition;
import com.hd123.rumba.commons.biz.query.QueryResult;
import com.hd123.rumba.commons.util.converter.ConverterUtil;
import com.qianfan123.baas.common.BaasException;
import com.qianfan123.baas.common.http2.BaasResponse;
import com.qianfan123.baas.config.api.entity.ConfigItem;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @Author guyahui
 * @Since
 */
@Slf4j
@Component
public class ShopTaskStartRemindJob implements Job {

  @Value("${sop-service.appId}")
  private String appId;

  @Autowired
  private BaasConfigClient baasConfigClient;
  @Autowired
  private ShopTaskService shopTaskService;
  @Autowired
  private MessageService messageService;
  @Autowired
  private FmsClient fmsClient;

  @Override
  public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
    log.info("执行门店巡检任务开始提醒job");
    Set<String> tenants;
    try {
      tenants = getTenants();
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
    try {
      Date remindTime = new Date();
      for (String tenant : tenants) {
        startRemind(tenant, remindTime);
      }
    } catch (Exception e) {
      log.error("执行门店巡检任务开始提醒job异常：" + JsonUtil.objectToJson(e));
    }
  }

  public void startRemind(String tenant, Date remindTime) throws BaasException {
    log.info("开始执行租户<{}>任务开始提醒，提醒时间：{}", tenant, remindTime);
    List<ShopTask> shopTasks = shopTaskService.listByStartTime(tenant, remindTime);
    log.info("需要进行开始提醒的任务：{}", JsonUtil.objectToJson(shopTasks));
    if (CollectionUtils.isNotEmpty(shopTasks)) {
      for (ShopTask shopTask : shopTasks) {
        List<ShopTaskLog> logs = shopTaskService.logList(tenant, shopTask.getUuid());
        logs = logs != null
            ? logs.stream().filter(s -> s.getState().equals(ShopTaskState.NOT_STARTED.name())).collect(Collectors.toList())
            : null;
        if (CollectionUtils.isEmpty(logs)) {
          continue;
        }
        Set<String> operators = logs.stream().map(ShopTaskLog::getOperatorId).collect(Collectors.toSet());
        sendCheckMessage(tenant, shopTask, operators, getSysOperateInfo());
        //更改任务状态、shopTaskLog为未完成
        shopTaskService.changeShopTaskState(tenant, shopTask.getUuid(), ShopTaskState.UNFINISHED, getSysOperateInfo());
      }
    }
  }

  public void sendCheckMessage(String tenant, ShopTask shopTask, Set<String> userIds, OperateInfo operateInfo)
      throws BaasException {
    if (CollectionUtils.isEmpty(userIds)) {
      return;
    }
    TaskMessageConfig config = baasConfigClient.getConfig(tenant, TaskMessageConfig.class);
    List<Message> messageList = new ArrayList<>();
    for (String userId : userIds) {
      Message message = buildCheckMessage(tenant, shopTask, userId, config.getCheckTaskPage());
      messageList.add(message);
    }

    MessageConfig messageConfig = baasConfigClient.getConfig(tenant, MessageConfig.class);
    if (MessageConfig.FMS.equals(messageConfig.getAppMessageVendor())) {
      List<AppMessageSaveNewReq> reqs = ConverterUtil.convert(messageList, MessageConvertUtil.MESSAGE_TO_APP_MESSAGE_SAVE_NEW_REQ);
      reqs.forEach(req->{req.setOperateInfo(this.getSysOperateInfo());});
      BaasResponse<Void> response = fmsClient.batchSave(tenant, shopTask.getOrgId(), reqs);
      if (!response.isSuccess()) {
        throw new BaasException("发送失败，code：{}，msg：{}", response.getCode(), response.getMsg());
      }
    } else {
      messageService.batchCreate(tenant, messageList, operateInfo);
    }
  }

  private Message buildCheckMessage(String tenant, ShopTask shopTask, String userId, String checkTaskPage) {
    Message message = new Message();
    message.setTenant(tenant);
    message.setShop(shopTask.getShop());
    message.setShopCode(shopTask.getShopCode());
    message.setShopName(shopTask.getShopName());
    message.setAction(MessageAction.PAGE);
    message.setActionInfo(buildUrl(checkTaskPage, shopTask.getPlan(), shopTask.getPlanPeriod()));
    message.setTitle("巡检计划");
    message.setType(MessageType.ALERT);
    message.setTag(MessageTag.巡检计划);
    message.setSource(shopTask.getUuid());
    Map<MessageContentKey, String> content = new LinkedHashMap<>();
    content.put(MessageContentKey.TEXT, "您有巡检计划开始了:" + shopTask.getPlanName());
    message.setContent(content);
    message.setUserId(userId);
    return message;
  }

  private String buildUrl(String page, String plan, String planPeriodCode) {
    return page + "?plan=" + plan + "&periodCode=" + planPeriodCode;
  }

  /**
   * 获取所有租户
   *
   * @return 租户列表
   */
  public Set<String> getTenants() throws Exception {
    QueryDefinition qd = new QueryDefinition();
    qd.addByField(ConfigItem.Queries.KEY, Cop.EQUALS, ShopTaskConfig.SHOP_TASK_ENABLED);
    qd.addByField(ConfigItem.Queries.APP_ID, Cop.EQUALS, appId);
    QueryResult<ConfigItem> result = baasConfigClient.query(qd);
    if (result != null) {
      return result.getRecords().stream().map(i -> i.getTenant()).collect(Collectors.toSet());
    }
    return null;
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
