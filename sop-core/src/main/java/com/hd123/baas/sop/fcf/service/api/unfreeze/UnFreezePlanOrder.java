package com.hd123.baas.sop.fcf.service.api.unfreeze;

import com.hd123.baas.sop.remote.bigdata.DayInfo;
import com.hd123.baas.sop.remote.bigdata.Weather;
import com.hd123.rumba.commons.biz.entity.StandardEntity;
import com.hd123.rumba.commons.biz.query.QueryEntity;
import com.hd123.rumba.commons.biz.query.QueryFactors;
import com.hd123.rumba.commons.biz.query.QueryField;
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
public class UnFreezePlanOrder extends StandardEntity {

  private static final long serialVersionUID = 819583573266506368L;
  @ApiModelProperty(value = "租户", example = "fcf")
  private String tenant;
  @ApiModelProperty(value = "计划单物理主键", example = "b78465d9-f38e-41eb-b862-feb5f9c69992")
  private String uuid;// 自己生成

  @ApiModelProperty(value = "计划单业务主键", example = "20210331160659123")
  private String billNumber;// 自己生成

  @ApiModelProperty(value = "解冻处理日 用于app查询", example = "2020-4-15")
  private Date processDate;
  // @ApiModelProperty(value = "每日解冻时间段uuid。解冻时间段是基础信息，且只会有一条记录", example =
  // "12212")
  // private String unFreezeTimeId;
  @ApiModelProperty(value = "状态可选todo,doing,confirmed; 默认是todo， 解冻任何一个商品后变成doing", example = "todo")
  private String state;
  @ApiModelProperty(value = "门店id", example = "100212")
  private String storeId;// 从mas2取
  @ApiModelProperty(value = "门店代码", example = "2120")
  private String storeCode;// 从mas2取
  @ApiModelProperty(value = "门店名称", example = "超市")
  private String storeName;// 从mas2取

  @ApiModelProperty("明日天气情况")
  private Weather weather;// 大数据返回

  @ApiModelProperty(value = "明日日期情况")
  private DayInfo dayInfo;// 大数据返回

  @ApiModelProperty(value = "解冻计划生成时间", example = "2021-03-31 23:21:06")
  private Date createTime;// 自己生成，当前时间

  @ApiModelProperty(value = "已完成品项数", example = "88")
  private BigDecimal confirmedCount;// 默认无，单据更新时改变

  @ApiModelProperty(value = "总品项数", example = "300")
  private BigDecimal totalCount;// 参考大数据返回数

  @ApiModelProperty("解冻时间")
  private DailyUnFreezeTime unFreezeTime;// 固定的一条数据

  @ApiModelProperty("类目明细")
  private List<UnFreezePlanCategory> categories = new ArrayList<>();// 从mas2查，通过gid+qpc查到所有category，还需要用map维护起来一一对应

  @QueryEntity(UnFreezePlanOrder.class)
  public static abstract class Queries extends QueryFactors.StandardEntity {
    private static final String PREFIX = UnFreezePlanOrder.class.getName() + "::";
    @QueryField
    public static final String TENANT = PREFIX + "tenant";
    @QueryField
    public static final String PROCESS_DATE = PREFIX + "processDate";
    @QueryField
    public static final String STORE_ID = PREFIX + "storeId";
    @QueryField
    public static final String STORE_CODE = PREFIX + "storeCode";
  }

}
