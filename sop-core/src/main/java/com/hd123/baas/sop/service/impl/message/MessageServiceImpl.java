package com.hd123.baas.sop.service.impl.message;

import java.util.*;
import java.util.stream.Collectors;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.hd123.baas.sop.annotation.Tx;
import com.hd123.baas.sop.service.api.message.Message;
import com.hd123.baas.sop.service.api.message.MessageService;
import com.hd123.baas.sop.service.api.message.MessageType;
import com.hd123.baas.sop.service.dao.message.MessageDaoBof;
import com.hd123.baas.sop.evcall.EvCallEventPublisher;
import com.hd123.baas.sop.evcall.exector.message.MessageReadEvCallExecutor;
import com.hd123.baas.sop.evcall.exector.message.MessageReadMsg;
import com.hd123.rumba.commons.biz.entity.OperateInfo;
import com.hd123.rumba.commons.biz.query.Cop;
import com.hd123.rumba.commons.biz.query.QueryDefinition;
import com.hd123.rumba.commons.biz.query.QueryResult;
import com.hd123.rumba.commons.lang.Assert;
import com.qianfan123.baas.common.BaasException;

/**
 * @author zhengzewang on 2020/11/9.
 */
@Service
public class MessageServiceImpl implements MessageService {

  @Autowired
  private MessageDaoBof messageDao;
  @Autowired
  private EvCallEventPublisher publisher;

  @Override
  @Tx
  public void create(String tenant, Message message, OperateInfo operateInfo) throws BaasException {
    Assert.hasText(tenant, "tenant");
    Assert.notNull(message, "message");
    Assert.notNull(message.getType(), "message.type");
    Assert.notNull(message.getAction(), "message.action");
    Assert.hasText(message.getTitle(), "message.title");
    messageDao.insert(tenant, message, operateInfo);
  }

  @Override
  public void batchCreate(String tenant, Collection<Message> messages, OperateInfo operateInfo) throws BaasException {
    Assert.hasText(tenant, "tenant");
    if (CollectionUtils.isEmpty(messages)) {
      return;
    }
    messageDao.batchInsert(tenant, messages, operateInfo);
  }

  @Override
  @Tx
  public void read(String tenant, String orgId, String uuid, String appId, OperateInfo operateInfo) throws BaasException {
    Assert.hasText(tenant, "tenant");
    Assert.hasText(uuid, "uuid");
    Assert.hasText(appId, "appId");
    Assert.notNull(operateInfo, "operateInfo");
    Message message = this.get(tenant, uuid);
    if (message == null) {
      throw new BaasException("消息不存在");
    }
    if (message.isRead()) {
      return;
    }
    messageDao.read(tenant, uuid, appId, operateInfo);
    //
    MessageReadMsg msg = new MessageReadMsg();
    msg.setTenant(tenant);
    msg.setOrgId(orgId);
    msg.setPks(Collections.singletonList(uuid));
    publisher.publishForNormal(MessageReadEvCallExecutor.MESSAGE_READ_CREATE_EXECUTOR_ID, msg);
  }

  @Override
  @Tx
  public void readAll(String tenant, String orgId, String shop, MessageType type, String appId, OperateInfo operateInfo) {
    Assert.hasText(tenant, "tenant");
    Assert.hasText(shop, "shop");
    Assert.notNull(type, "type");
    Assert.hasText(appId, "appId");
    Assert.notNull(operateInfo, "operateInfo");
    QueryDefinition qd = new QueryDefinition();
    qd.addByField(Message.Queries.TYPE, Cop.EQUALS, type.name());
    qd.addByField(Message.Queries.READ, Cop.EQUALS, false);
    qd.addByOperation(Message.Queries.OWNERSHIP_IN, shop, operateInfo.getOperator().getId(), appId);
    QueryResult<Message> messageQueryResult = messageDao.query(tenant, qd);
    if (!messageQueryResult.getRecords().isEmpty()) {
      // 通知
      MessageReadMsg msg = new MessageReadMsg();
      msg.setTenant(tenant);
      msg.setOrgId(orgId);
      msg.setPks(messageQueryResult.getRecords().stream().map(Message::getUuid).collect(Collectors.toList()));
      publisher.publishForNormal(MessageReadEvCallExecutor.MESSAGE_READ_CREATE_EXECUTOR_ID, msg);
    }
    messageDao.readAll(tenant, shop, type, appId, operateInfo);
  }

  @Override
  public Map<MessageType, Integer> summary(String tenant, String appId, String shop, String loginId, QueryDefinition qd) {
    Assert.hasText(tenant, "tenant");
    Assert.hasText(shop, "shop");
    Map<MessageType, Integer> map = messageDao.summary(tenant, appId, shop, loginId, qd);
    Map<MessageType, Integer> unread = new LinkedHashMap<>();
    for (MessageType type : MessageType.values()) {
      Integer i = map.get(type);
      if (i == null) {
        unread.put(type, 0);
      } else {
        unread.put(type, i);
      }
    }
    return unread;
  }

  @Override
  public QueryResult<Message> query(String tenant, QueryDefinition qd) {
    Assert.hasText(tenant, "tenant");
    Assert.notNull(qd, "qd");
    return messageDao.query(tenant, qd);
  }

  @Override
  public Message get(String tenant, String uuid) {
    Assert.hasText(tenant, "tenant");
    Assert.hasText(uuid, "uuid");
    return messageDao.get(tenant, uuid);
  }

  @Override
  public void cleanRead(String tenant, int keepDays) {
    Assert.hasText(tenant, "tenant");
    Date beforeDate = DateUtils.addDays(new Date(), 0 - keepDays);
    beforeDate = DateUtils.truncate(beforeDate, Calendar.DATE);
    messageDao.deleteRead(tenant, beforeDate);
  }
}
