/**
 * 版权所有(C)，上海海鼎信息工程股份有限公司，2015，所有权利保留。
 * 
 * 项目名：	sop-commons
 * 文件名：	StallCreation.java
  * 模块说明：	
 * 修改历史：

 * 2021年1月3日 - lsz - 创建。
 */
package com.hd123.baas.sop.remote.rsmas.stall;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * 
 * @author lsz
 */
@Getter
@Setter
@ApiModel("出品部门新增")
public class RsStallCreation implements Serializable {
  private static final long serialVersionUID = -4551920361571981453L;

  @ApiModelProperty(value = "ID")
  public String id;
  @ApiModelProperty(value = "代码")
  private String code;
  @ApiModelProperty(value = "名称")
  private String name;
  @ApiModelProperty(value = "门店ID")
  private String storeId;
  @ApiModelProperty(value = "是否打印厨打小票")
  private Boolean receiptPrinting;
}
