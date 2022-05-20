package com.hd123.baas.sop.service.impl.pomdata;

import com.qianfan123.baas.config.api.annotation.BcGroup;
import com.qianfan123.baas.config.api.annotation.BcKey;
import lombok.Data;

@Data
@BcGroup(name = "促销条目生成策略")
public class PromotionTypeGeneralBillConfig {
  private static final String PREFIX = "promotionType.generalBill.";

  /**
   * 当满减、满折、满赠时，覆盖商品条件中商品qpc=0，以使所有规格都参与促销活动。
   */
  @BcKey(name = "需要转换cover qpc的促销类型")
  private String coverQpc = "";
  @BcKey(name = "促销商品冲突分组")
  private String conflictPrmGroups = "1";

  @BcKey(name = "单品特价策略组")
  private String priceExecGroup = "1";
  @BcKey(name = "促销折扣策略组")
  private String priceDiscountExecGroup = "1";
  @BcKey(name = "组合折扣策略组")
  private String groupDiscountExecGroup = "2";
  @BcKey(name = "单品买赠策略组")
  private String gdGiftExecGroup = "3";
  @BcKey(name = "单品换购策略组")
  private String gdSpecialPriceExecGroup = "4";
  @BcKey(name = "普通满减策略组")
  private String fullReduceExecGroup = "5";
  @BcKey(name = "每满减策略组")
  private String preReduceExecGroup = "5";
  @BcKey(name = "阶梯折扣策略组")
  // 这里默认值2是因为历史bug问题。需要调整的话，则通过配置修改。
  private String stepDiscountExecGroup = "2";
  @BcKey(name = "阶梯满减策略组")
  private String stepReduceExecGroup = "5";
  @BcKey(name = "单品折扣策略组")
  private String gdDiscountExecGroup = "5";
  @BcKey(name = "普通折扣策略组")
  private String discountExecGroup = "5";
  @BcKey(name = "普通折扣无门槛策略组")
  private String discountZeroExecGroup;
  @BcKey(name = "普通折扣无门槛指定商品促销")
  private String discountZeroSingleProduct;
  @BcKey(name = "清仓折扣策略组")
  private String clearDiscountExecGroup = "5";
  @BcKey(name = "整单买赠策略组")
  private String giftExecGroup = "6";
  @BcKey(name = "满额换购策略组")
  private String specialPriceExecGroup = "7";
  @BcKey(name = "组合特价策略组")
  private String groupPriceExecGroup = "1";
  @BcKey(name = "组合满赠策略组")
  private String groupGiftExecGroup = "0.5";

  @BcKey(name = "单品特价优惠方式")
  private String priceExecutionForm = "price";

  @BcKey(name = "单品特价冲突名")
  private String priceConflictMutexName;
  @BcKey(name = "促销折扣冲突名")
  private String priceDiscountConflictMutexName;
  @BcKey(name = "组合折扣冲突名")
  private String groupDiscountConflictMutexName;
  @BcKey(name = "单品买赠冲突名")
  private String gdGiftConflictMutexName;
  @BcKey(name = "单品换购冲突名")
  private String gdSpecialPriceConflictMutexName;
  @BcKey(name = "普通满减冲突名")
  private String fullReduceConflictMutexName = "fullReduce";
  @BcKey(name = "每满减冲突名")
  private String preReduceConflictMutexName = "fullReduce";
  @BcKey(name = "阶梯满减冲突名")
  private String stepReduceConflictMutexName = "fullReduce";
  @BcKey(name = "普通折扣冲突名")
  private String discountConflictMutexName = "discount";
  @BcKey(name = "清仓折扣冲突名")
  private String clearDiscountConflictMutexName = "clearDiscount";
  @BcKey(name = "整单买赠冲突名")
  private String giftConflictMutexName = "gift";
  @BcKey(name = "满额换购冲突名")
  private String specialPriceConflictMutexName = "specialPrice";
  @BcKey(name = "组合特价冲突名")
  private String groupPriceConflictMutexName;
  @BcKey(name = "组合赠品冲突名")
  private String groupGiftConflictMutexName;

}
