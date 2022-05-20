package com.hd123.baas.sop.service.api.basedata.shopskuinvcrule;

import lombok.Getter;
import lombok.Setter;

/**
 * @Author:huangyaoting
 * @Date:2020/1/6
 */

@Setter
@Getter
public class RsIdName {
  private String id;
  private String name;

  public RsIdName() {
    super();
  }

  public RsIdName(String id, String name) {
    this.id = id;
    this.name = name;
  }
}
