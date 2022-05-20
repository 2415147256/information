/**
 * 版权所有(C)，上海海鼎信息工程股份有限公司，2015，所有权利保留。
 * <p>
 * 项目名：	mas-commons-api
 * 文件名：	TaskExecuteRecordFilter.java
 * 模块说明：
 * 修改历史：
 * <p>
 * 2019年10月9日 - lsz - 创建。
 */
package com.hd123.baas.sop.remote.rsmas.task;

import com.hd123.baas.sop.remote.rsmas.RsMasFilter;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author lsz
 */
@Getter
@Setter
@ApiModel
public class RsTaskExecuteRecordFilter extends RsMasFilter {
  private static final long serialVersionUID = -8039043540093490569L;

  @ApiModelProperty(value = "ID等于")
  private String idEq;
  @ApiModelProperty(value = "作业ID等于")
  private String taskIdEq;
  @ApiModelProperty(value = "作业ID在...范围内")
  private List<String> taskIdIn;
  @ApiModelProperty(value = "执行人标识等于")
  private String workerIdEq;
  @ApiModelProperty(value = "参数内容条件")
  private List<RsParameterCondition> parameterConditions = new ArrayList<RsParameterCondition>();
  @ApiModelProperty("组织类型等于")
  private String orgTypeEq;
  @ApiModelProperty("组织id等于")
  private String orgIdEq;

}
