/**
 * 版权所有(C)，上海海鼎信息工程股份有限公司，2020，所有权利保留。
 * <p>
 * 项目名：	workspace_shop
 * 文件名：	RequireApplyAuditor.java
 * 模块说明：
 * 修改历史：
 * 2020年02月14日 - huangjunxian - 创建。
 */
package com.hd123.baas.sop.remote.rsh6sop.invxfapply;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;
import java.util.Date;

/**
 * @author huangjunxian
 * @since 1.0
 */
@Data
@ApiModel(description = "审批通过并通知ERP")
public class RsInvXFApplyAuditor implements Serializable {
  private static final long serialVersionUID = 1691898871252818703L;

  @ApiModelProperty(value = "单号", required = true)
  @NotBlank
  private String num;

  @ApiModelProperty(value = "审核时间", example = "2020-02-02 12:00:00", required = false)
  private Date auditTime;
  @ApiModelProperty(value = "审核人代码", example = "zhangsan", required = false)
  private String auditorId;
  @ApiModelProperty(value = "审核人名称", example = "张三", required = false)
  private String auditorName;
}
