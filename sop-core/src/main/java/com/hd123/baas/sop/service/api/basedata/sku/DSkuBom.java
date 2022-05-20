package com.hd123.baas.sop.service.api.basedata.sku;

import com.hd123.baas.sop.remote.rsmas.RsMasQueryFactors;
import com.hd123.rumba.commons.biz.entity.Entity;
import com.hd123.rumba.commons.biz.query.QueryEntity;
import com.hd123.rumba.commons.biz.query.QueryField;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * @author lina
 */
@Getter
@Setter
public class DSkuBom extends Entity {
  private static final long serialVersionUID = -4886047099034843815L;

  @ApiModelProperty(value = "租户")
  private String tenant;
  @ApiModelProperty(value = "ERP商品GID")
  public String goodsGid;
  @ApiModelProperty(value = "SKUID")
  public String skuId;
  @ApiModelProperty(value = "bom")
  public String bom;
  @ApiModelProperty("组织类型")
  private String orgType;
  @ApiModelProperty("组织id")
  private String orgId;

  @QueryEntity(DSkuBom.class)
  public static abstract class Queries extends RsMasQueryFactors.Entity {

    private static final String PREFIX = DSkuBom.class.getName() + "::";
    @QueryField
    public static final String TENANT = PREFIX + "tenant";
    @QueryField
    public static final String GOODS_GID = PREFIX + "goodsGid";
    @QueryField
    public static final String SKU_ID = PREFIX + "skuId";
    @QueryField
    public static final String BOM = PREFIX + "bom";
    @QueryField
    public static final String ORG_TYPE = PREFIX + "orgType";
    @QueryField
    public static final String ORG_ID = PREFIX + "orgId";
  }

}