/*
 * Copyright (c) 2021. Shanghai HEADING information engineering Co., Ltd. All rights reserved.
 */
package com.hd123.baas.sop.remote.rsmas2;

import lombok.Getter;

import java.text.MessageFormat;

/**
 * @author BinLee
 */
@Getter
public class MasException extends Exception {

  private final Integer code;
  private final String message;
  private final Object data;

  private static final long serialVersionUID = -8944773507418316279L;

  public MasException(Integer code, String message) {
    this(code, message, null);
  }

  public MasException(Integer code, String message, Object data) {
    this.code = code;
    this.message = message;
    this.data = data;
  }

  public MasException(String pattern, Object... arguments) {
    this(ResponseCode.UNKNOWN, MessageFormat.format(pattern, arguments), null);
  }

}