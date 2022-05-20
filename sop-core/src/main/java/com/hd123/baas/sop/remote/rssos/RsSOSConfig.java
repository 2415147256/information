package com.hd123.baas.sop.remote.rssos;

import com.qianfan123.baas.config.api.annotation.BcGroup;
import com.qianfan123.baas.config.api.annotation.BcKey;

import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;

/**
 * @author lilong on 2020/10/28.
 */
@Getter
@Setter
@BcGroup(name = "rs-sos")
public class RsSOSConfig {
  private static final String PREFIX = "rs-sos.";

  @BcKey(name = "用户名")
  private String username = "guest";
  @BcKey(name = "密码")
  private String password = "guest";

  public static void main(String[] args) {
    String target = " ";
    if (StringUtils.isNotBlank(target)) {
      System.out.println(1);
    }
    System.out.println(2);
  }

}
