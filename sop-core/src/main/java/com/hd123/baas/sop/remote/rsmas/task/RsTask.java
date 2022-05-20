/**
 * 版权所有(C)，上海海鼎信息工程股份有限公司，2015，所有权利保留。
 * 
 * 项目名：	mas-commons-api
 * 文件名：	Task.java
  * 模块说明：	
 * 修改历史：

 * 2019年10月9日 - lsz - 创建。
 */
package com.hd123.baas.sop.remote.rsmas.task;

import com.hd123.baas.sop.remote.rsmas.RsParameter;
import com.hd123.rumba.commons.biz.entity.StandardEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 作业
 * 
 * @author lsz
 */
@Getter
@Setter
@ApiModel
public class RsTask extends StandardEntity {
  private static final long serialVersionUID = -3023425941543738247L;

  @ApiModelProperty(value = "租户")
  private String tenant;
  @ApiModelProperty(value = "组织类型")
  private String orgType;
  @ApiModelProperty(value = "组织id")
  private String orgId;
  @ApiModelProperty(value = "任务KEY，用于任务队列消息分发")
  private String key;
  @ApiModelProperty(value = "分组，一般指应用或场景")
  private String groupId;
  @ApiModelProperty(value = "类型，一般指业务分类")
  private String type;
  @ApiModelProperty(value = "ID")
  private String id;
  @ApiModelProperty(value = "名称")
  private String name;
  @ApiModelProperty(value = "状态")
  private RsTaskStatus status = RsTaskStatus.submitted;
  @ApiModelProperty(value = "周期表达式")
  private String cornExpression;
  @ApiModelProperty(value = "周期类型")
  private RsTaskPeriodType periodType = RsTaskPeriodType.once;
  @ApiModelProperty(value = "初次执行时间")
  private Date firstExecuteTime;
  @ApiModelProperty(value = "下次执行时间")
  private Date nextExecuteTime;
  @ApiModelProperty(value = "结束时间")
  private Date endTime;
  @ApiModelProperty(value = "备注")
  private String remark;
  @ApiModelProperty(value = "参数")
  private List<RsParameter> parameters = new ArrayList<RsParameter>();

}
