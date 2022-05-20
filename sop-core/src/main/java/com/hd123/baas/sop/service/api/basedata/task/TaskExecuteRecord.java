/**
 * 版权所有(C)，上海海鼎信息工程股份有限公司，2015，所有权利保留。
 * <p>
 * 项目名：	mas-commons-api
 * 文件名：	TaskExecuteRecord.java
 * 模块说明：
 * 修改历史：
 * <p>
 * 2019年10月9日 - lsz - 创建。
 */
package com.hd123.baas.sop.service.api.basedata.task;

import com.hd123.rumba.commons.biz.entity.Entity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Date;

/**
 *
 * @author lsz
 */
@Getter
@Setter
@ApiModel
public class TaskExecuteRecord extends Entity {
  private static final long serialVersionUID = 9035821143814462598L;

  @ApiModelProperty(value = "租户")
  private String tenant;
  @ApiModelProperty(value = "组织类型")
  private String orgType;
  @ApiModelProperty(value = "组织id")
  private String orgId;
  @ApiModelProperty(value = "ID")
  private String id;
  @ApiModelProperty(value = "任务ID")
  private String taskId;
  @ApiModelProperty(value = "执行状态")
  private ExecuteStatus status = ExecuteStatus.ready;
  @ApiModelProperty(value = "执行开始时间")
  private Date beginTime;
  @ApiModelProperty(value = "执行结束时间")
  private Date endTime;
  @ApiModelProperty(value = "进度（从0到1）")
  private BigDecimal progress = BigDecimal.ZERO;
  @ApiModelProperty(value = "执行人标识")
  private String workerId;
  @ApiModelProperty(value = "失败原因")
  private String failReason;

}
