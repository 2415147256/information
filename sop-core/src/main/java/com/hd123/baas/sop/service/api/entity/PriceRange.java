package com.hd123.baas.sop.service.api.entity;

import java.util.List;

import com.hd123.rumba.commons.biz.query.QueryEntity;
import com.hd123.rumba.commons.biz.query.QueryFactors;
import com.hd123.rumba.commons.biz.query.QueryField;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PriceRange {
  /**
   * uuid
   */
  private int uuid;
  /**
   * 租户id
   */
  private String tenant;
  /**
   * 组织id
   */
  private String orgId;
  /**
   * 名称
   */
  private String name;

  /**
   * 价格级
   */
  private List<PUnv> priceGrades;

  @QueryEntity(PriceRange.class)
  public static class Queries extends QueryFactors.Entity {

    private static final String PREFIX = PriceRange.class.getName() + "::";
    @QueryField
    public static final String TENANT = PREFIX + "tenant";
    @QueryField
    public static final String UUID = PREFIX + "uuid";
    @QueryField
    public static final String ORG_ID = PREFIX + "orgId";
    @QueryField
    public static final String NAME = PREFIX + "name";
  }
}
