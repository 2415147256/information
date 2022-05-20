/**
 * 版权所有(C)，上海海鼎信息工程股份有限公司，2015，所有权利保留。
 *
 * 项目名：	mas-cms-api
 * 文件名：	PlatformCategorySku.java
  * 模块说明：
 * 修改历史：

 * 2019年9月19日 - lsz - 创建。
 */
package com.hd123.baas.sop.service.api.basedata.platshopcategory;

import com.hd123.rumba.commons.biz.entity.Entity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

/**
 *
 * @author lsz
 */
@Getter
@Setter
@ApiModel("平台类目商品")
public class PlatShopCategorySku extends Entity {
  private static final long serialVersionUID = -3274993251215504709L;

  /** 租户 */
  @ApiModelProperty(value = "租户")
  private String tenant;

  @ApiModelProperty("门店id")
  private String shopId;
  @ApiModelProperty("平台门店类目ID")
  private String platShopCategoryId;
  @ApiModelProperty("商品Id")
  private String skuId;
  @ApiModelProperty("排序值")
  private Integer sort;

}
