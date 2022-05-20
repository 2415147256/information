/**
 * 版权所有(C)，上海海鼎信息工程股份有限公司，2020，所有权利保留。
 * <p>
 * 项目名：	sos_idea
 * 文件名：	InvXFDayCheck.java
 * 模块说明：
 * 修改历史：
 * 2020/11/10 - Leo - 创建。
 */

package com.hd123.baas.sop.remote.rssos.invxf;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Date;

/**
 * @author Leo
 */
@ApiModel(description = "当日调拨检查")
@Data
public class RsInvXFDayCheck implements Serializable {
  private static final long serialVersionUID = 474889194173788365L;

  @ApiModelProperty(value = "起始时间", example = "2020-10-10 00:00:00", required = true)
  private Date startTime;
  @NotNull
  @ApiModelProperty(value = "截止时间", example = "2020-10-10 23:59:59", required = true)
  @NotNull
  private Date endTime;
}
