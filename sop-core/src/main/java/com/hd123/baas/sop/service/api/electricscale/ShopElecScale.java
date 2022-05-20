package com.hd123.baas.sop.service.api.electricscale;

import com.hd123.baas.sop.service.api.TenantStandardEntity;
import com.hd123.rumba.commons.biz.query.QueryEntity;
import com.hd123.rumba.commons.biz.query.QueryFactors;
import com.hd123.rumba.commons.biz.query.QueryField;
import com.hd123.rumba.commons.biz.query.QueryOperation;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ShopElecScale extends TenantStandardEntity {
  // 名称
  private String name;
  // 门店代码
  private String shopCode;
  /** 门店名称 **/
  private String shopName;
  /** 电子秤厂商/型号 **/
  private String model;
  // ip
  private String ip;
  // 组织id
  private String orgId;

  /**
   * 电子秤ID
   */
  private String electronicScale;

  @QueryEntity(ShopElecScale.class)
  public static class Queries extends QueryFactors.StandardEntity {
    private static final String PREFIX = ShopElecScale.class.getName() + "::";
    @QueryField
    public static final String TENANT = PREFIX + "tenant";
    @QueryField
    public static final String NAME = PREFIX + "name";
    @QueryField
    public static final String SHOP_CODE = PREFIX + "shopCode";
    @QueryField
    public static final String SHOP_NAME = PREFIX + "shopName";
    @QueryField
    public static final String MODEL = PREFIX + "model";
    @QueryField
    public static final String ELECTRONIC_SCALE = PREFIX + "electronicScale";
    @QueryField
    public static final String ORG_ID = PREFIX + "orgId";
    @QueryOperation
    public static final String SHOP_KEY_WORD_LIKE = PREFIX + "shopKeyword like";

  }
}
