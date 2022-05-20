/**
 * 版权所有(C)，上海海鼎信息工程股份有限公司，2015，所有权利保留。
 * <p>
 * 项目名：	mas-commons-api
 * 文件名：	Condition.java
 * 模块说明：
 * 修改历史：
 * <p>
 * 2019年10月12日 - lsz - 创建。
 */
package com.hd123.baas.sop.remote.rsmas.index;

import java.util.ArrayList;
import java.util.List;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 查询条件
 *
 * @author lsz
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ApiModel
public class RsCondition {

  @NotNull
  @NotBlank
  @ApiModelProperty("字段名")
  private String field;
  @ApiModelProperty("操作符")
  @NotNull
  private RsCop operator;
  @ApiModelProperty("条件值")
  @NotNull
  private List<Object> parameters = new ArrayList<Object>();
}
