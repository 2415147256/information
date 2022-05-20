/**
 * 版权所有(C)，上海海鼎信息工程股份有限公司，2020，所有权利保留。
 * <p>
 * 项目名： workspace_mkh
 * 文件名： SOPPromotionItem.java
 * 模块说明：
 * 修改历史：
 * 2020年11月30日 - huangjunxian - 创建。
 */
package com.hd123.baas.sop.remote.rsh6sop.prom;

/**
 * @author huangjunxian
 * @since 1.0
 */

import java.io.Serializable;

import javax.validation.constraints.NotBlank;

import org.hibernate.validator.constraints.Length;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(description = "促销条目")
public class RSPromotionItem implements Serializable {
  @NotBlank
  @Length(max = 38)
  @ApiModelProperty(value = "条目标识", required = true)
  private String itemUuid;
  @NotBlank
  @Length(max = 38)
  @ApiModelProperty(value = "gid", required = true)
  private Integer gdGid;

}