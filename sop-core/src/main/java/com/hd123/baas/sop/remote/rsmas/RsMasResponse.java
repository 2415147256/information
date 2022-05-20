/**
 * 版权所有(C)，上海海鼎信息工程股份有限公司，2015，所有权利保留。
 * <p>
 * 项目名：	mas-commons-biz
 * 文件名：	RsMasResponse.java
 * 模块说明：
 * 修改历史：
 * <p>
 * 2019年8月20日 - lsz - 创建。
 */
package com.hd123.baas.sop.remote.rsmas;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * @author lsz
 */
@Getter
@Setter
public class RsMasResponse<T> implements Serializable{
  private static final long serialVersionUID = 4508866935374917730L;
  
    @ApiModelProperty(value = "响应吗")
    private int echoCode;
    @ApiModelProperty(value = "响应信息")
    private String echoMessage;
    @ApiModelProperty(value = "是否成功")
    public boolean success = true;
    @ApiModelProperty(value = "响应数据")
    private T data;

    public boolean isSuccess() {
        return echoCode == 0;
    }

    public static RsMasResponse ok() {
        return new RsMasResponse();
    }

    public RsMasResponse<T> ok(T data) {
      RsMasResponse<T> r = new RsMasResponse<T>();
        r.setData(data);
        return r;
    }

    /**
     * 静态方式创建
     *
     * @param data
     * @return
     */
    public static <T> RsMasResponse<T> success(T data) {
      RsMasResponse<T> r = new RsMasResponse<T>();
        r.setData(data);
        return r;
    }

    /**
     * 返回失败响应对象，echoCode设置为500
     *
     * @param echoMessage
     * @return
     */
    public static RsMasResponse fail(String echoMessage) {
        RsMasResponse r = new RsMasResponse();
        r.setSuccess(false);
        r.setEchoCode(500);
        r.setEchoMessage(echoMessage);
        return r;
    }

    public static RsMasResponse fail(int echoCode, String echoMessage) {
        if (echoCode == 0) {
            throw new IllegalArgumentException("echoCode不能为0");
        }
        RsMasResponse r = new RsMasResponse();
        r.setSuccess(false);
        r.setEchoCode(echoCode);
        r.setEchoMessage(echoMessage);
        return r;
    }

}
