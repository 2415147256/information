package com.hd123.baas.sop.service.api.entity;

import com.hd123.rumba.commons.biz.query.QueryEntity;
import com.hd123.rumba.commons.biz.query.QueryFactors;
import com.hd123.rumba.commons.biz.query.QueryField;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class SkuGroupRangeGradeConfig {
  /**
   * uuid
   */
  private int uuid;
  /**
   * 组织ID
   */
  private String orgId;
  /**
   * 租户id
   */
  private String tenant;
  /**
   * 自定义类别ID
   */
  private int skuGroupId;
  /**
   * 价格带ID
   */
  private int priceRangeId;
  /**
   * 价格级集合
   */
  private String priceGradeJson;

  @QueryEntity(SkuGroupRangeGradeConfig.class)
  public static class Queries extends QueryFactors.Entity {

    private static final String PREFIX = SkuGroupRangeGradeConfig.class.getName() + "::";
    @QueryField
    public static final String TENANT = PREFIX + "tenant";
    @QueryField
    public static final String ORG_ID = PREFIX + "orgId";
    @QueryField
    public static final String SKU_GROUP_ID = PREFIX + "skuGroupId";
  }
}
