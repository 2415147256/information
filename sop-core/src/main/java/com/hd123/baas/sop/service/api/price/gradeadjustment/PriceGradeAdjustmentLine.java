package com.hd123.baas.sop.service.api.price.gradeadjustment;

import com.hd123.baas.sop.service.api.TenantEntity;
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
public class PriceGradeAdjustmentLine extends TenantEntity {

  private String owner;

  // 商品类别
  private String skuGroup;
  private String skuGroupName;

  // 商品定位
  private String skuPosition;
  private String skuPositionName;

  // 价格级
  private String priceGrade;
  private String priceGradeName;

  @QueryEntity(PriceGradeAdjustmentLine.class)
  public static class Queries extends QueryFactors.Entity {

    private static final String PREFIX = PriceGradeAdjustmentLine.class.getName() + "::";

    @QueryField
    public static final String TENANT = PREFIX + "tenant";
    @QueryField
    public static final String OWNER = PREFIX + "owner";
    @QueryField
    public static final String SKU_GROUP = PREFIX + "skuGroup";
    @QueryField
    public static final String SKU_GROUP_NAME = PREFIX + "skuGroupName";
    @QueryField
    public static final String SKU_POSITION = PREFIX + "skuPosition";
    @QueryOperation
    public static final String SKU_KEYWORD = PREFIX + "skuKeyword";
    @QueryOperation
    public static final String PRICE_GRADE_IS_NULL = PREFIX + "priceGradeIsNull";

  }

}
