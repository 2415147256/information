package com.hd123.baas.sop.job.entity;

import java.util.Date;

import org.apache.commons.lang.StringUtils;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.hd123.rumba.commons.biz.entity.Entity;
import com.hd123.rumba.commons.biz.query.QueryEntity;
import com.hd123.rumba.commons.biz.query.QueryFactors;
import com.hd123.rumba.commons.biz.query.QueryField;

import lombok.Getter;
import lombok.Setter;

/**
 * @author zhengzewang
 */
@Getter
@Setter
public class TimedJob extends Entity {
  private String tranId; // 业务ID，由调用方生成。比如订单过期任务可以用"Order" +"_" + order.uuid作为tranId
  private String params; // 参数
  private String interval;// 运行间隔（支持数组,以逗号,分割），单位是秒. 60,300
  private int runTimes; // 已运行次数

  private Date expectedRunTime; // 期望运行时间
  private String callbackBeanName; // 回调bean名称

  @JsonIgnore
  public int[] getIntervals() {
    if (StringUtils.isBlank(interval)) {
      return new int[0];
    }
    String[] nums = interval.split(",");
    int[] target = new int[nums.length];
    for (int i = 0; i < nums.length; i++) {
      target[i] = Integer.parseInt(nums[i]);
    }
    return target;
  }

  @QueryEntity(TimedJob.class)
  public static class Queries extends QueryFactors.Entity {

    private static final String PREFIX = TimedJob.class.getName() + "::";
    @QueryField
    public static final String EXPECTED_RUN_TIME = PREFIX + "expectedRunTime";
    @QueryField
    public static final String CALLBACK_BEAN_NAME = PREFIX + "callbackBeanName";
  }
}
