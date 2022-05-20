package com.hd123.baas.sop.service.api.pms.explosive;

import com.hd123.rumba.commons.biz.entity.UCN;
import com.hd123.rumba.commons.biz.query.QueryEntity;
import com.hd123.rumba.commons.biz.query.QueryField;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
@ApiModel("爆品预定门店明细")
public class ExplosiveActivityDetailJoin {
  public static final String FILTER_DETAIL_UUID_EQUAL = "detailUuid:=";
  public static final String FILTER_STORE_UUID_EQUAL = "storeUuid:=";
  public static final String FILTER_SIGN_DATE_EQUAL = "signDate:=";
  public static final String FILTER_SIGN_DATE_BETWEEN = "signDate:[,]";

  @ApiModelProperty("租户")
  private String tenant;
  @ApiModelProperty("门店")
  private UCN store;
  @ApiModelProperty("配货规格")
  private BigDecimal alcQpc;
  @ApiModelProperty("配货单位")
  private String alcUnit;
  @ApiModelProperty("预定日期")
  private Date signDate;
  @ApiModelProperty("订货量")
  private BigDecimal signQty;

  @ApiModelProperty("爆品预定UUID")
  private String detailUuid;

  @QueryEntity(ExplosiveActivityDetailJoin.class)
  public static class Queries {
    private static final String PREFIX = ExplosiveActivityDetailJoin.class.getName() + "::";
    @QueryField
    public static final String TENANT = PREFIX + "tenant";
    @QueryField
    public static final String DETAIL_UUID = PREFIX + "detailUuid";
    @QueryField
    public static final String SIGN_QTY = PREFIX + "signQty";
    @QueryField
    public static final String STORE_UUID = PREFIX + "store.uuid";
    @QueryField
    public static final String SIGN_DATE = PREFIX + "signDate";
  }
}
