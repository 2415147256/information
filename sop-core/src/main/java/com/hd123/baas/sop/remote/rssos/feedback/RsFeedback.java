package com.hd123.baas.sop.remote.rssos.feedback;

import com.hd123.baas.sop.service.api.feedback.FeedbackResult;
import com.hd123.baas.sop.service.api.feedback.FeedbackState;
import com.hd123.baas.sop.service.api.feedback.FeedbackType;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 质量反馈单
 *
 * @author yu lilin
 * @since 1.0
 */
@Getter
@Setter
@ApiModel(description = "质量反馈单")
public class RsFeedback implements Serializable {
  private final static long serialVersionUID = -2525109883002266733L;

  @ApiModelProperty(value = "数据标识", example = "c14c046939704cf1a0e93545a1f768bb", required = true)
  @NotBlank
  private String billId;
  @ApiModelProperty(value = "数据来源")
  @NotNull
  private String appId;
  @ApiModelProperty(value = "租户标识", example = "d14c046939704cf1a0e93545a1f768bb", required = true)
  @NotBlank
  private String tenantId;
  @ApiModelProperty(value = "门店标识", example = "d14c046939704cf1a0e93545a1f768bb", required = true)
  @NotBlank
  private String shopId;
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
  @ApiModelProperty(value = "商品输入码", example = "9999202002110001", required = true)
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
  @ApiModelProperty(value = "申请类型,取值范围：normal-收获质量反馈，excepted-异常质量反馈")
  @NotNull
  private FeedbackType type;
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
  @ApiModelProperty(value = "称重数量", example = "100")
  @NotNull
  private BigDecimal subQty;
  @ApiModelProperty(value = "申请金额", example = "10.0000", required = true)
  @NotNull
  private BigDecimal total;
  @ApiModelProperty(value = "申请原因", example = "过期", required = true)
  @Length(max = 20)
  @NotBlank
  private String applyReason;
  @ApiModelProperty(value = "申请备注", required = false)
  @NotBlank
  private String applyNote;
  @ApiModelProperty(value = "处理结果,取值范围：approved-已同意，rejected-已拒绝")
  private FeedbackResult result;
  @ApiModelProperty(value = "审批原因", example = "其他", required = false)
  @Length(max = 20)
  @NotBlank
  private String auditReason;
  @ApiModelProperty(value = "审批备注", required = false)
  private String auditNote;
  @ApiModelProperty(value = "赔付比例", example = "70%", required = false)
  private BigDecimal payRate;
  @ApiModelProperty(value = "赔付金额", example = "10.0000", required = false)
  private BigDecimal payTotal;
  @ApiModelProperty(value = "创建时间", example = "2020-02-02 12:00:00", required = true)
  private Date created;
  @ApiModelProperty(value = "创建人代码", example = "zhangsan", required = false)
  private String creatorId;
  @ApiModelProperty(value = "创建人名称", example = "张三", required = false)
  private String creatorName;
  @ApiModelProperty(value = "最后修改时间", example = "2020-02-02 12:00:00", required = true)
  private Date lastModified;
  @ApiModelProperty(value = "最后修改人代码", example = "zhangsan", required = false)
  private String lastModifierId;
  @ApiModelProperty(value = "最后修改人名称", example = "张三", required = false)
  private String lastModifierName;
  @ApiModelProperty(value = "提交时间", example = "2020-02-02 12:00:00", required = false)
  private Date submitTime;
  @ApiModelProperty(value = "提交人代码", example = "zhangsan", required = false)
  private String submitterId;
  @ApiModelProperty(value = "提交人名称", example = "张三", required = false)
  private String submitterName;
  @ApiModelProperty(value = "审核时间", example = "2020-02-02 12:00:00", required = false)
  private Date auditTime;
  @ApiModelProperty(value = "审核人代码", example = "zhangsan", required = false)
  private String auditorId;
  @ApiModelProperty(value = "审核人名称", example = "张三", required = false)
  private String auditorName;
  @ApiModelProperty(value = "状态,取值范围：submitted-已提交，processed-已处理,feeGenerated-已生成费用单",
    example = "submitted", required = true)
  @NotNull
  private FeedbackState state;

  @ApiModelProperty(value = "质量反馈图片明细")
  private List<RsFeedbackImage> images = new ArrayList<>();
  @ApiModelProperty(value = "渠道，offline-线下，EC-电商平台")
  private String channel;
  @ApiModelProperty(value = "等级对象")
  private BSOPFeedbackGrade grade;

}
