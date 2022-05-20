package com.hd123.baas.sop.fcf.service.api.process;

import com.hd123.baas.sop.remote.bigdata.DayInfo;
import com.hd123.baas.sop.remote.bigdata.Weather;
import com.hd123.rumba.commons.biz.entity.StandardEntity;
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
public class ProcessPlanOrder extends StandardEntity {
  private static final long serialVersionUID = -9076702018183781595L;

  @ApiModelProperty(value = "租户", example = "fcf")
  private String tenant;
  @ApiModelProperty(value = "业务主键", example = "fcf")
  private String billNumber;
  @ApiModelProperty(value = "解冻处理日 用于app查询", example = "2020-4-15")
  private Date processDate;
  @ApiModelProperty(value = "门店id", example = "100212")
  private String storeId;
  @ApiModelProperty(value = "门店代码", example = "2120")
  private String storeCode;
  @ApiModelProperty(value = "门店名称", example = "超市")
  private String storeName;
  @ApiModelProperty(value = "制作单状态,可选： todo,doing,confirmed; 默认是todo", example = "todo")
  private String state = "todo";

  @ApiModelProperty(value = "已完成品项数", example = "88")
  private BigDecimal confirmedCount;
  @ApiModelProperty(value = "总品项数", example = "300")
  private BigDecimal totalCount;

  @ApiModelProperty("今日天气情况")
  private Weather weather;
  @ApiModelProperty("今日日期情况")
  private DayInfo dayInfo;

  @ApiModelProperty("类目明细")
  private List<ProcessPlanCategory> categories = new ArrayList<>();

}
