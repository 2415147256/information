package com.hd123.baas.sop.remote.rsmas;

import lombok.Getter;
import lombok.Setter;

/**
 * @Author:huangyaoting
 * @Date:2020/1/6
 */

@Setter
@Getter
public class RsIdCodeName {
  private String id;
  private String code;
  private String name;

  public RsIdCodeName() {
    super();
  }

  public RsIdCodeName(String id, String code, String name) {
    this.id = id;
    this.code = code;
    this.name = name;
  }
}
