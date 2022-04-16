package com.lzh.serviceuser.entity.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * @author 卢正豪
 * @version 1.0
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="User对象", description="")
public class LoginVo {

    @ApiModelProperty(value = "电话")
    private String telephone;

    @ApiModelProperty(value = "密码")
    private String password;

}
