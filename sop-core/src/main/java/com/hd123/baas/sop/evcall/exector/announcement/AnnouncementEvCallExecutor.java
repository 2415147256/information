package com.hd123.baas.sop.evcall.exector.announcement;

import com.hd123.baas.config.core.BaasConfigClient;
import com.hd123.baas.sop.annotation.Tx;
import com.hd123.baas.sop.remote.fms.bean.AppMsgSNReq;
import com.hd123.baas.sop.service.api.announcement.AnnouncementTargetType;
import com.hd123.baas.sop.service.api.basedata.employee.Employee;
import com.hd123.baas.sop.service.api.basedata.employee.EmployeeFilter;
import com.hd123.baas.sop.service.api.basedata.employee.EmployeeService;
import com.hd123.baas.sop.config.FmsConfig;
import com.hd123.baas.sop.config.MessageConfig;
import com.hd123.baas.sop.service.api.announcement.Announcement;
import com.hd123.baas.sop.service.api.announcement.AnnouncementService;
import com.hd123.baas.sop.service.api.announcement.AnnouncementShop;
import com.hd123.baas.sop.service.api.announcement.AnnouncementState;
import com.hd123.baas.sop.service.api.message.Message;
import com.hd123.baas.sop.service.api.message.MessageAction;
import com.hd123.baas.sop.service.api.message.MessageContentKey;
import com.hd123.baas.sop.service.api.message.MessageService;
import com.hd123.baas.sop.service.api.message.MessageType;
import com.hd123.baas.sop.evcall.AbstractEvCallExecutor;
import com.hd123.baas.sop.evcall.EvCallEventPublisher;
import com.hd123.baas.sop.evcall.exector.fms.FmsSendEvCallExecutor;
import com.hd123.baas.sop.evcall.exector.fms.FmsSendMsg;
import com.hd123.baas.sop.remote.fms.FmsClient;
import com.hd123.baas.sop.remote.fms.bean.AppMessageSaveNewReq;
import com.hd123.baas.sop.remote.fms.bean.BAppMessage;
import com.hd123.baas.sop.remote.fms.bean.MessageConvertUtil;
import com.hd123.baas.sop.utils.BaasJSONUtil;
import com.hd123.rumba.commons.biz.entity.OperateInfo;
import com.hd123.rumba.commons.biz.entity.Operator;
import com.hd123.rumba.commons.biz.query.Cop;
import com.hd123.rumba.commons.biz.query.QueryDefinition;
import com.hd123.rumba.commons.biz.query.QueryResult;
import com.hd123.rumba.commons.util.converter.ConverterUtil;
import com.hd123.rumba.evcall.EvCallExecutionContext;
import com.qianfan123.baas.common.BaasException;
import com.qianfan123.baas.common.http.FilterParam;
import com.qianfan123.baas.common.http.QueryRequest;
import com.qianfan123.baas.common.http2.BaasResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author zhengzewang on 2020/11/18.
 */
@Slf4j
@Component
public class AnnouncementEvCallExecutor extends AbstractEvCallExecutor<AnnouncementMsg> {

  public static final String ANNOUNCEMENT_CREATE_EXECUTOR_ID = AnnouncementEvCallExecutor.class.getSimpleName();

  @Autowired
  private MessageService messageService;
  @Autowired
  private AnnouncementService announcementService;
  @Autowired
  private EmployeeService employeeService;

  @Autowired
  private EvCallEventPublisher publisher;
  @Autowired
  private BaasConfigClient baasConfigClient;
  @Autowired
  private FmsClient fmsClient;

