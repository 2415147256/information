package com.hd123.baas.sop.service.api.option;

import com.hd123.baas.sop.service.api.TenantStandardEntity;
import com.hd123.rumba.commons.biz.query.QueryEntity;
import com.hd123.rumba.commons.biz.query.QueryFactorName;
import com.hd123.rumba.commons.biz.query.QueryFactors;
import com.hd123.rumba.commons.biz.query.QueryField;
import lombok.Getter;
import lombok.Setter;

/**
 * 侧写表
 *
 */

@Setter
@Getter
public class Option  extends TenantStandardEntity {

  /**
   * 键
   */
  private String opKey;
  /**
   * 值
   */
  private String opValue;

  /**
   * 类型
   */
  private OptionType type = OptionType.SHOP;

  @QueryEntity(Option.class)
  public static class Queries extends QueryFactors.StandardEntity {
    private static final QueryFactorName PREFIX = QueryFactorName.prefix(Option.class);
    @QueryField
    public static final String TENANT = PREFIX.nameOf("tenant");
    @QueryField
    public static final String TYPE = PREFIX.nameOf("type");
    @QueryField
    public static final String OP_KEY = PREFIX.nameOf("opKey");
  }
}
