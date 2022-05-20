package com.hd123.baas.sop.remote.bigdata;

import lombok.Getter;
import lombok.Setter;

/**
 * @author shenmin
 */
@Getter
@Setter
public class CommonResult<T> {
  //响应码
  private String code;
  //结果信息
  private String message;
  //请求成功与否
  private boolean success;
  //数据
  private T data;
}
