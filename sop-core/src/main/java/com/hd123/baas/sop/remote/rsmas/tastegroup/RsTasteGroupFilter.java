/**
 * 版权所有(C)，上海海鼎信息工程股份有限公司，2016，所有权利保留。
 * <p>
 * 项目名：	mas-parent
 * 文件名：	TasteGroupFilter.java
 * 模块说明：
 * 修改历史：
 * 2020/12/22 - lzy - 创建。
 */
package com.hd123.baas.sop.remote.rsmas.tastegroup;

import com.hd123.baas.sop.remote.rsmas.RsMasFilter;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * @author lzy
 */
@Getter
@Setter
@ApiModel("口味组查询条件")
public class RsTasteGroupFilter extends RsMasFilter {

  @ApiModelProperty("组织类型等于")
  private String orgTypeEq;
  @ApiModelProperty("组织id等于")
  private String orgIdEq;
  @ApiModelProperty(value = "代码等于")
  private String codeEq;
  @ApiModelProperty(value = "代码起始于")
  private String codeStartWith;
  @ApiModelProperty(value = "id不等于")
  private String idNotEq;
  @ApiModelProperty(value = "名称等于")
  private String nameEq;
  @ApiModelProperty(value = "名称类似于")
  private String nameLike;
  @ApiModelProperty(value = "代码/名称类似于...")
  private String keywordLike;

}
