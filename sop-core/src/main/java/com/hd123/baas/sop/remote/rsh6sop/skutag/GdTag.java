/**
 * 版权所有(C)，上海海鼎信息工程股份有限公司，2021，所有权利保留。
 * 
 * 项目名：	h6-sop-api
 * 文件名：	GdTag.java
 * 模块说明：	
 * 修改历史：
 * 2021年9月10日 - huangchun - 创建。
 */
package com.hd123.baas.sop.remote.rsh6sop.skutag;

import java.io.Serializable;

import javax.validation.constraints.NotBlank;

import org.hibernate.validator.constraints.Length;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author huangchun
 *
 */
@Data
@ApiModel(description = "商品标签")
@EqualsAndHashCode
public class GdTag implements Serializable {
  private static final long serialVersionUID = -562408724640218715L;
  @ApiModelProperty(value = "组织ID", example = "1000000", required = true)
  private Integer orgGid;
  @ApiModelProperty(value = "商品标签ID", example = "001", required = true)
  @NotBlank
  @Length(max = 64)
  private String tagId;
  @ApiModelProperty(value = "商品标签名称", example = "高毛利", required = true)
  @NotBlank
  @Length(max = 64)
  private String tagName;

}
