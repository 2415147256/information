/**
 * 版权所有(C)，上海海鼎信息工程股份有限公司，2016，所有权利保留。
 * <p>
 * 项目名：	sop-parent
 * 文件名：	RprcGrpQuerySort.java
 * 模块说明：
 * 修改历史：
 * 2021/1/13 - seven - 创建。
 */
package com.hd123.baas.sop.remote.rsh6sop.rprcgrp;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

/**
 * @author seven
 */
@Getter
@Setter
@AllArgsConstructor
public class RprcGrpQuerySort {
  @ApiModelProperty(value = "排序字段")
  private String field;
  @ApiModelProperty(value = "排序方向")
  private boolean asc;
}
