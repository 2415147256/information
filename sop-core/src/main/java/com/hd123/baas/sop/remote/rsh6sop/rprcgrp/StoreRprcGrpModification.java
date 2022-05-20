/**
 * 版权所有(C)，上海海鼎有限公司，2021，所有权利保留。
 * <p>
 * 项目名： h6-sop
 * 文件名： StoreRprcGrpModification.java
 * 模块说明：
 * 修改历史：
 * 2021年01月12日 - liuguilin - 创建。
 */
package com.hd123.baas.sop.remote.rsh6sop.rprcgrp;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;
import java.util.Date;

/**
 * @author huanghanyao
 */
@Data
@AllArgsConstructor
@ApiModel(description = "门店价格组修改对象")
public class StoreRprcGrpModification implements Serializable {
    private static final long serialVersionUID = -3085593324989640334L;

    @ApiModelProperty(value = "门店标识", example = "1000000", required = true)
    @NotBlank
    private String storeUuid;
    @ApiModelProperty(value = "价格组代码", example = "01", required = true)
    @NotBlank
    @Length(max = 10)
    private String grpCode;
    @ApiModelProperty(value = "修改时间", example = "2021-01-08 12:00:00", required = true)
    private Date modifyTime;
    @ApiModelProperty(value = "修改人代码", example = "zhangsan", required = true)
    private String modifierCode;
    @ApiModelProperty(value = "修改人名称", example = "张三", required = true)
    private String modifierName;
}
