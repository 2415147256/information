package com.hd123.baas.sop.service.api.basedata.area;

import com.hd123.rumba.commons.biz.entity.Entity;
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
@ApiModel(description = "区域")
public class Area extends Entity {

  @ApiModelProperty(value = "ID")
  public String id;
  @ApiModelProperty(value = "代码")
  public String code;
  @ApiModelProperty(value = "名称")
  public String name;

  @QueryEntity(Area.class)
  public static class Queries extends QueryFactors.Entity {

    private static final String PREFIX = Area.class.getName() + "::";
    @QueryField
    public static final String TENANT = PREFIX + "tenant";
    @QueryField
    public static final String ID = PREFIX + "id";
    @QueryField
    public static final String CODE = PREFIX + "code";
    @QueryField
    public static final String NAME = PREFIX + "name";
  }
}
