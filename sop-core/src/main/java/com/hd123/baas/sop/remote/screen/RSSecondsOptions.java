/**
 * 版权所有(C)，上海海鼎信息工程股份有限公司，2021，所有权利保留。
 * <p>
 * 项目名： workspace_mkh
 * 文件名： RSSecondsOptions.java
 * 模块说明：
 * 修改历史：
 * 2021年06月04日 - xiongzhimin - 创建。
 */
package com.hd123.baas.sop.remote.screen;

/**
 * @author xiongzhimin
 * @since 1.0
 */

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.List;

@Data
@ApiModel(description = "间隔时间配置")
public class RSSecondsOptions implements Serializable {

	private static final long serialVersionUID = 1592664059355952213L;

	@ApiModelProperty(value = "所属组织id", required = true)
	private String orgUuid;
	@ApiModelProperty(value = "间隔时间配置列表", required = true)
	private List<RSSecondsOption> options;
}