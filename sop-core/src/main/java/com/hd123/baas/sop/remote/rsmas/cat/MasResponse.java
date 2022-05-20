package com.hd123.baas.sop.remote.rsmas.cat;

import java.io.Serializable;

import io.swagger.annotations.ApiModelProperty;

/**
 * @Author maodapeng
 * @Since
 */
public class MasResponse<T> implements Serializable {
  private static final long serialVersionUID = 4508866935374917730L;
  @ApiModelProperty("响应吗")
  private int echoCode;
  @ApiModelProperty("响应信息")
  private String echoMessage;
  @ApiModelProperty("是否成功")
  public boolean success = true;
  @ApiModelProperty("响应数据")
  private T data;

  public MasResponse() {
  }

  public boolean isSuccess() {
    return this.echoCode == 0;
  }

  public static MasResponse ok() {
    return new MasResponse();
  }

  public MasResponse<T> ok(T data) {
    MasResponse<T> r = new MasResponse();
    r.setData(data);
    return r;
  }

  public static <T> MasResponse<T> success(T data) {
    MasResponse<T> r = new MasResponse();
    r.setData(data);
    return r;
  }

  public static MasResponse fail(String echoMessage) {
    MasResponse r = new MasResponse();
    r.setSuccess(false);
    r.setEchoCode(500);
    r.setEchoMessage(echoMessage);
    return r;
  }

  public static MasResponse fail(int echoCode, String echoMessage) {
    if (echoCode == 0) {
      throw new IllegalArgumentException("echoCode不能为0");
    } else {
      MasResponse r = new MasResponse();
      r.setSuccess(false);
      r.setEchoCode(echoCode);
      r.setEchoMessage(echoMessage);
      return r;
    }
  }

  public int getEchoCode() {
    return this.echoCode;
  }

  public String getEchoMessage() {
    return this.echoMessage;
  }

  public T getData() {
    return this.data;
  }

  public void setEchoCode(int echoCode) {
    this.echoCode = echoCode;
  }

  public void setEchoMessage(String echoMessage) {
    this.echoMessage = echoMessage;
  }

  public void setSuccess(boolean success) {
    this.success = success;
  }

  public void setData(T data) {
    this.data = data;
  }
}