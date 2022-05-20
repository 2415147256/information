/**
 * 版权所有(C)，上海海鼎信息工程股份有限公司，2021，所有权利保留。
 * <p>
 * 项目名： workspace_mkh
 * 文件名： H6GoodsQueryFilter.java
 * 模块说明：
 * 修改历史：
 * 2021年02月01日 - huangjunxian - 创建。
 */
package com.hd123.baas.sop.remote.rsh6sop.goods;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.util.ArrayList;
import java.util.List;

/**
 * @author huangjunxian
 * @since 1.0
 */
@Data
@ApiModel(description = "商品查询过滤器")
public class H6GoodsQueryFilter {
  @NotBlank
  @ApiModelProperty(value = "门店标识", required = true)
  private String storeUuid;
  @ApiModelProperty(value = "商品标识在...中,最大不超过200个", required = true)
  private List<String> idIn = new ArrayList<>();
}
