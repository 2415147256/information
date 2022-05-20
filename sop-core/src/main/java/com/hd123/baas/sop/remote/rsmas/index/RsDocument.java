/**
 * 版权所有(C)，上海海鼎信息工程股份有限公司，2021，所有权利保留。
 * 
 * 项目名：	sop-service
 * 文件名：	RsDocument.java
 * 模块说明：	
 * 修改历史：
 * 2021年1月11日 - hezhenhui - 创建。
 */
package com.hd123.baas.sop.remote.rsmas.index;

import java.util.HashMap;
import java.util.Map;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * @author hezhenhui
 *
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ApiModel("文档")
public class RsDocument {
  @NotNull
  @NotBlank
  @ApiModelProperty("索引index")
  private String index;
  @NotNull
  @NotBlank
  @ApiModelProperty("索引type")
  private String type;
  @NotNull
  @NotBlank
  @ApiModelProperty("文档id")
  private String id;
  @NotNull
  @ApiModelProperty("文档数据")
  private Map<String, Object> fields = new HashMap();

}
