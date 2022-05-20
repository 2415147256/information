package com.hd123.baas.sop.service.api.price.tempshoppriceadjustment;

import java.util.List;

import com.hd123.rumba.commons.biz.entity.OperateInfo;
import com.hd123.rumba.commons.biz.query.QueryDefinition;
import com.hd123.rumba.commons.biz.query.QueryResult;
import com.qianfan123.baas.common.BaasException;

/**
 * @Author maodapeng
 * @Since
 */
public interface TempShopPriceAdjustmentService {
  /**
   * 创建到店价临时改价单
   * 
   * @param tenant
   * @param operateInfo
   * @return
   * @throws BaasException
   */
  TempShopPriceAdjustment create(String tenant, String orgId, OperateInfo operateInfo);

  /**
   *
   * @param tenant
   * @param uuid
   * @return
   */
  TempShopPriceAdjustment get(String tenant, String uuid);

  /**
   * 保存到店价临时改价单
   * 
   * @param tenant
   * @param temp
   * @param operateInfo
   */
  void save(String tenant, TempShopPriceAdjustment temp, OperateInfo operateInfo) throws BaasException;

  /**
   * 查询到店价临时改价单
   * 
   * @param tenant
   * @param qd
   * @return
   */
  QueryResult<TempShopPriceAdjustment> query(String tenant, QueryDefinition qd);

  /**
   * 终止
   * 
   * @param tenant
   * @param uuid
   * @param operateInfo
   * @throws BaasException
   */
  void terminate(String tenant, String uuid, OperateInfo operateInfo) throws BaasException;

  /**
   * 清空行
   */
  /**
   * 终止
   *
   * @param tenant
   * @param uuid
   * @param operateInfo
   * @throws BaasException
   */
  void delete(String tenant, String uuid, OperateInfo operateInfo) throws BaasException;
  /**
   * 新增行
   * 
   * @param tenant
   * @param owner
   * @param lines
   */
  void addLiens(String tenant, String owner, List<TempShopPriceAdjustmentLine> lines);

  /**
   * 获取行
   */
  List<TempShopPriceAdjustmentLine> getLines(String tenant, String owner);

  /**
   *
   * @param tenant
   * @param uuid
   * @param operateInfo
   */
  void publish(String tenant, String uuid, OperateInfo operateInfo);

}
