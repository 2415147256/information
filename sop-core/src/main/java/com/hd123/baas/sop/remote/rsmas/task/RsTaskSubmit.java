/**
 * 版权所有(C)，上海海鼎信息工程股份有限公司，2015，所有权利保留。
 * <p>
 * 项目名：	mas-commons-api
 * 文件名：	TaskCreation.java
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

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author lsz
 */
@Getter
@Setter
@ApiModel
public class RsTaskSubmit implements Serializable {
  private static final long serialVersionUID = -2961635935283568159L;

  @ApiModelProperty(value = "任务KEY，用于任务队列消息分发")
  private String key;
  @ApiModelProperty(value = "任务id，可由外部传进来，为空的话自己生成")
  private String id;
  @ApiModelProperty(value = "分组，一般指应用或场景")
  @NotNull
  @NotBlank
  private String groupId;
  @ApiModelProperty(value = "类型，一般指业务分类")
  @NotNull
  @NotBlank
  private String type;
  @ApiModelProperty(value = "名称")
  @NotNull
  @NotBlank
  private String name;
  @ApiModelProperty(value = "周期表达式")
  @NotNull
  @NotBlank
  private String cornExpression;
  @ApiModelProperty(value = "周期类型")
  private RsTaskPeriodType periodType = RsTaskPeriodType.once;
  @ApiModelProperty(value = "备注")
  private String remark;
  @ApiModelProperty(value = "参数")
  private List<RsParameter> parameters = new ArrayList<RsParameter>();

}
