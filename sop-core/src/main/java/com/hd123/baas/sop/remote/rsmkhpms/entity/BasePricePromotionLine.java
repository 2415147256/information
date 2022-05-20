package com.hd123.baas.sop.remote.rsmkhpms.entity;

import java.math.BigDecimal;

import com.hd123.rumba.commons.biz.entity.Entity;
import com.hd123.rumba.commons.biz.query.QueryEntity;
import com.hd123.rumba.commons.biz.query.QueryFactors;
import com.hd123.rumba.commons.biz.query.QueryField;
import com.hd123.rumba.commons.biz.query.QueryOperation;

import lombok.Getter;
import lombok.Setter;

/**
 * @author youjiawei
 */
@Getter
@Setter
public class BasePricePromotionLine extends Entity {
  private String tenant;
  /**
   * 主单id
   */
  private String owner;
  /**
   * sku_id
   */
  private String skuId;
  /**
   * sku_Gid
   */
  private String skuGid;
  /**
   * skuCode
   */
  private String skuCode;
  private String skuName;
  private BigDecimal skuQpc;
  private String skuUnit;
  /**
   * 促销规则类型，固定值/周环比中位数
   */
  private String ruleType;
  /**
   * 促销规则JSON
   */
  private String promRule;

  @QueryEntity(BasePricePromotionLine.class)
  public static class Queries extends QueryFactors.Entity {
    private static final String PREFIX = BasePricePromotionLine.class.getSimpleName() + "::";
    @QueryField
    public static final String TENANT = PREFIX + "tenant";
    @QueryField
    public static final String SKU_CODE = PREFIX + "skuCode";
    @QueryField
    public static final String SKU_NAME = PREFIX + "skuName";
    @QueryOperation
    public static final String SKU_KEY_WORD = PREFIX + "skuKeyWord";
  }

}
