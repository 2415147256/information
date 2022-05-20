package com.hd123.baas.sop.service.api.activity;

import java.util.Collection;
import java.util.Date;
import java.util.List;

import com.hd123.baas.sop.service.api.pms.activity.PromActivity;
import com.hd123.rumba.commons.biz.entity.OperateInfo;
import com.qianfan123.baas.common.BaasException;

/**
 * @Author maodapeng
 * @Since
 */
public interface PromActivityService {
  /**
   * 获取活动详情
   * 
   * @param tenant
   * @param uuid
   * @return
   */
  PromActivity get(String tenant, String uuid, String... parts);

  /**
   * 活动列表
   * 
   * @param tenant
   * @param uuids
   * @param fetchParts
   * @return
   */
  List<PromActivity> list(String tenant, Collection<String> uuids, String... fetchParts);

  /**
   * 终止
   * 
   * @param tenant
   * @param uuid
   * @throws BaasException
   */
  void stopped(String tenant, String uuid, OperateInfo operateInfo) throws BaasException;

  /**
   * 取消
   * 
   * @param tenant
   * @param uuid
   * @throws BaasException
   */
  void cancel(String tenant, String uuid, OperateInfo operateInfo) throws BaasException;

  /**
   * 审核
   * 
   * @param tenant
   * @param uuid
   * @param operateInfo
   * @throws BaasException
   */
  void audit(String tenant, String uuid, OperateInfo operateInfo) throws BaasException;

  /**
   * 自动审核
   * 
   * @param tenant
   * @param uuid
   * @param operateInfo
   * @throws BaasException
   */
  void autoAudit(String tenant, String uuid, OperateInfo operateInfo);

  /**
   * 促销活动查询
   * 
   * @param tenant
   *          租户
   * @param shopCode
   *          门店编码
   * @param effectDate
   *          生效时间
   */
  List<PromActivity> query(String tenant, String shopCode, Date effectDate);

}
