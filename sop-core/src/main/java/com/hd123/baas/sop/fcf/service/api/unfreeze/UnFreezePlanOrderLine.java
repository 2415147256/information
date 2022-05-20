package com.hd123.baas.sop.fcf.service.api.unfreeze;

import com.hd123.rumba.commons.biz.entity.Entity;
import com.hd123.rumba.commons.biz.query.QueryEntity;
import com.hd123.rumba.commons.biz.query.QueryFactors;
import com.hd123.rumba.commons.biz.query.QueryField;
import com.hd123.rumba.commons.biz.query.QueryFieldPurpose;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * @author zhangweigang
 */
@Getter
@Setter
public class UnFreezePlanOrderLine extends Entity {

  private static final long serialVersionUID = -3026998818324969497L;
  @ApiModelProperty(value = "租户", example = "fcf")
  private String tenant;
  @ApiModelProperty(value = "序号", example = "1")
  private String lineNo;
  @ApiModelProperty(value = "所属计划的uuid", example = "20210331160659123")
  private String planUuid;
  @ApiModelProperty(value = "规格商品主键", example = "20210331160659123")
  private String skuId;
  @ApiModelProperty(value = "商品名称", example = "包子")
  private String productName;
  @ApiModelProperty(value = "建议数", example = "12")
  private BigDecimal suggestQty;
  @ApiModelProperty(value = "实际数", example = "12")
  private BigDecimal qty;
  @ApiModelProperty(value = "所属类目uuid", example = "20210331160659123")
  private String categoryUuid;
  @ApiModelProperty(value = "所属类目代码", example = "20210331160659123")
  private String categoryCode;
  @ApiModelProperty(value = "所属类目名称", example = "20210331160659123")
  private String categoryName;
  @ApiModelProperty(value = "前四周平均销量", example = "12")
  private BigDecimal avgLast4weekSale;
  @ApiModelProperty(value = "状态,是否完成 执行过解冻即是完成 可选 todo，confirmed", example = "todo")
  private String state;

  @QueryEntity(UnFreezePlanOrderLine.class)
  public static abstract class Queries extends QueryFactors.Entity {
    private static final String PREFIX = UnFreezePlanOrderLine.class.getName() + "::";
    @QueryField
    public static final String PLAN_UUID = PREFIX + "planUuid";
    @QueryField
    public static final String TENANT = PREFIX + "tenant";
    @QueryField
    public static final String STATE = PREFIX + "state";
    @QueryField(purposes = QueryFieldPurpose.order)
    public static final String AVG_LAST_4_WEEK_SALE = "avgLast4weekSale";
  }

  /*
   * 
   * 
   * @ApiModelProperty("明日天气情况") private Weather weather;
   * 
   * @ApiModelProperty(value = "明日日期情况") private DayInfo dayInfo;
   * 
   * @ApiModelProperty(value = "解冻计划生成时间", example = "2021-03-31 23:21:06")
   * private Date createTime;
   * 
   * @ApiModelProperty("门店") private UCN store;
   * 
   * @ApiModelProperty(value = "已完成品项数", example = "88") private BigDecimal
   * confirmedCount;
   * 
   * @ApiModelProperty(value = "总品项数", example = "300") private BigDecimal
   * totalCount;
   */
}
