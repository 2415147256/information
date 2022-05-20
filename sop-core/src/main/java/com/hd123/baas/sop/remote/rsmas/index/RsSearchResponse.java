/**
 * 版权所有(C)，上海海鼎信息工程股份有限公司，2021，所有权利保留。
 * 
 * 项目名：	sop-service
 * 文件名：	RsSearchResponse.java
 * 模块说明：	
 * 修改历史：
 * 2021年1月11日 - hezhenhui - 创建。
 */
package com.hd123.baas.sop.remote.rsmas.index;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * 搜索响应结果
 * 
 * @author lsz
 */
@Getter
@Setter
@ApiModel
public class RsSearchResponse {

  @ApiModelProperty(value = "页号")
  private int page;
  @ApiModelProperty(value = "页记录数")
  private int pageSize;
  @ApiModelProperty(value = "当前页")
  private int pageCount;
  @ApiModelProperty(value = "总数")
  private int total;
  @ApiModelProperty(value = "文档列表")
  private List<RsDocument> documents = new ArrayList<>();
  @ApiModelProperty(value = "聚合结果")
  private Map<String, Object> aggs = new HashMap<String, Object>();
}
