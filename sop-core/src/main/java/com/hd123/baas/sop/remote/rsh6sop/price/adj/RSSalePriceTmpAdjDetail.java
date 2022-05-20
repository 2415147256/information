/**
 * 版权所有(C)，上海海鼎信息工程股份有限公司，2020，所有权利保留。
 * <p>
 * 项目名： workspace_mkh
 * 文件名： GoodsSalePriceAdjDetail.java
 * 模块说明：
 * 修改历史：
 * 2020年11月21日 - huangjunxian - 创建。
 */
package com.hd123.baas.sop.remote.rsh6sop.price.adj;

/**
 * @author huangjunxian
 * @since 1.0
 */

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.math.BigDecimal;

@Data
@ApiModel(description = "售价连调价单明细")
public class RSSalePriceTmpAdjDetail implements Serializable {
	@NotBlank
	@ApiModelProperty(value = "门店标识，对应EPR的gid", example = "1000000", required = true)
	private String storeId;
	@NotBlank
	@ApiModelProperty(value = "商品ID", example = "3000000", required = true)
	private String skuId;
	@NotNull
	@ApiModelProperty(value = "调整后的价格", example = "100.0000", required = true)
	private BigDecimal price;
}