package com.hd123.baas.sop.service.api.price.priceadjustment;

import java.math.BigDecimal;
import java.util.List;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.hd123.baas.sop.service.api.TenantEntity;
import com.hd123.baas.sop.service.api.price.PriceSku;
import com.hd123.baas.sop.service.api.price.SkuDefine;
import com.hd123.mpa.api.common.ObjectNodeUtil;
import com.hd123.rumba.commons.biz.query.QueryEntity;
import com.hd123.rumba.commons.biz.query.QueryFactors;
import com.hd123.rumba.commons.biz.query.QueryField;
import com.hd123.rumba.commons.biz.query.QueryOperation;

import lombok.Getter;
import lombok.Setter;

/**
 * @author zhengzewang on 2020/11/10.
 */
@Getter
@Setter
public class PriceAdjustmentLine extends TenantEntity {

  private BigDecimal highInPrice;
  private BigDecimal lowInPrice;
  private BigDecimal highBackGrossRate;
  private BigDecimal lowBackGrossRate;
  private BigDecimal highFrontGrossRate;
  private BigDecimal lowFrontGrossRate;
  private BigDecimal highMarketDiffRate;
  private BigDecimal lowMarketDiffRate;
  private BigDecimal highPriceFloatRate;
  private BigDecimal lowPriceFloatRate;
  /** 调整前 目标采购价 */
  private BigDecimal preSkuInPrice = BigDecimal.ZERO;
  /** 调整前 后台加价率 */
  private BigDecimal preSkuIncreaseRate = BigDecimal.ZERO;
  /** 调整前 价格带加价率 json存储 */
  private List<PriceIncreaseRate> prePriceRangeIncreaseRates;
  /** 调整前 价格级售价 json存储 */
  private List<PriceGradeSalePrice> prePriceGrades;

  private String owner;

  // 需要将商品信息平铺到数据库中
  private PriceSku sku;
  // 目标采购价
  private BigDecimal skuInPrice = BigDecimal.ZERO;
  // 初始采购价
  private BigDecimal skuInitInPrice = BigDecimal.ZERO;
  // 基础到店价
  private BigDecimal skuBasePrice;

  // 实际到店价。不持久化，用作job计算
  private BigDecimal skuShopPrice;

  // 商品基本配置
  private BigDecimal skuToleranceValue;
  private BigDecimal skuKv = BigDecimal.ONE;
  private BigDecimal skuBv = BigDecimal.ZERO;
  // 后台加价率
  private BigDecimal skuIncreaseRate = BigDecimal.ZERO;
  // 商品自定义分类
  private String skuGroup;
  private String skuGroupName;
  // 类别容差值
  private BigDecimal skuGroupToleranceValue;
  // 商品定义
  private SkuDefine skuDefine = SkuDefine.NORMAL;
  private String raw;
  // 商品定位
  private String skuPosition;
  private String skuPositionName;
  /** 商品定位加价率 json存储 */
  private List<PriceIncreaseRate> skuPositionIncreaseRates;
  /** 价格带加价率 json存储 */
  private List<PriceIncreaseRate> priceRangeIncreaseRates;
  /** 商品加价率 json存储 */
  private List<PriceIncreaseRate> skuGradeIncreaseRates;
  /** 加价方式 */
  private PriceIncreaseType increaseType;
  /** 加价规则 json存储 */
  private List<PriceIncreaseRule> increaseRules;
  /** 价格级售价 json存储 */
  private List<PriceGradeSalePrice> priceGrades;
  // 竞品信息 竞品表
  private PriceCompetitorLine competitor;
  /** 重新调整说明 */
  private String remark;
  // 是否计算尾差
  private Boolean calcTailDiff;
  /** 上周平均数量 */
  private BigDecimal aveWeekQty;

  /**
   * 拓展属性
   */
  private ObjectNode ext = ObjectNodeUtil.createObjectNode();

  @QueryEntity(PriceAdjustmentLine.class)
  public static class Queries extends QueryFactors.Entity {

    private static final String PREFIX = PriceAdjustmentLine.class.getName() + "::";

    @QueryField
    public static final String TENANT = PREFIX + "tenant";
    @QueryField
    public static final String OWNER = PREFIX + "owner";
    @QueryField
    public static final String SKU_ID = PREFIX + "sku.id";
    @QueryField
    public static final String SKU_CODE = PREFIX + "sku.code";
    @QueryField
    public static final String SKU_QPC = PREFIX + "sku.qpc";
    @QueryField
    public static final String SKU_GROUP = PREFIX + "skuGroup";
    @QueryField
    public static final String SKU_POSITION = PREFIX + "skuPosition";
    @QueryField
    public static final String SKU_DEFINE = PREFIX + "skuDefine";
    @QueryField
    public static final String INCREASE_TYPE = PREFIX + "increaseType";
    @QueryOperation
    public static final String SKU_KEYWORD = PREFIX + "skuKeyword";
    @QueryOperation
    public static final String SKU_BASE_PRICE_IS_NULL = PREFIX + "skuBasePriceIsNull";
    @QueryOperation
    public static final String SKU_IN_PRICE_IS_NULL = PREFIX + "skuInPriceIsNull";
    @QueryOperation
    public static final String INCREASE_TYPE_RULE_IS_NULL = PREFIX + "increaseTypeRuleIsNull";
    @QueryOperation
    public static final String SKU_GROUP_IS_NULL = PREFIX + "skuGroupIsNull";
    @QueryOperation
    public static final String SKU_POSITION_IS_NULL = PREFIX + "skuPositionIsNull";
    @QueryOperation
    public static final String INCREASE_TYPE_IS_NULL = PREFIX + "increaseTypeIsNull";
    /** 存在竞品 */
    @QueryOperation
    public static final String EXSIT_COMPETIOR = PREFIX + "exist competitor";

  }

  public static class Ext {
    public static final String POSITION_GRADE_ID = "position_grade_id";
    public static final String POSITION_GRADE_NAME = "position_grade_name";
  }

}
