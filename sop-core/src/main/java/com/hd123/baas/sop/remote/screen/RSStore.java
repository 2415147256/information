/**
 * 版权所有(C)，上海海鼎信息工程股份有限公司，2020，所有权利保留。
 * <p>
 * 项目名：	mkh-screen-core
 * 文件名：	PStore.java
 * 模块说明：
 * 修改历史：
 * 2020年2月4日 - Leo - 创建。
 */
package com.hd123.baas.sop.remote.screen;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * 门店实体
 *
 * @author Leo
 */
@Data
@ApiModel(description = "门店")
public class RSStore implements Serializable {

	private static final long serialVersionUID = 6099549328585383584L;

	@ApiModelProperty(value = "门店标识", required = true)
	private String uuid;
	@ApiModelProperty(value = "门店代码", required = true)
	private String code;
	@ApiModelProperty(value = "门店名称", required = true)
	private String name;

}
