/**
 * 版权所有(C)，上海海鼎信息工程股份有限公司，2021，所有权利保留。
 * 
 * 项目名：	sop-service
 * 文件名：	RsPlatformCategorySku.java
 * 模块说明：	
 * 修改历史：
 * 2021年1月6日 - hezhenhui - 创建。
 */
package com.hd123.baas.sop.remote.rsmas.platformcategory;

import com.hd123.rumba.commons.biz.entity.Entity;

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
@ApiModel("平台类目商品")
public class RsPlatformCategorySku extends Entity {
  private static final long serialVersionUID = 2726945921948988198L;

  @ApiModelProperty(value = "租户")
  private String tenant;
  @ApiModelProperty("组织类型")
  private String orgType;
  @ApiModelProperty("组织id")
  private String orgId;
  @ApiModelProperty("平台id")
  private String platformId;
  @ApiModelProperty("平台类目类型")
  private String platformCategoryType;
  @ApiModelProperty("平台类目id")
  private String platformCategoryId;
  @ApiModelProperty("商品SKUID")
  private String skuId;
  @ApiModelProperty("排序值")
  private Integer sort;
}
