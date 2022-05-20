package com.hd123.baas.sop.service.api.entity;

import com.hd123.rumba.commons.biz.query.QueryEntity;
import com.hd123.rumba.commons.biz.query.QueryFactors;
import com.hd123.rumba.commons.biz.query.QueryField;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class SkuGroupCategoryAssoc {
  /**
   * uuid
   */
  private int uuid;
  /**
   * orgId
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
   * 分类ID
   */
  private String categoryId;
  /**
   * 分类code
   */
  private String categoryCode;
  /**
   * 分类名称
   */
  private String categoryName;

  @QueryEntity(SkuGroupCategoryAssoc.class)
  public static class Queries extends QueryFactors.Entity {

    private static final String PREFIX = SkuGroupCategoryAssoc.class.getName() + "::";
    @QueryField
    public static final String TENANT = PREFIX + "tenant";
    @QueryField
    public static final String SKU_GROUP_ID = PREFIX + "skuGroupId";
    @QueryField
    public static final String CATEGORY_ID = PREFIX + "categoryId";
  }
}
