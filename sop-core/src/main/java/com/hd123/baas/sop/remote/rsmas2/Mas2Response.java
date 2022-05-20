/*
 * Copyright (c) 2021. Shanghai HEADING information engineering Co., Ltd. All rights reserved.
 */
package com.hd123.baas.sop.remote.rsmas2;

import lombok.Getter;
import lombok.Setter;

import java.util.Map;

/**
 * @author BinLee
 */
@Getter
@Setter
public class Mas2Response<T> {

  public static <T> Mas2Response<T> ok(T body) {
    Mas2Response<T> response = new Mas2Response<>();
    response.setData(body);
    return response;
  }

  public static Mas2Response<Void> ok() {
    return new Mas2Response<>();
  }

  public static Mas2Response<Void> fail(Integer code, String message) {
    Mas2Response<Void> response = new Mas2Response<>();
    response.setCode(code);
    response.setMsg(message);
    return response;
  }

  private int code = ResponseCode.SUCCESS;
  private String msg;
  private T data;
  private long total = 0L;
  private boolean more;
  private Map<String, String> fields;
  private String cmpId = "MAS2";
  private String errorLevel;

}