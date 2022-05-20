package com.hd123.baas.sop.fcf.service.api.process;

import com.hd123.rumba.commons.biz.entity.UCN;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * @author zhangweigang
 */
@Getter
@Setter
@ApiModel("各类目概览")
public class ProcessPlanCategoryOverView {
  private static final long serialVersionUID = 5964487425467749515L;
  @ApiModelProperty("类目信息")
  private UCN categoryInfo;

  @ApiModelProperty("各餐段完成品项件数之和")
  private BigDecimal qty;

  @ApiModelProperty("各类目餐段状态")
  private List<ProcessPlanGoodsSummaryState> mealStatus = new ArrayList<>();
}
