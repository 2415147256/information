/**
 * 版权所有(C)，上海海鼎信息工程股份有限公司，2021，所有权利保留。
 * <p>
 * 项目名： workspace_mkh
 * 文件名： RSTip.java
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
import lombok.EqualsAndHashCode;

import java.util.ArrayList;
import java.util.List;

@Data
@ApiModel(description = "轮播图配置")
@EqualsAndHashCode(callSuper = true)
public class RSBannerOption extends RSOption {
	private static final long serialVersionUID = -7320107598637803928L;

	@ApiModelProperty(value = "内容明细", required = true)
	private List<RSBanner> details = new ArrayList<>();
}
