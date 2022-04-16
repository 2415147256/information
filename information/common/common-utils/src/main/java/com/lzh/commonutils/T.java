package com.lzh.commonutils;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.HashMap;
import java.util.Map;

/**
 * @author 卢正豪
 * @version .
 */
@Data
public class T {

    @ApiModelProperty(value = "是否成功")
    private Boolean success;
    @ApiModelProperty(value = "返回码")
    private Integer code;
    @ApiModelProperty(value = "返回消息")
    private String message;
    @ApiModelProperty(value = "返回数据")
    private Map<String, Object> data = new HashMap<String, Object>();

    private T() {
    }

    public static T ok() {
        T r = new T();
        r.setSuccess(true);
        r.setCode(ResultCode.SUCCESS);
        r.setMessage("成功");
        return r;
    }

    public static T error() {
        T r = new T();
        r.setSuccess(false);
        r.setCode(ResultCode.ERROR);
        r.setMessage("失败");
        return r;
    }

    public T success(Boolean success) {
        this.setSuccess(success);
        return this;
    }

    public T message(String message) {
        this.setMessage(message);
        return this;
    }

    public T code(Integer code) {
        this.setCode(code);
        return this;
    }

    public T data(String key, Object value) {
        this.data.put(key, value);
        return this;
    }

    public T data(Map<String, Object> map) {
        this.setData(map);
        return this;
    }
}
