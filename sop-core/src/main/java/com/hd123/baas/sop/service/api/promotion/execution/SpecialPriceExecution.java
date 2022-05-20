package com.hd123.baas.sop.service.api.promotion.execution;

import com.hd123.baas.sop.service.api.promotion.PomEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Data
@ApiModel("换购优惠")
public class SpecialPriceExecution {
  private List<Item> items = new ArrayList<>();

  @Data
  @ApiModel("换购优惠Item")
  public static class Item {
    private PomEntity entity;
    private Form form;
    private BigDecimal value;
  }

  /**
   * 优惠方式
   *
   * @author lxm
   */
  public enum Form {
    @ApiModelProperty("促销价")
    price,
    @ApiModelProperty("折扣率")
    discount,
  }
}
