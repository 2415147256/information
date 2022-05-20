package com.hd123.baas.sop.config;

import com.qianfan123.baas.config.api.annotation.BcGroup;
import com.qianfan123.baas.config.api.annotation.BcKey;
import lombok.Getter;
import lombok.Setter;

/**
 * @author W.J.H.7
 */
@Getter
@Setter
@BcGroup(name = "阿里云.语音")
public class BaasAliyunCallConfig {

  private static final String PREFIX = "aliyun.call.";
  public static final String ACCESS_KEY_ID_KEY = PREFIX + "accessKeyId";

  @BcKey(name = "accessKeyId")
  private String accessKeyId;
  @BcKey(name = "accessKeySecret")
  private String accessKeySecret;
  @BcKey(name = "calledShowNumber")
  private String calledShowNumber;

  @BcKey(name = "domain")
  private String domain = "dyvmsapi.aliyuncs.com";
  @BcKey(name = "regionId")
  private String regionId = "cn-hangzhou";
  @BcKey(name = "version")
  private String version = "2017-05-25";
  @BcKey(name = "prodId")
  private String prodId = "11000000300006";

}
