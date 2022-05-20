package com.hd123.baas.sop.fcf.controller.process;

import com.hd123.baas.sop.service.api.TenantEntity;
import com.hd123.rumba.commons.biz.entity.UCN;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

/**
 * @author zhangweigang
 */
@Getter
@Setter
@ApiModel("智能制作计划单概览")
public class BProcessPlanOverView extends TenantEntity {
  private static final long serialVersionUID = 1635312276084379551L;
  @ApiModelProperty(value = "计划单物理主键", example = "ee739156-b529-44e6-8fc7-ce5eb0244863")
  private String uuid;
  @ApiModelProperty("计划单业务主键")
  private String billNumber;

  @ApiModelProperty("门店")
  private UCN store;

  @ApiModelProperty("餐段合集")
  private List<BFreshMealTime> mealTimes = new ArrayList<>();

  @ApiModelProperty
  private List<BProcessPlanCategoryOverView> overViews = new ArrayList<>();
}
