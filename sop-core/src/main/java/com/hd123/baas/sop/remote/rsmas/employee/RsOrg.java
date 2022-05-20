/**
 * 版权所有(C)，上海海鼎信息工程股份有限公司，2015，所有权利保留。
 * 
 * 项目名：	mas-user-api
 * 文件名：	Org.java
  * 模块说明：	
 * 修改历史：

 * 2019年9月3日 - lsz - 创建。
 */
package com.hd123.baas.sop.remote.rsmas.employee;

import com.hd123.baas.sop.remote.rsmas.RsMasEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * 组织
 * 
 * @author lsz
 */
@Getter
@Setter
@ApiModel("组织")
public class RsOrg extends RsMasEntity {
  private static final long serialVersionUID = 8097868597711851158L;

  public static final String ROOT = "-";

  /** 级联上级 */
  public static final String PART_UPPER = "upper";

  @ApiModelProperty(value = "类型", required = true)
  private String type;
  @ApiModelProperty(value = "代码")
  private String code;
  @ApiModelProperty(value = "是否已启用")
  private Boolean enabled;
  @ApiModelProperty(value = "名称")
  private String name;
  @ApiModelProperty(value = "上级组织")
  private String upperId;
  @ApiModelProperty(value = "上级组织")
  private RsOrg upper;

}
