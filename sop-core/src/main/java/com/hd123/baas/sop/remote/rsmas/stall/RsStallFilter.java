/**
 * 版权所有(C)，上海海鼎信息工程股份有限公司，2019，所有权利保留。
 * 
 * 项目名：	mas-company-api
 * 文件名：	ShopFilter.java
 * 模块说明：	
 * 修改历史：
 * 2019年9月4日 - sulin - 创建。
 */
package com.hd123.baas.sop.remote.rsmas.stall;

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
@ApiModel("出品部门查询条件")
public class RsStallFilter extends RsMasFilter {

  private static final long serialVersionUID = 1L;

  @ApiModelProperty(value = "所属组织类型等于")
  private String orgTypeEq;
  @ApiModelProperty(value = "所属组织ID等于")
  private String orgIdEq;
  @ApiModelProperty(value = "名称、代码类似于")
  private String keywordLike;
  @ApiModelProperty(value = "id等于")
  private String idEq;
  @ApiModelProperty(value = "id在...范围内")
  private List<String> idIn;
  @ApiModelProperty(value = "代码类似于")
  private String codeLike;
  @ApiModelProperty(value = "代码等于")
  private String codeEq;
  @ApiModelProperty(value = "名称类似于")
  private String nameLike;
  @ApiModelProperty(value = "起禁用状态等于")
  private Boolean enabledEq;
  @ApiModelProperty(value = "skuId在之中")
  private List<String> skuIdIn;
  @ApiModelProperty(value = "门店id等于")
  private String storeIdEq;
  @ApiModelProperty(value = "门店id在...范围")
  private List<String> storeIdIn;

}
