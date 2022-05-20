package com.hd123.baas.sop.jmzs.shopdailysale.api;

import com.hd123.rumba.commons.biz.entity.OperateInfo;
import com.hd123.rumba.commons.biz.query.QueryDefinition;
import com.hd123.rumba.commons.biz.query.QueryResult;

public interface ShopDailySaleInfoService {
  /**
   * 查询
   * @param tenant
   *        租户
   * @param qd
   *        查询条件
   * @return
   */
  QueryResult<ShopDailySaleInfo> query(String tenant, QueryDefinition qd);

  /**
   * 获取
   *
   * @param tenant
   *        租户
   * @param uuid
   *        标识
   * @return
   */
  ShopDailySaleInfo get(String tenant, String uuid);

  /**
   * 新建
   *
   * @param tenant
   *        租户
   * @param saleInfo
   *        新建数据
   * @param operateInfo
   *        操作上下问
   * @return
   * @throws Exception
   */
  String saveNew(String tenant, ShopDailySaleInfo saleInfo, OperateInfo operateInfo) throws Exception;


  /**
   * 门店盈亏账提交
   *
   * @param tenant
   * @param uuid
   * @param operateInfo
   * @throws Exception
   */
  void submit(String tenant, String uuid, OperateInfo operateInfo) throws Exception;

  /**
   * 门店盈亏账编辑
   *
   * @param tenant
   * @param shopDailySaleInfo
   * @param operateInfo
   */
  void saveModify(String tenant, ShopDailySaleInfo shopDailySaleInfo, OperateInfo operateInfo) throws Exception;

}
