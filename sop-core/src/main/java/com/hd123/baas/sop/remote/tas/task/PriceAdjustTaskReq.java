package com.hd123.baas.sop.remote.tas.task;

import com.hd123.rumba.commons.biz.entity.UCN;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;
import java.util.Date;
import java.util.List;

/**
 * 外部服务-任务创建请求
 *
 * @since 1.3.0
 */
@Setter
@Getter
@ApiModel("外部服务-任务创建请求")
public class PriceAdjustTaskReq {
  @ApiModelProperty(value = "任务uuid")
  private String uuid;
  @ApiModelProperty(value = "任务计划ID", required = true)
  private String planId;
  @ApiModelProperty(value = "任务计划名称", required = true)
  private String planName;
  @ApiModelProperty(value = "发生单据ID", required = true)
  private String orderId;
  @ApiModelProperty(value = "发生单据类型", required = true, example = "售价改价单|促销单")
  private String orderType;
  @ApiModelProperty(value = "发生单据编号", required = true)
  private String orderNo;
  @ApiModelProperty(value = "单据生效日期", required = true, example = "yyyy-MM-dd")
  private String validDate;
  @ApiModelProperty(value = "结束日期", example = "活动结束时间 yyyy-MM-dd HH:mm:ss")
  private Date endDate;
  @ApiModelProperty(value = "单据审核时间", required = true, example = "单据审核时间 yyyy-MM-dd HH:mm:ss")
  private Date auditTime;
  @ApiModelProperty(value = "门店信息", required = true, example = "生效门店ID/代码/名称")
  private UCN shop;
  @ApiModelProperty(value = "任务创建明细", required = true)
  @NotEmpty
  private List<PriceAdjustTaskReqLine> lines;
}
