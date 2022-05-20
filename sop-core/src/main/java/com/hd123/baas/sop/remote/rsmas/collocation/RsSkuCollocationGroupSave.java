/**
 * 版权所有(C)，上海海鼎信息工程股份有限公司，2016，所有权利保留。
 * <p>
 * 项目名：	sop-parent 文件名：	RsSkuCollocationGroupSave.java 模块说明： 修改历史： 2021/8/2 - XLT - 创建。
 */
package com.hd123.baas.sop.remote.rsmas.collocation;

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
@ApiModel("商品加料组保存")
public class RsSkuCollocationGroupSave implements Serializable {

  private static final long serialVersionUID = -4529965393644793227L;

  @ApiModelProperty(value = "加料组id")
  private String collocationGroupId;
  @ApiModelProperty(value = "显示序号")
  private Integer showIndex;

}