package com.hd123.baas.sop.evcall.exector.message;

import com.hd123.baas.config.core.BaasConfigClient;
import com.hd123.baas.sop.annotation.Tx;
import com.hd123.baas.sop.config.MessageConfig;
import com.hd123.baas.sop.service.api.announcement.AnnouncementService;
import com.hd123.baas.sop.service.api.message.Message;
import com.hd123.baas.sop.service.api.message.MessageService;
import com.hd123.baas.sop.service.api.message.MessageType;
import com.hd123.baas.sop.evcall.AbstractEvCallExecutor;
import com.hd123.baas.sop.remote.fms.FmsClient;
import com.hd123.baas.sop.remote.fms.bean.BAppMessage;
import com.hd123.baas.sop.remote.fms.bean.MessageConvertUtil;
import com.hd123.baas.sop.utils.BaasJSONUtil;
import com.hd123.rumba.commons.biz.entity.OperateInfo;
import com.hd123.rumba.commons.biz.entity.Operator;
import com.hd123.rumba.commons.biz.query.Cop;
import com.hd123.rumba.commons.biz.query.QueryDefinition;
import com.hd123.rumba.commons.util.converter.ConverterUtil;
import com.hd123.rumba.evcall.EvCallExecutionContext;
import com.qianfan123.baas.common.BaasException;
import com.qianfan123.baas.common.http.FilterParam;
import com.qianfan123.baas.common.http.QueryRequest;
import com.qianfan123.baas.common.http2.BaasResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author zhengzewang on 2020/11/22.
 */
@Slf4j
@Component
public class MessageReadEvCallExecutor extends AbstractEvCallExecutor<MessageReadMsg> {

  public static final String MESSAGE_READ_CREATE_EXECUTOR_ID = MessageReadEvCallExecutor.class.getSimpleName();

  @Autowired
  private MessageService messageService;
  @Autowired
  private AnnouncementService announcementService;
  @Autowired
  private BaasConfigClient baasConfigClient;
  @Autowired
  private FmsClient fmsClient;

  @Override
  @Tx
  protected void doExecute(MessageReadMsg msg, EvCallExecutionContext context) throws Exception {
    String tenant = msg.getTenant();
    Collection<String> pks = msg.getPks();
    String orgId = msg.getOrgId();
    if (CollectionUtils.isEmpty(pks)) {
      return;
    }
    List<Message> messageList;
    MessageConfig messageConfig = baasConfigClient.getConfig(tenant, MessageConfig.class);
    if (MessageConfig.FMS.equals(messageConfig.getAppMessageVendor())) {
      QueryRequest request = new QueryRequest();
      request.setLimit(-1);
      FilterParam param = new FilterParam();
      param.setProperty("uuid:in");
      param.setValue(pks);
      request.setFilters(Collections.singletonList(param));
      BaasResponse<List<BAppMessage>> query = fmsClient.query(tenant, request);
      if (query.isSuccess()) {
        messageList = ConverterUtil.convert(query.getData(), MessageConvertUtil.APP_MESSAGE_TO_MESSAGE);
      } else {
        throw new BaasException("访问fms失败，code：{}，msg：{}", query.getCode(), query.getMsg());
      }
    } else {
      QueryDefinition qd = new QueryDefinition();
      qd.addByField(Message.Queries.UUID, Cop.IN, pks.toArray());
      messageList = messageService.query(tenant, qd).getRecords();
    }

    Set<String> announcements = messageList.stream()
        .filter(m -> MessageType.ANNOUNCEMENT == m.getType() && StringUtils.isNotBlank(m.getSource()))
        .map(Message::getSource)
        .collect(Collectors.toSet());
    if (announcements.isEmpty()) {
      return;
    }
    //
    for (String announcement : announcements) {
      announcementService.noticeRead(tenant, orgId, announcement, getOperateInfo());
    }
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
  protected MessageReadMsg decodeMessage(String msg) throws BaasException {
    log.info("收到MessageReadMsg:{}", msg);
    return BaasJSONUtil.safeToObject(msg, MessageReadMsg.class);
  }
}
