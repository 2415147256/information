/**
 * 版权所有(C)，上海海鼎信息工程股份有限公司，2021，所有权利保留。
 * <p>
 * 项目名： workspace_mkh
 * 文件名： RSCarouselInterval.java
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

import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Data
@ApiModel(description = "轮播间隔时间配置")
public class RSSecondsOption implements Serializable {
	private static final long serialVersionUID = -6582234645150698456L;

	@ApiModelProperty(value = "轮播类型： tip:广告语； banner：轮播图； price：价格表。", required = true)
	private String type;
	@NotNull
	@ApiModelProperty(value = "轮播间隔时间，单位秒", required = true)
	private Integer carouselSeconds;
}