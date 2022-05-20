package com.hd123.baas.sop.service.api.message;

import java.util.Collection;
import java.util.Map;

import com.hd123.rumba.commons.biz.entity.OperateInfo;
import com.hd123.rumba.commons.biz.query.QueryDefinition;
import com.hd123.rumba.commons.biz.query.QueryResult;
import com.qianfan123.baas.common.BaasException;

/**
 * @author zhengzewang on 2020/11/9.
 */
public interface MessageService {

  /**
   * 创建消息
   * 
   * @param tenant
   *          租户
   * @param message
   *          消息
   * @param operateInfo
   *          操作人信息
   */
  void create(String tenant, Message message, OperateInfo operateInfo) throws BaasException;

  /**
   * 批量创建消息
   * 
   * @param tenant
   *          租户
   * @param messages
   *          消息
   * @param operateInfo
   *          操作人
   */
  void batchCreate(String tenant, Collection<Message> messages, OperateInfo operateInfo) throws BaasException;

  /**
   * 阅读
   * 
   * @param tenant
   * @param uuid
   * @param appId
   * @param operateInfo
   */
  void read(String tenant, String orgId, String uuid, String appId, OperateInfo operateInfo) throws BaasException;

  /**
   * 阅读某个类型下全部
   * 
   * @param tenant
   *          租户
   * @param shop
   *          门店
   * @param type
   *          类型
   * @param appId
   *          设备
   * @param operateInfo
   *          操作人
   */
  void readAll(String tenant, String orgId, String shop, MessageType type, String appId, OperateInfo operateInfo);

  /**
   * 未读数合计
   * 
   * @param tenant
   *          租户
   * @param shop
   *          门店
   * @return 未读数合计
   */
  Map<MessageType, Integer> summary(String tenant, String appId, String shop, String loginId, QueryDefinition qd);

  /**
   * 列表查询
   * 
   * @param tenant
   *          租户
   * @param qd
   *          查询条件
   * @return 响应信息
   */
  QueryResult<Message> query(String tenant, QueryDefinition qd);

  /**
   * 详情
   * 
   * @param tenant
   *          租户
   * @param uuid
   *          uuid
   * @return 消息
   */
  Message get(String tenant, String uuid);

  /**
   * 清除已读消息
   * 
   * @param tenant
   *          租户
   * @param keepDays
   *          保留天数
   */
  void cleanRead(String tenant, int keepDays);

}
