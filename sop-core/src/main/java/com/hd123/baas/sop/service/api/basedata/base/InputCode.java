/**
 * 版权所有(C)，上海海鼎信息工程股份有限公司，2019，所有权利保留。
 *
 * 项目名：	mas-product-api
 * 文件名：	RsInputCode.java
 * 模块说明：
 * 修改历史：
 * Sep 11, 2019 - sulin - 创建。
 */
package com.hd123.baas.sop.service.api.basedata.base;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @author sulin
 *
 */
@Getter
@Setter
@ApiModel("输入码")
public class InputCode implements Serializable {
  private static final long serialVersionUID = 1L;
  /** 拼音码 */
  public static final String TYPE_PINYIN = "PINYIN";
  /** 条形码 */
  public static final String TYPE_EAN = "EAN";
  /** 二维码 */
  public static final String TYPE_QR = "QR";

  @ApiModelProperty(value = "类型", required = true)
  private String type;
  @ApiModelProperty(value = "代码", required = true)
  private String code;
  @ApiModelProperty(value = "规格", required = false)
  private BigDecimal qpc;
  @ApiModelProperty(value = "规格说明", required = false)
  private String qpcStr;
  @ApiModelProperty(value = "单位", required = false)
  private String unit;
  @ApiModelProperty(value = "重量")
  private BigDecimal weight;
}
