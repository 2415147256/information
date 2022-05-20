/**
 * 版权所有(C)，上海海鼎有限公司，2021，所有权利保留。
 * <p>
 * 项目名： h6-sop
 * 文件名： RprcGrp.java
 * 模块说明：
 * 修改历史：
 * 2021年01月06日 - liuguilin - 创建。
 */
package com.hd123.baas.sop.remote.rsh6sop.rprcgrp;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * @author huanghanyao
 */
@Data
@ApiModel(description = "价格组")
public class RprcGrp implements Serializable {
    private static final long serialVersionUID = 3854952998283689945L;

    @NotNull
    @ApiModelProperty(value = "代码", required = true)
    private String code;
    @NotNull
    @ApiModelProperty(value = "名称", required = true)
    private String name;
    @NotNull
    @ApiModelProperty(value = "所属组织标识", example = "1000000", required = true)
    private Integer orgGid;
}
