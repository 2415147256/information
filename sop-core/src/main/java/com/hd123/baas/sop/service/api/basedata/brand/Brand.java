package com.hd123.baas.sop.service.api.basedata.brand;

import com.hd123.rumba.commons.biz.query.QueryEntity;
import com.hd123.rumba.commons.biz.query.QueryFactors;
import com.hd123.rumba.commons.biz.query.QueryField;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * @author Silent
 **/
@Getter
@Setter
@ApiModel(description = "品牌")
public class Brand {

  @ApiModelProperty(value = "ID")
  public String id;
  @ApiModelProperty(value = "代码")
  public String code;
  @ApiModelProperty(value = "名称")
  public String name;
  @ApiModelProperty(value = "组织类型", required = true)
  private String orgType;
  @ApiModelProperty(value = "组织id", required = true)
  private String orgId;

  @QueryEntity(Brand.class)
  public static class Queries extends QueryFactors.Entity {

    private static final String PREFIX = Brand.class.getName() + "::";
    @QueryField
    public static final String TENANT = PREFIX + "tenant";
    @QueryField
    public static final String ID = PREFIX + "id";
    @QueryField
    public static final String CODE = PREFIX + "code";
    @QueryField
    public static final String NAME = PREFIX + "name";
    @QueryField
    public static final String ORG_TYPE = PREFIX + "orgType";
    @QueryField
    public static final String ORG_ID = PREFIX + "orgId";
  }

}
