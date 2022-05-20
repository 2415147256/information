package com.hd123.baas.sop.service.api.pomdata;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Data
public class PrmPriceQueryRequest {
  @NotNull
  @ApiModelProperty("门店标识")
  private String storeUuid;
  @NotNull
  @ApiModelProperty("指定日期")
  private Date targetDate;
  @NotNull
  @ApiModelProperty("查询商品行")
  private List<ProductLine> lines = new ArrayList<>();

  @Data
  public static class ProductLine {
    @NotNull
    @ApiModelProperty(value = "商品标识", required = true)
    private String gdGid;
    @NotNull
    @ApiModelProperty(value = "商品规格", required = true)
    private BigDecimal gdQpc;
    @ApiModelProperty("促销价，输出")
    private BigDecimal prmPrice;
  }
}
