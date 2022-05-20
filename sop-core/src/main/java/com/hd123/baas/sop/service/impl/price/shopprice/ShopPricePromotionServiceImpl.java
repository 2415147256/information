package com.hd123.baas.sop.service.impl.price.shopprice;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.hd123.baas.sop.annotation.Tx;
import com.hd123.baas.sop.service.api.price.pricepromotion.PricePromotionType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.hd123.baas.sop.service.api.price.shopprice.ShopPricePromotionService;
import com.hd123.baas.sop.service.api.price.shopprice.bean.ShopPricePromotion;
import com.hd123.baas.sop.service.dao.price.shopprice.ShopPricePromotionDaoBof;

/**
 * @author zhengzewang on 2020/11/19.
 */
@Service
public class ShopPricePromotionServiceImpl implements ShopPricePromotionService {

  @Autowired
  private ShopPricePromotionDaoBof dao;

  @Override
  @Tx
  public void batchSave(String tenant, Collection<ShopPricePromotion> promotions) {
    List<ShopPricePromotion> inserts = new ArrayList<>();
    List<ShopPricePromotion> updates = new ArrayList<>();
    for (ShopPricePromotion promotion : promotions) {
      ShopPricePromotion ever = dao.get(tenant, promotion.getShop(), promotion.getSku().getId());
      if (ever != null) {
/*        if (!checkCover(promotion.getPricePromotionType(), ever.getPricePromotionType())) {
          continue;
        }*/
        ever.setPricePromotionType(promotion.getPricePromotionType());
        ever.setSourceLastModified(promotion.getSourceLastModified());
        ever.setType(promotion.getType());
        ever.setRule(promotion.getRule());
        ever.setSource(promotion.getSource());
        ever.setEffectiveEndDate(promotion.getEffectiveEndDate());
        updates.add(ever);
      } else {
        inserts.add(promotion);
      }
    }
    dao.batchInsert(tenant, inserts);
    dao.batchUpdate(tenant, updates);
  }

  /**
   * 当新旧促销规则相减不等于负数,代表可以覆盖
   * 
   * @param newPromotionType
   * @param oldPromotionType
   * @return
   */
  private boolean checkCover(String newPromotionType, String oldPromotionType) {
    PricePromotionType newType = PricePromotionType.valueOf(newPromotionType);
    PricePromotionType oldType = PricePromotionType.valueOf(oldPromotionType);
    return newType.getPriority() - oldType.getPriority() >= 0;
  }

  @Override
  public ShopPricePromotion get(String tenant, String shop, String skuId) {
    return dao.get(tenant, shop, skuId);
  }

  @Override
  public void delete(String tenant, String source) {
    dao.delete(tenant,source);
  }
}
