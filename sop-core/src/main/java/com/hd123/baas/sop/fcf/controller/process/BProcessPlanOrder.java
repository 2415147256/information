package com.hd123.baas.sop.fcf.controller.process;

import com.hd123.baas.sop.remote.bigdata.DayInfo;
import com.hd123.baas.sop.remote.bigdata.Weather;
import com.hd123.rumba.commons.biz.entity.UCN;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author zhangweigang
 */
@Getter
@Setter
@ApiModel("智能制作计划单")
public class BProcessPlanOrder {
  private static final long serialVersionUID = 754399733523304577L;
  @ApiModelProperty(value = "计划单物理主键", example = "ee739156-b529-44e6-8fc7-ce5eb0244863")
  private String uuid;
  @ApiModelProperty(value = "计划单业务主键", example = "20210331160659123")
  private String billNumber;

  @ApiModelProperty("门店")
  private UCN store;

  @ApiModelProperty("今日天气情况")
  private Weather weather;

  @ApiModelProperty("今日日期情况")
  private DayInfo dayInfo;

  @ApiModelProperty(value = "制作计划生成时间", example = "2021-03-31 23:21:06")
  private Date createTime;

  @ApiModelProperty(value = "已完成品项数", example = "88")
  private BigDecimal confirmedCount;

  @ApiModelProperty(value = "总品项数", example = "300")
  private BigDecimal totalCount;

  @ApiModelProperty("当前处理的餐段")
  private BFreshMealTime mealTime;

  @ApiModelProperty("类目明细")
  private List<BProcessPlanCategory> categories = new ArrayList<>();

}
