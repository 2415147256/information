package com.hd123.baas.sop.service.api.feedback;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 质量反馈单创建对象
 *
 * @author yu lilin
 * @since 1.0
 */
@Getter
@Setter
@ApiModel(description = "质量反馈单创建对象")
public class FeedbackCreation implements Serializable {
  private static final long serialVersionUID = -4838662928351967006L;
  @ApiModelProperty(value = "组织ID", example = "100000", required = true)
  @NotBlank
  private String orgId;
  @ApiModelProperty(value = "数据标识", example = "c14c046939704cf1a0e93545a1f768bb", required = true)
  @NotBlank
  private String billId;
  @ApiModelProperty(value = "门店标识", example = "8048", required = true)
  @NotBlank
  private String shop;
  @ApiModelProperty(value = "门店代码", example = "1001", required = true)
  @NotBlank
  private String shopNo;
  @ApiModelProperty(value = "门店名称", example = "XX店", required = true)
  @NotBlank
  private String shopName;
  @ApiModelProperty(value = "收货单号", example = "9999202002110001", required = true)
  @NotBlank
  private String receiptNum;
  @ApiModelProperty(value = "收货单商品行标识", example = "c14c046939704cf1a0e93545a1f768bb", required = true)
  @NotBlank
  private String receiptLineId;
  @ApiModelProperty(value = "商品标识", example = "d14c046939704cf1a0e93545a1f768bb", required = true)
  @NotBlank
  private String gdUuid;
  @ApiModelProperty(value = "商品输入码", example = "9999202002110001", required = false)
  private String gdInputCode;
  @ApiModelProperty(value = "商品名称", example = "名称", required = true)
  @NotBlank
  private String gdName;
  @ApiModelProperty(value = "商品代码", example = "001", required = true)
  @NotBlank
  private String gdCode;
  @ApiModelProperty(value = "计量单位", example = "箱", required = true)
  @NotBlank
  private String munit;
  @ApiModelProperty(value = "最小规格单位", example = "Kg", required = true)
  private String minMunit;
  @ApiModelProperty(value = "规格", example = "2", required = true)
  @NotNull
  private BigDecimal qpc;
  @ApiModelProperty(value = "所属类别代码", example = "", required = true)
  @NotNull
  private String gdTypeCode;
  @ApiModelProperty(value = "所属类别名称", example = "", required = true)
  @NotNull
  private String gdTypeName;
  @ApiModelProperty(value = "到货时间", example = "2020-02-02 12:00:00", required = true)
  private Date deliveryTime;
  @ApiModelProperty(value = "到店价", example = "10.0000", required = true)
  @NotNull
  private BigDecimal singlePrice;
  @ApiModelProperty(value = "收货数量", example = "100", required = true)
  @NotNull
  private BigDecimal receiptQty;
  @ApiModelProperty(value = "申请数量", example = "100", required = true)
  @NotNull
  private BigDecimal qty;
  @ApiModelProperty(value = "申请金额", example = "10.0000", required = true)
  @NotNull
  private BigDecimal total;
  @ApiModelProperty(value = "申请原因", example = "过期", required = false)
  private String applyReason;
  @ApiModelProperty(value = "申请备注", required = false)
  private String applyNote;
  @ApiModelProperty(value = "申请类型,取值范围：normal-收获质量反馈，excepted-异常质量反馈")
  private FeedbackType type;
  @ApiModelProperty(value = "创建时间", example = "2020-02-02 12:00:00", required = true)
  private Date created;
  @ApiModelProperty(value = "创建人代码", example = "zhangsan", required = true)
  private String creatorId;
  @ApiModelProperty(value = "创建人名称", example = "张三", required = true)
  private String creatorName;
  @ApiModelProperty(value = "渠道 取值范围：offline-线下，EC-电商平台", example = "offline", required = false)
  private String channel;
}
