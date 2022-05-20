/**
 * 版权所有(C)，上海海鼎信息工程股份有限公司，2015，所有权利保留。
 * 
 * 项目名：	mas-commons-api
 * 文件名：	UnionCondition.java
  * 模块说明：	
 * 修改历史：

 * 2019年11月1日 - lsz - 创建。
 */
package com.hd123.baas.sop.remote.rsmas.index;

import java.util.ArrayList;
import java.util.List;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * 联合查询条件
 * 
 * @author lsz
 */
@Getter
@Setter
@ApiModel
public class RsUnionCondition {

  @ApiModelProperty(value = "OR查询条件")
  private List<RsCondition> orConditions = new ArrayList<>();

}
