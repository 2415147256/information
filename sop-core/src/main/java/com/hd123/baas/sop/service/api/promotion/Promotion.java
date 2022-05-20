package com.hd123.baas.sop.service.api.promotion;

import com.hd123.baas.sop.service.api.promotion.condition.PacketCondition;
import com.hd123.baas.sop.service.api.promotion.condition.ProductCondition;
import com.hd123.baas.sop.service.api.promotion.condition.StepCondition;
import com.hd123.baas.sop.service.api.promotion.execution.ExecutionSet;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
public class Promotion {
  @ApiModelProperty("组织Id")
  private String orgId;
  @ApiModelProperty("促销类型")
  private PromotionType promotionType;
  @ApiModelProperty("促销方式")
  private String promotionMode;
  @ApiModelProperty("促销描述")
  private String description;
  @ApiModelProperty("促销商品")
  private ProductCondition productCondition;
  @ApiModelProperty("阶梯条件")
  private StepCondition stepCondition;
  @ApiModelProperty("商品分组条件")
  private PacketCondition packetCondition;
  @ApiModelProperty("促销优惠")
  private ExecutionSet executionSet;
  @ApiModelProperty("促销计算参数")
  private List<String> executionOptions;
}
