package com.hd123.baas.sop.service.api.basedata.employee;

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
@ApiModel(description = "员工")
public class Employee {

  @ApiModelProperty(value = "ID")
  public String id;
  @ApiModelProperty(value = "代码")
  public String code;
  @ApiModelProperty(value = "名称")
  public String name;
  @ApiModelProperty(value = "用户ID")
  public String userId;
  @ApiModelProperty(value = "手机号")
  public String mobile;
  @ApiModelProperty(value = "在职状态")
  public boolean enabled;

  @QueryEntity(Employee.class)
  public static class Queries extends QueryFactors.Entity {

    private static final String PREFIX = Employee.class.getName() + "::";
    @QueryField
    public static final String TENANT = PREFIX + "tenant";
    @QueryField
    public static final String ID = PREFIX + "id";
    @QueryField
    public static final String CODE = PREFIX + "code";
    @QueryField
    public static final String NAME = PREFIX + "name";
    @QueryField
    public static final String USER_ID = PREFIX + "userId";
  }
}
