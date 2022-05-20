/**
 * 版权所有(C)，上海海鼎信息工程股份有限公司，2020，所有权利保留。
 * <p>
 * 项目名： workspace_mkh
 * 文件名： GoodsOrderQty.java
 * 模块说明：
 * 修改历史：
 * 2020年11月14日 - huangjunxian - 创建。
 */
package com.hd123.baas.sop.remote.rsh6sop.price;

/**
 * @author huangjunxian
 * @since 1.0
 */

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
@Accessors(chain = true)
@ApiModel(description = "商品订货数")
public class RSGoodsOrderQty implements Serializable {
    private static final long serialVersionUID = -6078283602247785448L;

    @ApiModelProperty(value = "商品标识", example = "03b1617053db4619b693842debfb32c5", required = true)
    private String gdUuid;
    @ApiModelProperty(value = "订货总数", example = "1000.0000", required = true)
    private BigDecimal qty;
}
