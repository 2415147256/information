/**
 * 版权所有(C)，上海海鼎信息工程股份有限公司，2021，所有权利保留。
 * <p>
 * 项目名： sop-parent
 * 文件名： RsPlatShopCategoryBatchUpdate.java
 * 模块说明：
 * 修改历史：
 * 2021年02月25日 - XLT - 创建。
 */
package
  com.hd123.baas.sop.remote.rsmas.platshopcategory;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@ApiModel
public class RsPlatShopCategoryBatchUpdate extends RsPlatShopCategoryUpdate implements
  Serializable {
  private static final long serialVersionUID = -122L;

  @ApiModelProperty("组织类型")
  private String orgType;
  @ApiModelProperty("组织id")
  private String orgId;
  @ApiModelProperty("ID")
  private String id;
  @ApiModelProperty("平台id")
  private String platformId;
  @ApiModelProperty("门店id")
  private String shopId;
  @ApiModelProperty("类型")
  private String type;
}