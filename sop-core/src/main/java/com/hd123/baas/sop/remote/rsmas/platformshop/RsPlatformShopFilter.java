/**
 * 版权所有(C)，上海海鼎信息工程股份有限公司，2021，所有权利保留。
 * 
 * 项目名：	sop-service
 * 文件名：	RsPlatformShopFilter.java
 * 模块说明：	
 * 修改历史：
 * 2021年1月7日 - hezhenhui - 创建。
 */
package com.hd123.baas.sop.remote.rsmas.platformshop;

import com.hd123.baas.sop.remote.rsmas.RsMasFilter;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * @author hezhenhui
 *
 */
@Getter
@Setter
@ApiModel("平台门店查询条件")
public class RsPlatformShopFilter extends RsMasFilter {
  private static final long serialVersionUID = -1193173296954854341L;


  @ApiModelProperty(value = "组织Id等于")
  private String orgIdEq;
  @ApiModelProperty(value = "组织类型等于")
  private String orgTypeEq;
  @ApiModelProperty("平台等于")
  private String platformIdEq;
  @ApiModelProperty("平台门店代码等于")
  private String codeEq;
  @ApiModelProperty("平台门店代码起始于")
  private String codeStartWith;
  @ApiModelProperty("平台门店代码在....范围")
  private List<String> codeIdIn;
  @ApiModelProperty("平台门店状态等于")
  private RsPlatformShopState stateEq;
  @ApiModelProperty("平台门店代码,平台 门店名称类似于")
  private String keywordLike;
  @ApiModelProperty("平台门店代码, 门店代码, 门店名称类似于")
  private String keyLike;
  @ApiModelProperty("平台商家代码起始于")
  private String merchantCodeStartWith;
  @ApiModelProperty("平台商家代码在什么范围内")
  private List<String> merchantCodeIn;
  @ApiModelProperty("门店代码起始于")
  private String shopCodeStartWith;
  @ApiModelProperty("门店代码在..范围")
  private List<String> shopCodeIn;
  @ApiModelProperty("门店名称类似于")
  private String shopNameLike;
  @ApiModelProperty("门店Id等于")
  private String shopIdEq;
  @ApiModelProperty("门店Id不在..范围")
  private List<String> shopIdNotIn;
  @ApiModelProperty("门店Id类似于")
  private String shopIdLike;
  @ApiModelProperty("门店Id在..范围")
  private List<String> shopIdIn;
  @ApiModelProperty("是否有关联门店")
  private Boolean hasAssociateShop;
  @ApiModelProperty("所属城市等于")
  private String cityIdEq;
  @ApiModelProperty("门店类型等于")
  private String shopTypeEq;
  @ApiModelProperty("营业状态等于")
  private String businessStateEq;
  @ApiModelProperty("营业状态在...之中")
  private List<String> businessStateIn;
}
