package com.hd123.baas.sop.service.api.pms.explosive;

import com.hd123.baas.sop.service.api.promotion.PomEntity;
import com.hd123.rumba.commons.biz.entity.OperateInfo;
import com.hd123.rumba.commons.biz.entity.UCN;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Data
@ApiModel("爆品活动报名明细")
public class ExplosiveActivitySignJoin  {

  @NotNull
  @ApiModelProperty("门店")
  private UCN store;
  @ApiModelProperty("提交信息")
  private OperateInfo submitInfo;

  @NotEmpty
  @NotNull
  private List<ExplosiveActivitySignLine> lines;

  @Data
  public static class ExplosiveActivitySignLine {
    @ApiModelProperty("预定日期")
    private Date signDate;
    @ApiModelProperty("商品")
    private PomEntity entity;
    @ApiModelProperty("订货量")
    private BigDecimal signQty;
  }
}
