package com.hd123.baas.sop.service.api.entity;

import java.math.BigDecimal;

import com.hd123.rumba.commons.biz.query.QueryEntity;
import com.hd123.rumba.commons.biz.query.QueryFactors;
import com.hd123.rumba.commons.biz.query.QueryField;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class SkuGroup {
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
   * 名称
   */
  private String name;
  /**
   * 容差值
   */
  private BigDecimal toleranceValue;

  @QueryEntity(SkuGroup.class)
  public static class Queries extends QueryFactors.Entity {

    private static final String PREFIX = SkuGroup.class.getName() + "::";
    @QueryField
    public static final String TENANT = PREFIX + "tenant";
    @QueryField
    public static final String ORG_ID = PREFIX + "orgId";
    @QueryField
    public static final String UUID = PREFIX + "uuid";
    @QueryField
    public static final String NAME = PREFIX + "name";
  }

}
