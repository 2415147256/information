/**
 * 版权所有(C)，上海海鼎信息工程股份有限公司，2020，所有权利保留。
 * <p>
 * 项目名： workspace_mkh
 * 文件名： GoodsSalePriceAdj.java
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
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Data
@ApiModel(description = "售价连调价单创建对象")
public class RSSalePriceTmpAdjCreation implements Serializable {

	@NotBlank
	@Length(max = 32)
	@ApiModelProperty(value = "调价单号", example = "9999202011210001", required = true)
	private String billNumber;
	@NotNull
	@ApiModelProperty(value = "生效日期", example = "2020-11-21", required = true)
	private Date effectDate;
	@NotNull
	@ApiModelProperty(value = "单据最后修改时间，用于区分同一天内的单据加工顺序", example = "2020-11-21 09:00:00", required = true)
	private Date lstUpdTime;

	@ApiModelProperty(value = "调整明细", required = true)
	private List<RSSalePriceTmpAdjDetail> details = new ArrayList<>();
}