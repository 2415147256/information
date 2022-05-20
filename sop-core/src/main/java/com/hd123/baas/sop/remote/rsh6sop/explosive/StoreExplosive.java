/**
 * 版权所有(C)，上海海鼎信息工程股份有限公司，2021，所有权利保留。
 * <p>
 * 项目名： workspace_mkh
 * 文件名： PExplosive.java
 * 模块说明：
 * 修改历史：
 * 2021年01月13日 - huangjunxian - 创建。
 */
package com.hd123.baas.sop.remote.rsh6sop.explosive;

/**
 * @author huangjunxian
 * @since 1.0
 */

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Data
@ApiModel(description = "爆品预订活动")
public class StoreExplosive implements Serializable {
  @NotBlank
  @Length(max = 32)
  @ApiModelProperty(value = "活动代码", example = "0001", required = true)
  private String activityCode;
  @NotBlank
  @Length(max = 64)
  @ApiModelProperty(value = "活动名称", example = "活动", required = true)
  private String activityName;
  @NotNull
  @ApiModelProperty(value = "门店标识", required = true)
  private String storeUuid;
  @NotNull
  @ApiModelProperty(value = "开始时间", required = true)
  private Date beginTime;
  @NotNull
  @ApiModelProperty(value = "截止时间", required = true)
  private Date endTime;

  @Valid
  @NotEmpty
  private List<StoreExplosiveDetail> details = new ArrayList<>();

}
