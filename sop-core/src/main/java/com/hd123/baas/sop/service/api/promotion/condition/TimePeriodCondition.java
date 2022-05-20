package com.hd123.baas.sop.service.api.promotion.condition;

import io.swagger.annotations.ApiModel;
import lombok.Data;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Data
@ApiModel("时段促销")
public class TimePeriodCondition {
  protected List<TimeRange> periods = new ArrayList<>();
  protected CycleAlgorithm algorithm = CycleAlgorithm.inside;

  @Data
  public static class TimeRange {
    private Date start;
    private Date finish;
  }

  /**
   * @author zhuchao
   */
  public enum CycleAlgorithm {
    /** 在指定周期内 */
    inside,
    /** 在指定周期外 */
    outside
  }
}
