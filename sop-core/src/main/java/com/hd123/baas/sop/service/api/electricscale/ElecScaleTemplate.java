package com.hd123.baas.sop.service.api.electricscale;

import com.hd123.rumba.commons.biz.entity.StandardEntity;
import com.hd123.rumba.commons.biz.query.QueryEntity;
import com.hd123.rumba.commons.biz.query.QueryFactors;
import com.hd123.rumba.commons.biz.query.QueryField;
import com.hd123.rumba.commons.biz.query.QueryOperation;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ElecScaleTemplate extends StandardEntity {
  private String tenant;
  private String name;
  /**
   * 电子秤设备id
   */
  private String electronicScale;
  /**
   * 电子秤键盘id
   */
  private String elecScaleKeyBoard;
  /**
   * 所属组织id
   */
  private String orgId;

  @QueryEntity(ElecScaleTemplate.class)
  public static class Queries extends QueryFactors.StandardEntity {
    private static final String PREFIX = ElecScaleTemplate.class.getName() + "::";
    @QueryField
    public static final String TENANT = PREFIX + "tenant";
    @QueryField
    public static final String UUID = PREFIX + "uuid";
    @QueryField
    public static final String NAME = PREFIX + "name";
    @QueryField
    public static final String ORG_ID = PREFIX + "orgId";
    @QueryOperation
    public static final String SHOP_KEY_WORD = PREFIX + "shopKeyWord";
    @QueryField
    public static final String ELECTRONIC_SCALE = PREFIX + "electronicScale";
  }
}
