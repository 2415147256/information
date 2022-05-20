/**
 * 版权所有(C)，上海海鼎信息工程股份有限公司，2021，所有权利保留。
 * 
 * 项目名：	sop-service
 * 文件名：	RsPlatformCategorySkuFilter.java
 * 模块说明：	
 * 修改历史：
 * 2021年1月6日 - hezhenhui - 创建。
 */
package com.hd123.baas.sop.remote.rsmas.platformcategory;

import java.util.List;

import com.hd123.baas.sop.remote.rsmas.RsFilter;

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
@ApiModel("平台类目商品查询条件")
public class RsPlatformCategorySkuFilter extends RsFilter {
  private static final long serialVersionUID = -2286151087375919054L;

  @ApiModelProperty("组织类型等于")
  private String orgTypeEq;
  @ApiModelProperty("组织id等于")
  private String orgIdEq;
  @ApiModelProperty("平台id等于")
  private String platformIdEq;
  @ApiModelProperty("平台id在之中")
  private List<String> platformIdIn;
  @ApiModelProperty("平台类目类型等于")
  private String platformCategoryTypeEq;
  @ApiModelProperty("平台类目类型在之中")
  private List<String> platformCategoryTypeIn;
  @ApiModelProperty("平台类目id等于")
  private String platformCategoryIdEq;
  @ApiModelProperty("平台类目id在之中")
  private List<String> platformCategoryIdIn;
  @ApiModelProperty("商品SKUID在之中")
  private List<String> skuIdIn;
  @ApiModelProperty("商品SKUID类似于")
  private String skuIdLike;
}
