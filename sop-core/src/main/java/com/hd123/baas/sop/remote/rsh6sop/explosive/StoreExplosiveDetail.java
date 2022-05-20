/**
 * 版权所有(C)，上海海鼎信息工程股份有限公司，2021，所有权利保留。
 * <p>
 * 项目名： workspace_mkh
 * 文件名： PExplosiveDetail.java
 * 模块说明：
 * 修改历史：
 * 2021年01月13日 - huangjunxian - 创建。
 */
package com.hd123.baas.sop.remote.rsh6sop.explosive;

/**
 * @author huangjunxian
 * @since 1.0
 */

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

@Data
@ApiModel(description = "爆品活动明细")
public class StoreExplosiveDetail implements Serializable {
  public static final String TABLE_NAME = "storeExplosiveDtl";
  @NotNull
  @ApiModelProperty(value = "商品标识", required = true)
  private String gdUuid;
  @NotNull
  @ApiModelProperty(value = "规格", required = true)
  private BigDecimal qpc;
  @NotNull
  @ApiModelProperty(value = "预订数量", required = true)
  private BigDecimal qty;
  @NotNull
  @ApiModelProperty(value = "业务日期", required = true)
  private Date bizDate;
}
