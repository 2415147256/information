package com.hd123.baas.sop.remote.rsias.inv;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * @author lins
 */
@Getter
@Setter
public class RsIasResponse<T> {


    @ApiModelProperty(value = "响应吗")
    private int code;
    @ApiModelProperty(value = "响应信息")
    private String msg;
    @ApiModelProperty(value = "是否成功")
    public boolean success = true;
    @ApiModelProperty(value = "响应数据")
    private T data;

}
