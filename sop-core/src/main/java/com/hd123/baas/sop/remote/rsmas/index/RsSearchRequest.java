/**
 * 版权所有(C)，上海海鼎信息工程股份有限公司，2021，所有权利保留。
 * 
 * 项目名：	sop-service
 * 文件名：	RsSearchRequest.java
 * 模块说明：	
 * 修改历史：
 * 2021年1月11日 - hezhenhui - 创建。
 */
package com.hd123.baas.sop.remote.rsmas.index;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import com.hd123.baas.sop.remote.rsmas.RsSort;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * @author hezhenhui
 *
 */
@Getter
@Setter
@ApiModel
public class RsSearchRequest {
  @ApiModelProperty(value = "页号")
  private int page;
  @ApiModelProperty(value = "页记录数")
  private int pageSize;
  @NotNull
  @NotBlank
  @ApiModelProperty(value = "INDEX")
  private String index;
  @NotNull
  @NotBlank
  @ApiModelProperty(value = "TYPE")
  private String type;
  @ApiModelProperty(value = "查询条件，多个之间AND关系")
  private List<RsCondition> conditions = new ArrayList<>();
  @ApiModelProperty(value = "联合查询条件，多个之间AND关系")
  private List<RsUnionCondition> unionConditions = new ArrayList<>();
  @ApiModelProperty(value = "或查询条件，多个之间OR关系")
  private List<RsMultiAndCondition> orConditions = new ArrayList<>();
  @ApiModelProperty(value = "排序条件")
  private List<RsSort> sorts = new ArrayList<>();
  @ApiModelProperty(value = "返回字段，不传则默认返回所有字段")
  private List<String> fields = new ArrayList<String>();
  @ApiModelProperty(value = "字段折叠")
  private Map<String, Object> collapse = new HashMap<String, Object>();
  @ApiModelProperty(value = "聚合查询")
  private Map<String, Object> aggs = new HashMap<String, Object>();
}
