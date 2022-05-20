/**
 * 版权所有(C)，上海海鼎信息工程股份有限公司，2020，所有权利保留。
 * <p>
 * 项目名：	sos_idea
 * 文件名：	StoreInvXFReqAutoApprove.java
 * 模块说明：
 * 修改历史：
 * 2020/11/5 - Leo - 创建。
 */

package com.hd123.baas.sop.remote.rsh6sop.invxfapply;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * @author Leo
 */
@Data
@ApiModel(description = "调拨申请自动审批配置")
public class RsInvXFApplyAutoAudit implements Serializable {
  private static final long serialVersionUID = -4403730614440780369L;

  @ApiModelProperty(value = "调拨申请自动审批配置", example = "true", required = true)
  @NotNull
  private Boolean autoAudit;

}
