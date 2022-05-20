package com.hd123.baas.sop.job.bean;

import com.hd123.baas.config.core.BaasConfigClient;
import com.hd123.baas.sop.config.MessageConfig;
import com.hd123.baas.sop.config.ShopTaskConfig;
import com.hd123.baas.sop.config.TaskMessageConfig;
import com.hd123.baas.sop.service.api.message.Message;
import com.hd123.baas.sop.service.api.message.MessageAction;
import com.hd123.baas.sop.service.api.message.MessageContentKey;
import com.hd123.baas.sop.service.api.message.MessageService;
import com.hd123.baas.sop.service.api.message.MessageType;
import com.hd123.baas.sop.service.impl.task.message.MessageTag;
import com.hd123.baas.sop.service.api.task.ShopTask;
import com.hd123.baas.sop.service.api.task.ShopTaskService;
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
import org.apache.commons.lang.StringUtils;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @Author guyahui
 * @Since
 */
@Slf4j
@Component
public class ShopTaskAssignableExpireRemindJob implements Job {

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
    log.info("执行普通任务过期提醒job");
    Set<String> tenants;
    try {
      tenants = getTenants();
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
    try {
      Date remindTime = new Date();
      for (String tenant : tenants) {
        expireRemind(tenant, remindTime);
      }
    } catch (Exception e) {
      log.error("执行门店任务过期提醒job异常：" + JsonUtil.objectToJson(e));
    }
  }

  public void expireRemind(String tenant, Date remindTime) throws BaasException {
    log.info("开始执行租户<{}>任务提醒，提醒时间：{}", tenant, remindTime);
    List<ShopTask> shopTasks = shopTaskService.listAssignableByRemindTime(tenant, remindTime);
    log.info("需要提醒的任务：{}", JsonUtil.objectToJson(shopTasks));
    if (CollectionUtils.isNotEmpty(shopTasks)) {
      List<Message> messageList = new ArrayList<>();
      for (ShopTask shopTask : shopTasks) {
        if (StringUtils.isEmpty(shopTask.getOperatorId())) {
          return;
        }
        TaskMessageConfig config = baasConfigClient.getConfig(tenant, TaskMessageConfig.class);
        String path = config.getAssignableShopTaskRemindPage() + "?uuid=" + shopTask.getUuid();
        Message message = buildCheckMessage(tenant, shopTask, path);
        messageList.add(message);
      }
      MessageConfig messageConfig = baasConfigClient.getConfig(tenant, MessageConfig.class);
      if (MessageConfig.FMS.equals(messageConfig.getAppMessageVendor())) {
        List<AppMessageSaveNewReq> reqs = ConverterUtil.convert(messageList, MessageConvertUtil.MESSAGE_TO_APP_MESSAGE_SAVE_NEW_REQ);
        reqs.forEach(req->{req.setOperateInfo(this.getSysOperateInfo());});
        BaasResponse<Void> response = fmsClient.batchSave(tenant, shopTasks.get(0).getOrgId(), reqs);
        if (!response.isSuccess()) {
          throw new BaasException("发送失败，code：{}，msg：{}", response.getCode(), response.getMsg());
        }
      } else {
        messageService.batchCreate(tenant, messageList, getSysOperateInfo());
      }
    }
  }

  private Message buildCheckMessage(String tenant, ShopTask shopTask, String checkTaskPage) {
    Message message = new Message();
    message.setTenant(tenant);
    message.setShop(shopTask.getShop());
    message.setShopCode(shopTask.getShopCode());
    message.setShopName(shopTask.getShopName());
    message.setAction(MessageAction.PAGE);
    message.setActionInfo(checkTaskPage);
    message.setTitle("任务临期提醒");
    message.setType(MessageType.ALERT);
    message.setTag(MessageTag.普通任务过期提醒);
    message.setSource(shopTask.getUuid());
    Map<MessageContentKey, String> content = new LinkedHashMap<>();
    content.put(MessageContentKey.TEXT, "您有一个任务:" + shopTask.getName() + "即将过期");
    message.setContent(content);
    message.setUserId(shopTask.getOperatorId());
    return message;
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
