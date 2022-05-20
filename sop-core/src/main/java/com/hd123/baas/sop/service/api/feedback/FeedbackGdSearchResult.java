package com.hd123.baas.sop.service.api.feedback;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 可反馈反馈商品查询结果
 *
 * @author yu lilin
 * @since 1.0
 */
@Getter
@Setter
public class FeedbackGdSearchResult implements Serializable {
  private final static long serialVersionUID = 6248866916870491269L;

  @ApiModelProperty(value = "收货单号", example = "9999202002110001", required = true)
  private String receiptNum;
  @ApiModelProperty(value = "已收货数量", example = "100", required = true)
  private BigDecimal receiptQty;
  @ApiModelProperty(value = "可反馈数量", example = "10", required = true)
  private BigDecimal qty;
  @ApiModelProperty(value = "到货时间", example = "2020-02-02 12:00:00", required = true)
  private Date deliveryTime;
  @ApiModelProperty(value = "商品标识", example = "d14c046939704cf1a0e93545a1f768bb", required = true)
  @NotBlank
  private String gdUuid;
  @ApiModelProperty(value = "商品代码", example = "001", required = true)
  @NotBlank
  private String gdCode;
  @ApiModelProperty(value = "商品输入码", example = "9999202002110001", required = true)
  private String gdInputCode;
  @ApiModelProperty(value = "商品名称", example = "商品名称", required = true)
  private String gdName;
  @ApiModelProperty(value = "到店价", example = "10.0000", required = true)
  private BigDecimal singlePrice;
  @ApiModelProperty(value = "计量单位", example = "箱", required = true)
  @NotBlank
  private String munit;
  @ApiModelProperty(value = "最小规格单位", example = "Kg", required = true)
  private String minMunit;
  @ApiModelProperty(value = "规格", example = "2", required = true)
  private BigDecimal qpc;
  @ApiModelProperty(value = "收货单商品行标识", example = "c14c046939704cf1a0e93545a1f768bb", required = true)
  private String receiptLineId;
  @ApiModelProperty(value = "已反馈数量", example = "10.0000", required = false)
  private BigDecimal feedbackQty;
  @ApiModelProperty(value = "渠道 取值范围：offline-线下，EC-电商平台", example = "offline", required = false)
  private String channel;
}
