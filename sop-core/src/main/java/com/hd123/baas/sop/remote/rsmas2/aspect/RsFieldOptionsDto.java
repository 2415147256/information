package com.hd123.baas.sop.remote.rsmas2.aspect;

import lombok.Getter;
import lombok.Setter;

/**
 * @author zhangweigang
 */
@Getter
@Setter
public class RsFieldOptionsDto {
  private String operator;
  private String aspectName;
  private String fieldName;
  private Object options;
}
