/**
 * 版权所有(C)，上海海鼎信息工程股份有限公司，2015，所有权利保留。
 * 
 * 项目名：	mas-user-api
 * 文件名：	Tenant.java
  * 模块说明：	
 * 修改历史：

 * 2019年10月8日 - lsz - 创建。
 */
package com.hd123.baas.sop.remote.rsmas.tenant;

import com.hd123.rumba.commons.biz.entity.StandardEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * @author lsz
 */
@Getter
@Setter
@ApiModel("租户")
public class RsTenant extends StandardEntity {
  private static final long serialVersionUID = 8662367497258611790L;

  @ApiModelProperty(value = "ID")
  private String id;
  @ApiModelProperty(value = "分组")
  private String fgroup;
  @ApiModelProperty(value = "名称")
  private String name;
  @ApiModelProperty(value = "是否已删除")
  private Boolean deleted = false;

}
