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
package com.hd123.baas.sop.remote.rsmas.task;

import com.hd123.baas.sop.remote.rsmas.goods.RsParameter;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 *
 * @author lsz
 */
@Getter
@Setter
@ApiModel
public class RsTaskExecuteReport implements Serializable {
  private static final long serialVersionUID = 3448618077004851883L;

  @ApiModelProperty(value = "报告类型")
  @NotNull
  private ReportType reportType;
  @ApiModelProperty(value = "执行结果")
  private RsExecuteResult executeResult;
  @ApiModelProperty(value = "进度（从0到1）")
  private BigDecimal progress;
  @ApiModelProperty(value = "执行人标识")
  private String workerId;
  @ApiModelProperty(value = "失败原因")
  private String failReason;
  @ApiModelProperty(value = "参数")
  private List<RsParameter> parameters;

}
