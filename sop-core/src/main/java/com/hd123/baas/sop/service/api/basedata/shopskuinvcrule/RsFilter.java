/**
 * 版权所有(C)，上海海鼎信息工程股份有限公司，2015，所有权利保留。
 * 
 * 项目名：	mas-commons-biz
 * 文件名：	Filter.java
  * 模块说明：	
 * 修改历史：

 * 2019年8月20日 - lsz - 创建。
 */
package com.hd123.baas.sop.service.api.basedata.shopskuinvcrule;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;

/**
 * 
 * @author lsz
 */
@Getter
@Setter
public abstract class RsFilter implements Serializable {
  private static final long serialVersionUID = 4278270637116391234L;

  @ApiModelProperty(value = "页号")
  private int page;
  @ApiModelProperty(value = "页记录数")
  private int pageSize;
  @ApiModelProperty(value = "排序字段集合")
  private List<RsSort> sorts;
  @ApiModelProperty(value = "级联查询信息")
  private String fetchParts;

}
