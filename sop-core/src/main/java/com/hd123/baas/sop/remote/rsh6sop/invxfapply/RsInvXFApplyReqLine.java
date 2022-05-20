/**
 * 版权所有(C)，上海海鼎信息工程股份有限公司，2020，所有权利保留。
 * <p>
 * 项目名：	workspace_shop
 * 文件名：	InvXFApplyCreationLine.java
 * 模块说明：
 * 修改历史：
 * 2020年04月22日 - huangjunxian - 创建。
 */
package com.hd123.baas.sop.remote.rsh6sop.invxfapply;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;
import java.math.BigDecimal;

@Data
public class RsInvXFApplyReqLine implements Serializable {
  private static final long serialVersionUID = 8702011179627119326L;

  @ApiModelProperty(value = "单据标识", example = "c14c046939704cf1a0e93545a1f768bb", required = true)
  @NotBlank
  private String num;
  @ApiModelProperty(value = "行号", example = "1", required = true)
  private Integer line;
  @ApiModelProperty(value = "类别", example = "门店调拨申请", required = true)
  @Length(max = 16)
  @NotBlank
  private String cls;
  @ApiModelProperty(value = "商品标识", required = true)
  private String gdUuid;
  @ApiModelProperty(value = "商品代码", required = true)
  private String gdCode;
  @ApiModelProperty(value = "商品名称", required = true)
  private String gdName;
  @ApiModelProperty(value = "个", required = true)
  private String munit;
  @ApiModelProperty(value = "包装规格", example = "1*1", required = true)
  private String qpcStr;
  @ApiModelProperty(value = "包装", example = "1", required = true)
  private BigDecimal qpc = BigDecimal.ONE;
  @ApiModelProperty(value = "数量", example = "1", required = true)
  private BigDecimal qty = BigDecimal.ZERO;
  @ApiModelProperty(value = "包装数量", example = "1", required = true)
  private String qtyStr = "0";
  @ApiModelProperty(value = "批准数量", example = "1")
  private BigDecimal approveQty = BigDecimal.ZERO;
  @ApiModelProperty(value = "批准包装数量", example = "1", required = true)
  private String approveQtyStr = "0";
  @ApiModelProperty(value = "调出方库存数", example = "10", required = true)
  private BigDecimal fromQty = BigDecimal.ZERO;
  @ApiModelProperty(value = "调出方库存规格数", example = "10", required = true)
  private String fromQtyStr = "0";
  @ApiModelProperty(value = "调入方库存数", example = "10", required = true)
  private BigDecimal toQty = BigDecimal.ZERO;
  @ApiModelProperty(value = "调入方库存规格数", example = "10", required = true)
  private String toQtyStr = "0";
  @ApiModelProperty(value = "调拨原因")
  private String reason;
  @ApiModelProperty(value = "调拨单价", required = true)
  private BigDecimal price = BigDecimal.ZERO;
  @ApiModelProperty(value = "调拨金额", required = true)
  private BigDecimal total = BigDecimal.ZERO;
  @ApiModelProperty(value = "调拨税额", required = true)
  private BigDecimal tax = BigDecimal.ZERO;
  @ApiModelProperty(value = "备注", required = false)
  private String note;
}