/**
 * 版权所有(C)，上海海鼎信息工程股份有限公司，2019，所有权利保留。
 * <p>
 * 项目名：	mas-company-api 文件名：	ShopFilter.java 模块说明： 修改历史： 2019年9月4日 - sulin - 创建。
 */
package com.hd123.baas.sop.remote.rsmas.store;

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
@ApiModel("门店查询条件")
public class RsStoreFilter extends RsMasFilter {

  private static final long serialVersionUID = 1L;

  @ApiModelProperty(value = "组织类型等于...")
  private String orgTypeEq;
  @ApiModelProperty(value = "组织ID等于")
  private String orgIdEq;
  @ApiModelProperty(value = "所属组织类型在...范围内")
  private List<String> orgTypeIn;
  @ApiModelProperty(value = "所属组织ID在...范围内")
  private List<String> orgIdIn;

  @ApiModelProperty(value = "商圈等于")
  private String businessCircleEq;
  @ApiModelProperty(value = "对接平台等于")
  private String platformEq;
  @ApiModelProperty(value = "名称、代码类似于")
  private String keyword;
  @ApiModelProperty(value = "代码等于")
  private String codeEq;
  @ApiModelProperty(value = "代码类似于")
  private String codeLike;
  @ApiModelProperty(value = "代码在...范围内")
  private List<String> codeIn;
  @ApiModelProperty(value = "代码不在...范围内")
  private List<String> codeNotIn;
  @ApiModelProperty(value = "名称类似于")
  private String nameLike;
  @ApiModelProperty(value = "地址类似于")
  private String addressLike;
  @ApiModelProperty(value = "门店类型等于")
  private String typeEq;
  @ApiModelProperty(value = "门店类型在...范围内")
  private List<String> typeIn;
  @ApiModelProperty(value = "城市等于")
  private String cityEq;
  @ApiModelProperty(value = "城市在...范围内")
  private List<String> cityIn;
  @ApiModelProperty(value = "门店等级等于")
  private String levelEq;
  @ApiModelProperty(value = "uuid在...范围内")
  private List<String> uuidIn;
  @ApiModelProperty(value = "启禁用状态")
  private Boolean enabledEq;
  @ApiModelProperty("业态等于")
  private String shopBusinessTypeEq;
  @ApiModelProperty(value = "区域代码等于")
  private String areaCodeEq;

  @ApiModelProperty("营业状态等于")
  private String businessStateEq;
  @ApiModelProperty("营业状态在...之中")
  private List<String> businessStateIn;

}
