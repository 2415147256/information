/**
 * 版权所有(C)，上海海鼎信息工程股份有限公司，2016，所有权利保留。
 * <p>
 * 项目名：	sop 文件名：	RsMultiAndCondition.java 模块说明： 修改历史： 2021/8/30 - XLT - 创建。
 */
package com.hd123.baas.sop.remote.rsmas.index;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

/**
 * @author XLT
 */
@Getter
@Setter
@ApiModel
public class RsMultiAndCondition {
  @ApiModelProperty(value = "AND查询条件")
  private List<RsCondition> andConditions = new ArrayList<>();
}