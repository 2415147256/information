/**
 * 版权所有(C)，上海海鼎信息工程股份有限公司，2021，所有权利保留。
 * <p>
 * 项目名： workspace_mkh
 * 文件名： RSGoods.java
 * 模块说明：
 * 修改历史：
 * 2021年06月04日 - huangjunxian - 创建。
 */
package com.hd123.baas.sop.remote.screen;

/**
 * @author huangjunxian
 * @since 1.0
 */

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
@ApiModel(description = "商品信息")
public class RSGoods implements Serializable {
	private static final long serialVersionUID = 474173373322800975L;
	@ApiModelProperty(value = "数据标识", required = true)
	private String uuid;
	@ApiModelProperty(value = "门店", required = true)
	private RSStore store;
	@ApiModelProperty(value = "类别代码", required = true)
	private String sortCode;
	@ApiModelProperty(value = "类别名称", required = true)
	private String sortName;
	@ApiModelProperty(value = "商品标识", required = true)
	private String gdUuid;
	@ApiModelProperty(value = "商品代码", required = true)
	private String gdCode;
	@ApiModelProperty(value = " 商品名称", required = true)
	private String gdName;
	@ApiModelProperty(value = "规格", required = true)
	private BigDecimal qpc;
	@ApiModelProperty(value = "包装规格", required = true)
	private String qpcStr;
	@ApiModelProperty(value = "单位", required = true)
	private String munit;
	@ApiModelProperty(value = "售价", required = true)
	private BigDecimal rtlPrc;
	@ApiModelProperty(value = "标准单位", required = false)
	private String srcMunit;
	@ApiModelProperty(value = "标准售价", required = false)
	private BigDecimal srcRtlPrc;
	@ApiModelProperty(value = "会员价", required = false)
	private BigDecimal mbrPrc;
	@ApiModelProperty(value = "标准会员价", required = false)
	private BigDecimal srcMbrPrc;
}
