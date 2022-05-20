package com.hd123.baas.sop.service.api.price.config;

import java.math.BigDecimal;
import java.util.List;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.hd123.baas.sop.service.api.basedata.category.Category;
import com.hd123.baas.sop.service.api.price.PriceSku;
import com.hd123.baas.sop.service.api.price.SkuDefine;
import com.hd123.baas.sop.service.api.price.priceadjustment.PriceIncreaseRule;
import com.hd123.baas.sop.service.api.price.priceadjustment.PriceIncreaseType;
import com.hd123.mpa.api.common.ObjectNodeUtil;
import com.hd123.rumba.commons.biz.entity.StandardEntity;
import com.hd123.rumba.commons.biz.query.QueryEntity;
import com.hd123.rumba.commons.biz.query.QueryField;
import com.hd123.rumba.commons.biz.query.QueryOperation;

import lombok.Getter;
import lombok.Setter;

/**
 * @author zhengzewang on 2020/11/10.
 */
@Getter
@Setter
public class PriceSkuConfig extends StandardEntity {

  private String tenant;
  // 组织ID
  private String orgId;
  // 商品。数据库只存储skuId
  private PriceSku sku;
  // 容差值，10表示10%
  private BigDecimal toleranceValue;
  // K值
  private BigDecimal kv;
  // B值
  private BigDecimal bv;
  // 后台加价率，10表示10%
  private BigDecimal increaseRate;
  // 是否计算尾差
  private Boolean calcTailDiff;
  // 商品定位
  private String skuPosition;
  // 商品定位限制价格级 堡垒商品专用
  private String skuPositionGradeId;
  // 商品分类 二期添加
  private String skuGroup;

  private Category category;

  // 目标采购价上限
  private BigDecimal highInPrice;
  // 目标采购价下限
  private BigDecimal lowInPrice;
  // 后台毛利率上限
  private BigDecimal highBackGrossRate;
  // 后台毛利率下限
  private BigDecimal lowBackGrossRate;
  // 前台毛利率上限
  private BigDecimal highFrontGrossRate;
  // 前台毛利率下限
  private BigDecimal lowFrontGrossRate;
  // 市调差异率上限
  private BigDecimal highMarketDiffRate;
  // 市调差异率下限
  private BigDecimal lowMarketDiffRate;
  // 零售价变动率上限
  private BigDecimal highPriceFloatRate;
  // 零售价变动率下限
  private BigDecimal lowPriceFloatRate;

  // 以下不持久化
  private String skuPositionName;
  private String skuGroupName;
  private BigDecimal skuGroupToleranceValue;

  // 商品定义
  private SkuDefine skuDefine = SkuDefine.NORMAL;
  private String raw;

  /** 加价方式 */
  private PriceIncreaseType increaseType;
  /** 加价规则 json存储 */
  private List<PriceIncreaseRule> increaseRules;

  /**
   * 拓展属性
   */
  private ObjectNode ext = ObjectNodeUtil.createObjectNode();

  @QueryEntity(PriceSkuConfig.class)
  public static class Queries {

    private static final String PREFIX = PriceSkuConfig.class.getName() + "::";
    @QueryField
    public static final String TENANT = PREFIX + "tenant";
    @QueryField
    public static final String ORG_ID = PREFIX + "orgId";
    @QueryField
    public static final String SKU_POSITION = PREFIX + "skuPosition";
    @QueryOperation
    public static final String TOLERANCE_BETWEEN = "toleranceBetween";

    @QueryOperation
    public static final String SKU_POSITION_IS_NULL = PREFIX + "skuPositionIsNull";
    @QueryOperation
    public static final String SKU_GROUP_IS_NULL = PREFIX + "skuGroupIsNull";
    @QueryOperation
    public static final String INCREASERATE_IS_NULL = PREFIX + "increaseRateIsNull";

  }

  public static class Ext {
    public static final String POSITION_GRADE_ID = "position_grade_id";
    public static final String POSITION_GRADE_NAME = "position_grade_name";
  }

}
