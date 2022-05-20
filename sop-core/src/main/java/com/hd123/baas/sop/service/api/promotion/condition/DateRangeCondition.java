package com.hd123.baas.sop.service.api.promotion.condition;

import com.hd123.spms.commons.calendar.DateRange;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
@ApiModel("促销日期")
public class DateRangeCondition {
  private DateRange dateRange;
  @ApiModelProperty("周期")
  private DateRangeCycle timeCycle;

  @Data
  @ApiModel("周期条件")
  public static class DateRangeCycle {
    @ApiModelProperty("促销日的计算方式")
    private By by = By.day;
    @ApiModelProperty(value = "周期间隔", notes = "取值范围为大于1的整数，默认为1。")
    private int every = 1;
    @ApiModelProperty(value = "计数值", notes = "取值范围是除0以外的所有整数，dayOfMonth时正整数表示从一个月的第一天开始计算，即正数；负整数表示从一个月的最后一天开始计算，即倒数。")
    private List<Integer> ordinal = new ArrayList<>();
    @ApiModelProperty(value = "一周中的一天", notes = "仅当按周计算时有效，dayOfMonth时与ordinal一起表示一个月中“正数或倒数第几个星期几”。")
    private List<DayOfWeek> dayOfWeek = new ArrayList<>();

  }

  @ApiModel("周期（月）促销日计算方式")
  public enum By {
    /** 按日计算 */
    day,
    /** 按周计算 */
    week,
    /** 按月的日计算 */
    dayOfMonth,
    /** 按月的周计算 */
    weekOfMonth,
  }

  public enum DayOfWeek {
    sunday, monday, tuesday, wednesday, thursday, friday, saturday;
  }
}
