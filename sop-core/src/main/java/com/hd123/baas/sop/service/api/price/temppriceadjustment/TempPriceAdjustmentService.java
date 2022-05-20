package com.hd123.baas.sop.service.api.price.temppriceadjustment;

import com.hd123.rumba.commons.biz.entity.OperateInfo;
import com.hd123.rumba.commons.biz.query.QueryDefinition;
import com.hd123.rumba.commons.biz.query.QueryResult;
import com.qianfan123.baas.common.BaasException;

/**
 * @Author maodapeng
 * @Since
 */
public interface TempPriceAdjustmentService {
  /**
   * 创建临时改价单
   * @param tenant
   * @param operateInfo
   * @return
   * @throws BaasException
   */
  TempPriceAdjustment create(String tenant,String orgId, OperateInfo operateInfo);

  /**
   *
   * @param tenant
   * @param uuid
   * @return
   */
  TempPriceAdjustment get(String tenant,String uuid);

  /**
   * 保存临时改价单
   * @param tenant
   * @param temp
   * @param operateInfo
   */
  void save(String tenant,TempPriceAdjustment temp,OperateInfo operateInfo) throws BaasException;

  /**
   * 查询临时改价单
   * @param tenant
   * @param qd
   * @return
   */
  QueryResult<TempPriceAdjustment> query(String tenant, QueryDefinition qd);

  /**
   * 查询改价单中的门店信息 支持分页
   * @param tenant
   * @param uuid
   * @param qd
   * @return
   */
  QueryResult<TempShop> queryShop(String tenant, String uuid,QueryDefinition qd);

  /**
   * 发布历史改价信息
   * @param tenant
   * @param uuid
   * @param operateInfo
   * @throws BaasException
   */
  void publish(String tenant, String uuid, OperateInfo operateInfo) throws BaasException;
}
