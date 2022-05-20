package com.hd123.baas.sop.service.api.offset;

import lombok.Getter;
import lombok.Setter;

/**
 * @author zhengzewang on 2020/11/17.
 */
@Getter
@Setter
public class Offset {

  public static final String DEF = "def";

  private String tenant;
  private String spec = DEF;
  private Long seq;
  private OffsetType type;

}
