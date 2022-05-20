/**
 * 版权所有(C)，上海海鼎信息工程股份有限公司，2021，所有权利保留。
 * <p>
 * 项目名： workspace_mkh
 * 文件名： RSBanner.java
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

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Data
@ApiModel(description = "轮播图")
public class RSBanner implements Serializable {
	private static final long serialVersionUID = -1168781720430933106L;
	@NotBlank
	@ApiModelProperty(value = "图片名称", example = "1.jpg", required = true)
	private String fileName;
	@NotNull
	@ApiModelProperty(value = "图片路径", required = true)
	private String fileUrl;
}