package com.hd123.baas.sop.config;

import com.qianfan123.baas.config.api.annotation.BcGroup;
import com.qianfan123.baas.config.api.annotation.BcKey;

import lombok.Getter;
import lombok.Setter;

/**
 * @author zhengzewang on 2020/11/24.
 */
@Getter
@Setter
@BcGroup(name = "H6Task")
public class H6TaskConfig {

  @BcKey(name = "是否启用")
  private boolean enabled = false;

  @BcKey(name = "启用下发数据至H6")
  private boolean delivered = true;

}
