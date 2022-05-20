package com.hd123.baas.sop.service.api.price.shopprice;

import java.util.Collection;

import com.hd123.baas.sop.service.api.price.shopprice.bean.ShopPricePromotion;

/**
 * @author zhengzewang on 2020/11/19.
 */
public interface ShopPricePromotionService {

  /**
   * 有则更新，无则插入（门店+skuGroup+skuPosition唯一）
   *
   * @param tenant
   *          租户
   * @param grades
   *          价格级
   */
  void batchSave(String tenant, Collection<ShopPricePromotion> grades);

  /**
   * 根据商品获取当前到店价促销
   * 
   * @param tenant
   *          租户
   * @param shop
   *          门店
   * @param skuId
   *          商品id
   * @return 到店价促销
   */
  ShopPricePromotion get(String tenant, String shop, String skuId);

  /**
   *
   * @param tenant 租户
   * @param source 原促销单
   */
  void delete(String tenant,String source);

}
