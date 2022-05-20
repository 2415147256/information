/**
 * 版权所有(C)，上海海鼎信息工程股份有限公司，2016，所有权利保留。
 * <p>
 * 项目名：	mas-parent
 * 文件名：	SellingTime.java
 * 模块说明：
 * 修改历史：
 * 2020/12/25 - lzy - 创建。
 */
package com.hd123.baas.sop.remote.rsmas.sku;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * @author lzy
 */
@Getter
@Setter
@ApiModel("售卖时间")
public class RsSellingTime {

  @ApiModelProperty(value = "全时段可售卖", required = true)
  private Boolean allTimeSale;
  @ApiModelProperty(value = "起始日期(全时段可售卖时不填)")
  private Date beginTime;
  @ApiModelProperty(value = "截止日期(全时段可售卖时不填)")
  private Date endTime;
  @ApiModelProperty(value = "星期范围,7位字符串,1表示可售,0表示不可售(全时段可售卖时不填)")
  private String weeks;
  @ApiModelProperty(value = "全天可售(全时段可售卖时不填)")
  private Boolean availableAllDay;

  @ApiModelProperty(value = "时间段(全时段可售卖、全天可售时不填)")
  private List<RsTimeRange> periods = new ArrayList<>();

}
