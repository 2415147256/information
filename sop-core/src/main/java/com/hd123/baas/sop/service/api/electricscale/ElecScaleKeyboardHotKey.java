package com.hd123.baas.sop.service.api.electricscale;

import com.hd123.rumba.commons.biz.entity.Entity;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ElecScaleKeyboardHotKey extends Entity {
  private String tenant;
  private String owner;
  /**
   * 热键名
   */
  private String hotKey;
  /**
   * 热键参数 json
   */
  private String param;

}
