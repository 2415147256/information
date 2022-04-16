package com.lzh.serviceuser.entity.frontVo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author 卢正豪
 * @version 1.0
 */
@Data
public class RegisterVo {
    @ApiModelProperty(value = "昵称")
    private String nickname;

    @ApiModelProperty(value = "手机号")
    private String telephone;

    @ApiModelProperty(value = "密码")
    private String password;

    @ApiModelProperty(value = "验证码")
    private String code;
}
