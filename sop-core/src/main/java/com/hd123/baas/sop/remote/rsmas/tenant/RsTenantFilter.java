/**
 * 版权所有(C)，上海海鼎信息工程股份有限公司，2015，所有权利保留。
 * 
 * 项目名：	mas-user-api
 * 文件名：	TenantFilter.java
  * 模块说明：	
 * 修改历史：

 * 2019年10月8日 - lsz - 创建。
 */
package com.hd123.baas.sop.remote.rsmas.tenant;

import com.hd123.baas.sop.remote.rsmas.RsMasFilter;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * 
 * @author lsz
 */
@Getter
@Setter
@ApiModel("租户查询条件")
public class RsTenantFilter extends RsMasFilter {

  private static final long serialVersionUID = 2603647391601269968L;

  @ApiModelProperty(value = "分组等于", required = false)
  private String fgroupEq;

}
