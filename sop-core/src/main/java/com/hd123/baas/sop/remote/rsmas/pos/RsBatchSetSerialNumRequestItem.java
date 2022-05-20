/**
 * 版权所有(C)，上海海鼎信息工程股份有限公司，2016，所有权利保留。
 * <p>
 * 项目名：	sop-parent 文件名：	RsBatchSetSerialNumRequestItem.java 模块说明： 修改历史： 2021/7/1 - XLT - 创建。
 */
package com.hd123.baas.sop.remote.rsmas.pos;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * @author XLT
 */
@Getter
@Setter
@ApiModel("POS收银机批量设置序号请求明细")
public class RsBatchSetSerialNumRequestItem implements Serializable {
  private static final long serialVersionUID = -8426294493609883680L;

  @ApiModelProperty(value = "门店ID", required = true)
  private String storeId;
  @ApiModelProperty(value = "ID")
  public String id;
  @ApiModelProperty(value = "收银机序列号")
  private String posSerialNum;
}