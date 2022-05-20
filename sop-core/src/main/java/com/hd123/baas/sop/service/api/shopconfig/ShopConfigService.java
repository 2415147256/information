package com.hd123.baas.sop.service.api.shopconfig;

import com.qianfan123.baas.common.BaasException;

import java.util.List;

/**
 * @Author maodapeng
 * @Since
 */
public interface ShopConfigService {
  /**
   * 根据shopId查询门店配置
   * 
   * @param tenant
   * @param shopIds
   * @return
   */
  List<ShopConfig> list(String tenant, List<String> shopIds);

  /**
   * 批量保存门店配置 重复则更新
   * 
   * @param tenant
   * @param shopConfig
   */
  void save(String tenant, ShopConfig shopConfig) throws BaasException;

}
