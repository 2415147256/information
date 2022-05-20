/**
 * 版权所有(C)，上海海鼎信息工程股份有限公司，2020，所有权利保留。
 * <p>
 * 项目名： workspace_mkh
 * 文件名： SOPPromotionBill.java
 * 模块说明：
 * 修改历史：
 * 2020年11月30日 - huangjunxian - 创建。
 */
package com.hd123.baas.sop.remote.rsh6sop.prom;

/**
 * @author huangjunxian
 * @since 1.0
 */

import com.hd123.baas.sop.remote.rsh6sop.activity.H6FavorSharingDetail;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Data
@ApiModel(description = "促销单")
public class RSPromotionBill implements Serializable {
  @NotBlank
  @Length(max = 20)
  @ApiModelProperty(value = "促销模型标识，目前写死retail", example = "retail", required = true)
  private String type;
  @NotBlank
  @Length(max = 32)
  @ApiModelProperty(value = "单号", example = "9999202011300001", required = true)
  private String billNumber;
  private String activityCode;
  @NotNull
  @ApiModelProperty(value = "状态：initial-未审核,audited-已审核,aborted-已作废", example = "audited", required = true)
  private RSPromotionBillState state;
  @NotNull
  @ApiModelProperty(value = "是否会员专享", example = "false", required = true)
  private Boolean memberExclusive = Boolean.FALSE;
  @Length(max = 10)
  @ApiModelProperty(value = "结转期号yyyyMM", example = "202011", required = true)
  private String settleNo;
  @NotNull
  @ApiModelProperty(value = "促销开始日期", example = "2020-11-30", required = true)
  private Date startDate;
  @NotNull
  @ApiModelProperty(value = "促销结束日期", example = "2020-12-1", required = true)
  private Date finishDate;
  @NotBlank
  @Length(max = 64)
  @ApiModelProperty(value = "模板名称,取值范围：price-促销价，fullReduce-普通满减，preReduce-每满减，stepReduce-阶梯满减，discount-普通折扣，clearDiscount-清仓促销，groupDiscount-组合折扣,gdGift-单品买赠，gift-整单满赠，gdSpecialPrice-单品换购，specialPrice-满额换购", example = "gift", required = true)
  private String templateName;
  @Length(max = 255)
  @ApiModelProperty(value = "说明", required = true)
  private String remark;
  @NotNull
  @ApiModelProperty(value = "所有组织", example = "true", required = true)
  private Boolean allOrg;
  @NotNull
  @ApiModelProperty(value = "是否全场", example = "false", required = true)
  private Boolean overall;
  @Length(max = 38)
  @ApiModelProperty(value = "所属组织id", required = false)
  private String starterOrgUuid;
  @ApiModelProperty(value = "审核时间", example = "2020-11-30 11:00:00", required = false)
  private Date auditTime;
  @ApiModelProperty(value = "作废时间", example = "2020-11-30 11:00:00", required = false)
  private Date abortTime;

  @ApiModelProperty(value = "参加单位列表", required = true)
  private List<RSPromotionBillJoin> joins = new ArrayList<>();
  @ApiModelProperty(value = "促销条目列表", required = true)
  private List<RSPromotionItem> items = new ArrayList<>();
  @ApiModelProperty(value = "费用承担明细", required = true)
  private List<H6FavorSharingDetail> sharingDetails = new ArrayList<>();
}
