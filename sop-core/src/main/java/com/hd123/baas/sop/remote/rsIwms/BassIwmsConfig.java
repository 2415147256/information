package com.hd123.baas.sop.remote.rsIwms;

import com.qianfan123.baas.config.api.annotation.BcGroup;
import com.qianfan123.baas.config.api.annotation.BcKey;

import lombok.Getter;
import lombok.Setter;

/**
 * @author W.J.H.7
 */
@Getter
@Setter
@BcGroup(name = "iWms")
public class BassIwmsConfig {
  @BcKey(name = "服务地址")
  private String serverUrl = "http://test.iwms.hd123.cn/test/iwms-openapi/api";
  @BcKey(name = "companyUuid")
  private String companyUuid = "ceb6436738194e2f82b2f6a7eec0f41a";
  @BcKey(name = "dcCode(配送中⼼代码)")
  private String dcCode = "P001";
}
