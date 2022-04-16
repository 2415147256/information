package com.lzh.servicebase.exception;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author 卢正豪
 * @version 1.0
 */
@Data
public class GuLiException extends RuntimeException {

    @ApiModelProperty(value = "状态码")
    private Integer code;

    private String msg;

    public GuLiException(int i, String msg) {
        this.code = i;
        this.msg = msg;
    }

    @Override
    public String toString() {
        
        return "GuliException{" +
                "message=" + this.getMessage() +
                ", code=" + code +
                '}';

    }
}
