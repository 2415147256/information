/**
 * 版权所有(C)，上海海鼎信息工程股份有限公司，2019，所有权利保留。
 * 
 * 项目名：	mas-company-api
 * 文件名：	ShopFilter.java
 * 模块说明：	
 * 修改历史：
 * 2019年9月4日 - sulin - 创建。
 */
package com.hd123.baas.sop.remote.rsmas.pos;

import com.hd123.baas.sop.remote.rsmas.RsMasFilter;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * @author sulin
 *
 */
@Getter
@Setter
@ApiModel("POS收银机查询条件")
public class RsPosFilter extends RsMasFilter {

  private static final long serialVersionUID = 1L;


  @ApiModelProperty(value = "所属组织类型等于")
  private String orgTypeEq;
  @ApiModelProperty(value = "所属组织ID等于")
  private String orgIdEq;
  @ApiModelProperty(value = "所属组织类型在...范围内")
  private List<String> orgTypeIn;
  @ApiModelProperty(value = "所属组织ID在...范围内")
  private List<String> orgIdIn;

  @ApiModelProperty(value = "名称、代码类似于")
  private String keyword;
  @ApiModelProperty(value = "代码等于")
  private String codeEq;
  @ApiModelProperty(value = "代码类似于")
  private String codeLike;
  @ApiModelProperty(value = "代码在...范围内")
  private List<String> codeIn;
  @ApiModelProperty(value = "名称类似于")
  private String nameLike;
  @ApiModelProperty(value = "id类似于")
  private String idLike;
  @ApiModelProperty(value = "门店等于")
  private String storeIdEq;
  @ApiModelProperty(value = "门店ID在...范围内")
  private List<String> storeIdIn;
  @ApiModelProperty(value = "起禁用状态等于")
  private Boolean enabledEq;
  @ApiModelProperty(value = "出品部门ID在...范围内")
  private List<String> stallIdIn;
  @ApiModelProperty(value = "出品部门ID等于")
  private String stallIdEq;

  @ApiModelProperty(value = "是否默认等于")
  private Boolean isDefaultEq;
}
