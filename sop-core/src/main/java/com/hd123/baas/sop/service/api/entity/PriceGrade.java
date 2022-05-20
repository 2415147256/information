package com.hd123.baas.sop.service.api.entity;

import com.hd123.rumba.commons.biz.query.QueryEntity;
import com.hd123.rumba.commons.biz.query.QueryFactors;
import com.hd123.rumba.commons.biz.query.QueryField;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class PriceGrade {
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
   * 顺序
   */
  private int seq = 0;
  /**
   * 是否是默认价格级
   */
  private Boolean dft = false;

  @QueryEntity(PriceGrade.class)
  public static class Queries extends QueryFactors.Entity {

    private static final String PREFIX = PriceGrade.class.getName() + "::";
    @QueryField
    public static final String TENANT = PREFIX + "tenant";
    @QueryField
    public static final String ORG_ID = PREFIX + "orgId";
    @QueryField
    public static final String UUID = PREFIX + "uuid";
    @QueryField
    public static final String SEQ = PREFIX + "seq";
    @QueryField
    public static final String NAME = PREFIX + "name";
    @QueryField
    public static final String DFT = PREFIX + "dft";
  }
}
