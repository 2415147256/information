/**
 * 版权所有(C)，上海海鼎信息工程股份有限公司，2020，所有权利保留。
 * <p>
 * 项目名： workspace_mkh
 * 文件名： SOPPromotionBIllJoin.java
 * 模块说明：
 * 修改历史：
 * 2020年11月30日 - huangjunxian - 创建。
 */
package com.hd123.baas.sop.remote.rsh6sop.prom;

/**
 * @author huangjunxian
 * @since 1.0
 */

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

@Data
@ApiModel(description = "促销单参加门店")
public class RSPromotionBillJoin implements Serializable {
	@NotBlank
	@Length(max = 38)
	@ApiModelProperty(value = "参加单位标识", required = true)
	private String joinOrgUuid;
	@NotBlank
	@Length(max = 38)
	@ApiModelProperty(value = "参加单位代码", required = true)
	private String joinOrgCode;
	@NotBlank
	@Length(max = 100)
	@ApiModelProperty(value = "参加单位名称", required = true)
	private String joinOrgName;
}