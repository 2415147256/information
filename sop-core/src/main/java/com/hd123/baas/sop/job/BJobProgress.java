/**
 * 版权所有(C)，上海海鼎信息工程股份有限公司，2015，所有权利保留。
 * <p>
 * 项目名：	zjlh-cntr-web
 * 文件名：	BJobProgress.java
 * 模块说明：
 * 修改历史：
 * 2015年3月25日 - neilspears - 创建。
 */
package com.hd123.baas.sop.job;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BJobProgress {

  public static final String JOB_RESULT = "job_result";
  public static final String JOB_GROUP = "job_progress";
  public static final String JOB_SUCCESS_COUNT = "successCount";
  public static final String JOB_FAIL_COUNT = "failCount";
  public static final String JOB_IGNORE_COUNT = "ignoreCount";

  @ApiModelProperty(value = "最小值", required = true)
  private final int minimum = 0;
  @ApiModelProperty(value = "最大值", required = true)
  private int maximum = 100;
  @ApiModelProperty(value = "当前位置", required = true)
  private float position;
  @ApiModelProperty(value = "进度百分比", required = true)
  private float percent;
  @ApiModelProperty(value = "当前进展", required = true)
  private String lastMessage;
  @ApiModelProperty(value = "是否启动", required = true)
  private boolean started;
  @ApiModelProperty(value = "是否完成", required = true)
  private boolean finished;
  @ApiModelProperty(value = "是否成功", required = true)
  private boolean success;
  @ApiModelProperty(value = "导入结果的excel地址", required = true)
  private Object result;
  @ApiModelProperty(value = "成功数", required = true, example = "20")
  private int successCount;
  @ApiModelProperty(value = "失败数", required = true, example = "20")
  private int failCount;
  @ApiModelProperty(value = "忽略数", required = true, example = "20")
  private int ignoreCount;

}
