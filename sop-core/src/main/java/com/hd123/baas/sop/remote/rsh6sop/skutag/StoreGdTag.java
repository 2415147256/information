/**
 * 版权所有(C)，上海海鼎信息工程股份有限公司，2021，所有权利保留。
 * 
 * 项目名：	h6-sop-api
 * 文件名：	StoreGdTag.java
 * 模块说明：	
 * 修改历史：
 * 2021年9月10日 - huangchun - 创建。
 */
package com.hd123.baas.sop.remote.rsh6sop.skutag;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.validation.constraints.NotNull;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author huangchun
 *
 */
@Data
@ApiModel(description = "门店商品标签")
@EqualsAndHashCode
public class StoreGdTag implements Serializable {
  private static final long serialVersionUID = -4806277758535983137L;

  @ApiModelProperty(value = "门店GID", example = "1000251", required = true)
  @NotNull
  private Integer storeGid;
  @ApiModelProperty(value = "商品GID", example = "1000073", required = true)
  @NotNull
  private Integer gdGid;
  @ApiModelProperty(value = "商品标签ID列表", required = true)
  private List<String> tagIds = new ArrayList<>();

}