  @Override
  @Tx
  protected void doExecute(AnnouncementMsg msg, EvCallExecutionContext context) throws Exception {
    String tenant = msg.getTenant();
    String pk = msg.getPk();
    String orgId = msg.getOrgId();
    Announcement announcement = announcementService.get(tenant, pk);
    if (announcement == null) {
      log.info("公告 {} 不存在，忽略", pk);
      return;
    }
    if (AnnouncementState.UNPUBLISHED == announcement.getState()) {
      log.info("公告 {} 未发布，忽略", pk);
      return;
    }
    MessageConfig messageConfig = baasConfigClient.getConfig(tenant, MessageConfig.class);
    if (MessageConfig.FMS.equals(messageConfig.getAppMessageVendor())) {
      QueryRequest request = new QueryRequest();
      List<FilterParam> filterParams = new ArrayList<>();
      FilterParam param = new FilterParam();
      param.setProperty("source:=");
      param.setValue(pk);
      filterParams.add(param);
      request.setFilters(filterParams);
      request.setStart(0);
      request.setLimit(1);
      BaasResponse<List<BAppMessage>> query = fmsClient.query(tenant, request);
      if (!query.isSuccess()) {
        throw new BaasException("访问fms异常，code：{}，msg：{}", query.getCode(), query.getMsg());
      } else {
        if (!query.getData().isEmpty()) {
          log.info("公告 {} 对应的消息已生成，忽略", pk);
          return;
        }
      }
    } else {
      QueryDefinition qd = new QueryDefinition();
      qd.addByField(Message.Queries.SOURCE, Cop.EQUALS, pk);
      qd.setPage(0);
      qd.setPageSize(1);
      if (!messageService.query(tenant, qd).getRecords().isEmpty()) {
        log.info("公告 {} 对应的消息已生成，忽略", pk);
        return;
      }
    }

    List<Message> messages = new ArrayList<>();
    List<AnnouncementShop> shops = announcementService.listShops(tenant, pk);
    for (AnnouncementShop shop : shops) {
      Message message = new Message();
      message.setShop(shop.getShop());
      message.setShopCode(shop.getShopCode());
      message.setShopName(shop.getShopName());
      message.setType(MessageType.ANNOUNCEMENT);
      message.setAction(MessageAction.DETAIL);
      message.setTitle(announcement.getTitle());
      message.setOrgId(orgId);
      Map<MessageContentKey, String> content = new LinkedHashMap<>();
      content.put(MessageContentKey.TEXT, announcement.getContent());
      if (StringUtils.isNotEmpty(announcement.getImage())) {
        content.put(MessageContentKey.IMAGE, announcement.getImage());
      }
      if (StringUtils.isNotBlank(announcement.getUrl())) {
        content.put(MessageContentKey.URL, announcement.getUrl());
      }
      message.setContent(content);

      message.setSource(pk);
      message.setSendPos(announcement.isSendPos());
      messages.add(message);
    }
    if (AnnouncementTargetType.FRANCHISEE.equals(announcement.getTargetType())) {
      List<AppMsgSNReq> reqs = ConverterUtil.convert(messages, MessageConvertUtil.MESSAGE_TO_APP_MESSAGE_SAVE_NEW_REQ_v2);
      BaasResponse<Void> response = fmsClient.batchSaveV2(tenant, orgId, reqs);
      if (!response.isSuccess()) {
        throw new BaasException("发送失败，code：{}，msg：{}", response.getCode(), response.getMsg());
      }
      return;
    }
    if (MessageConfig.FMS.equals(messageConfig.getAppMessageVendor())) {
      List<AppMessageSaveNewReq> reqs = ConverterUtil.convert(messages, MessageConvertUtil.MESSAGE_TO_APP_MESSAGE_SAVE_NEW_REQ);
      reqs.forEach(req->{req.setOperateInfo(this.getSysOperateInfo());});
      BaasResponse<Void> response = fmsClient.batchSave(tenant, orgId, reqs);
      if (!response.isSuccess()) {
        throw new BaasException("发送失败，code：{}，msg：{}", response.getCode(), response.getMsg());
      }
    } else {
      messageService.batchCreate(tenant, messages, getOperateInfo());
    }
    for (Message message : messages) {
      pushFmsSendMsg(tenant, message.getShop(), message.getTitle());
    }
  }

  private void pushFmsSendMsg(String tenant, String shop, String title) throws BaasException {
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
    templateParams.put(FmsConfig.CONTENT, title);
    fmsSendMsg.setTemplateParams(templateParams);
    fmsSendMsg.setTraceId(MDC.get("trace_id"));
    log.info("发布推送fms事件");
    publisher.publishForNormal(FmsSendEvCallExecutor.FMS_SEND_EXECUTOR_ID, fmsSendMsg);
  }

  private OperateInfo getOperateInfo() {
    OperateInfo operateInfo = new OperateInfo();
    Operator operator = new Operator();
    operateInfo.setTime(new Date());
    operator.setId("system");
    operator.setFullName("系统用户");
    operator.setNamespace("system");
    operateInfo.setOperator(operator);
    return operateInfo;
  }

  @Override
  protected AnnouncementMsg decodeMessage(String msg) throws BaasException {
    log.info("收到AnnouncementMsg:{}", msg);
    return BaasJSONUtil.safeToObject(msg, AnnouncementMsg.class);
  }

}
