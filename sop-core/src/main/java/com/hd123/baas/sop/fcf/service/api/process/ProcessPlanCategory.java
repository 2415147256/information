package com.hd123.baas.sop.fcf.service.api.process;

import com.hd123.baas.sop.fcf.controller.process.ProcessPlanGoods;
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
@ApiModel("类目明细")
public class ProcessPlanCategory {
  @ApiModelProperty
  private UCN categoryInfo;

  @ApiModelProperty(value = "建议的品项件数(数量)", example = "100")
  private BigDecimal suggestQty;
  @ApiModelProperty(value = "已处理的品项件量(数量)", example = "10")
  private BigDecimal confirmedQty;

  @ApiModelProperty(value = "待办品项数", example = "2")
  private BigDecimal unConfirmCount;

  @ApiModelProperty(value = "待办品项件数(数量)", example = "24")
  private BigDecimal unConfirmQty;

  @ApiModelProperty(value = "上周同期天气状况", example = "晴天")
  private String lastWeekWeather;

  @ApiModelProperty(value = "上周同期销量", example = "100")
  private BigDecimal lastWeekSale;

  @ApiModelProperty("类目商品明细")
  private List<ProcessPlanGoods> goods = new ArrayList<>();
}
