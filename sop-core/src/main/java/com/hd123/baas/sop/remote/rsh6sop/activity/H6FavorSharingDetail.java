/**
 * 版权所有(C)，上海海鼎信息工程股份有限公司，2021，所有权利保留。
 * <p>
 * 项目名： workspace_mkh
 * 文件名： PromActivity.java
 * 模块说明：
 * 修改历史：
 * 2021年01月29日 - huangjunxian - 创建。
 */
package com.hd123.baas.sop.remote.rsh6sop.activity;

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
import java.math.BigDecimal;

@Data
@ApiModel(description = "运营平台活动费用承担明细")
public class H6FavorSharingDetail implements Serializable {
	@NotBlank
	@Length(max = 38)
	@ApiModelProperty(value = "承担方代码", required = true)
	private String targetUuid;
	@NotBlank
	@Length(max = 38)
	@ApiModelProperty(value = "承担方代码", required = true)
	private String targetCode;
	@NotBlank
	@Length(max = 128)
	@ApiModelProperty(value = "承担方名称", required = true)
	private String targetName;
	@NotNull
	@ApiModelProperty(value = "承担比例,比如70%，则传70", example = "70", required = true)
	private BigDecimal rate;


}
