package com.hd123.baas.sop.remote.rsmkhpms.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import com.hd123.rumba.commons.biz.entity.OperateInfo;
import com.qianfan123.baas.common.entity.BUcn;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * @author W.J.H.7
 */
@Getter
@Setter
public class BBasePricePromRes implements Serializable {

  @ApiModelProperty(value = "模板uuid")
  private String uuid;

  @ApiModelProperty(value = "组织ID")
  private String orgId;

  @ApiModelProperty(value = "名称")
  private String name;

  @ApiModelProperty(value = "模板说明")
  private String remark;
  @ApiModelProperty(value = "生效门店")
  private List<BUcn> shops;
  @ApiModelProperty(value = "是否全部门店")
  private boolean allShop;
  @ApiModelProperty(value = "模板代码/模型", example = "CURVE;BROKEN;FULL_DISCOUNT")
  private String code;
  @ApiModelProperty(value = "单号")
  private String flowNo;
  @ApiModelProperty(value = "生效开始时间")
  private Date effectiveStartDate;
  @ApiModelProperty(value = "生效结束时间")
  private Date effectiveEndDate;
  @ApiModelProperty(value = "状态")
  private String state;

  @ApiModelProperty(value = "促销行", notes = "仅在详情接口返回")
  private List<BBasePricePromLineRes> lines;
  @ApiModelProperty(value = "创建人信息")
  private OperateInfo createInfo;
  @ApiModelProperty(value = "最后修改人信息")
  private OperateInfo lastModifyInfo;

  @ApiModelProperty(value = "说明")
  private String reason;

  @ApiModelProperty("总部承担比例")
  private BigDecimal headSharingRate;
  @ApiModelProperty("督导承担比例")
  private BigDecimal supervisorSharingRate;
  @ApiModelProperty("最小起订额")
  private BigDecimal ordLimitAmount;
  @ApiModelProperty("最小起订量")
  private BigDecimal ordLimitQty;

}
