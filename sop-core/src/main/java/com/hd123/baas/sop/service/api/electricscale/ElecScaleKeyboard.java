package com.hd123.baas.sop.service.api.electricscale;

import com.hd123.rumba.commons.biz.entity.Entity;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * @author youjiawei
 */
@Getter
@Setter
public class ElecScaleKeyboard extends Entity {

  private String tenant;
  /**
   * 热键集合
   */
  private List<ElecScaleKeyboardHotKey> hotKeys;

}
