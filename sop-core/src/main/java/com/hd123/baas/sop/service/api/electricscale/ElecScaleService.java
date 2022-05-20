package com.hd123.baas.sop.service.api.electricscale;

import java.util.List;

import com.hd123.rumba.commons.biz.entity.OperateInfo;
import com.hd123.rumba.commons.biz.query.QueryDefinition;
import com.hd123.rumba.commons.biz.query.QueryResult;
import com.qianfan123.baas.common.BaasException;

public interface ElecScaleService {

  /**
   * 新增电子秤
   */
  String saveNew(String tenant, ShopElecScale scale, OperateInfo operateInfo) throws BaasException;

  /**
   * 修改电子秤
   */
  void saveModify(String tenant, ShopElecScale scale, OperateInfo operateInfo) throws BaasException;

  /**
   * 获取指定电子秤
   */
  ShopElecScale get(String tenant, String uuid);

  /**
   * 删除电子秤
   */
  void delete(String tenant, String uuid) throws BaasException;

  /**
   * 查询电子秤
   */
  QueryResult<ShopElecScale> query(String tenant, QueryDefinition qd);

  /**
   * 批量查询电子传秤状态
   */
  List<ElecScaleState> stateListData(String tenant, List<String> electricScaleUUIDs);

  /**
   * 根据门店code和电子秤uuid获取电子秤
   */
  ShopElecScale getByShopCodeAndUUid(String tenant, String shopCode, String uuid);

  /**
   * 获取门店下的所有的电子秤
   */
  List<ShopElecScale> listByShopCode(String tenant, String shopCode);

  /**
   * 批量查询电子模板下发状态
   */
  List<ElecScaleState> stateListTemplate(String tenant, List<String> list);

  /**
   * 获取电子秤设备型号
   */
  List<ElecScale> listElecScale(String tenant);

}
