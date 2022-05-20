package com.hd123.baas.sop.remote.rsmas.cat;

import io.swagger.annotations.ApiModelProperty;

/**
 * @Author maodapeng
 * @Since
 */
public class MasRequest<T> {
  @ApiModelProperty("请求数据")
  private T data;

  public T getData() {
    return this.data;
  }

  public void setData(T data) {
    this.data = data;
  }

  public MasRequest(T data) {
    this.data = data;
  }

  public MasRequest() {
  }
}