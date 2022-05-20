package com.hd123.baas.sop.evcall.exector.fcf;

import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.http.MethodType;
import com.aliyuncs.http.ProtocolType;
import com.aliyuncs.profile.DefaultProfile;
import com.aliyuncs.profile.IClientProfile;
import com.aliyuncs.push.model.v20160801.PushMessageToAndroidRequest;
import com.aliyuncs.push.model.v20160801.PushMessageToAndroidResponse;
import com.hd123.baas.config.core.BaasConfigClient;
import com.hd123.baas.sop.fcf.config.FcfConfig;
import com.hd123.baas.sop.utils.SpringUtils;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class AliYunPush {
  public final static String REGION_ID = "cn-hangzhou";
  public final static String TITLE = "alert";
  public final static String TARGET = "ACCOUNT";

  public final static BaasConfigClient configClient = SpringUtils.getBeansOfType(BaasConfigClient.class);

  public static void doPush(String tenant, String message, String storeCode) throws ClientException {
    try {
      FcfConfig config = configClient.getConfig(tenant, FcfConfig.class);
      if (config != null) {
        PushMessageToAndroidRequest androidRequest = new PushMessageToAndroidRequest(); // 安全性比较高的内容建议使用HTTPS
        androidRequest.setProtocol(ProtocolType.HTTPS); // 内容较大的请求，使用POST请求
        androidRequest.setMethod(MethodType.POST);
        androidRequest.setAppKey(Long.parseLong(config.getAppKey()));
        androidRequest.setTarget(TARGET);
        androidRequest.setTargetValue(tenant + "-" + storeCode);
        androidRequest.setTitle(TITLE);
        androidRequest.setBody(message);
        IClientProfile profile = DefaultProfile.getProfile(REGION_ID, config.getAccessKeyId(), config.getSecret());
        IAcsClient client = new DefaultAcsClient(profile);
        PushMessageToAndroidResponse pushMessageToAndroidResponse = client.getAcsResponse(androidRequest);
        log.info("doPush方法请求,参数:[tenant={},message={},storeCode={}]", tenant, message, storeCode);
        log.info("doPush方法返回,参数:[RequestId={},MessageId={}]", pushMessageToAndroidResponse.getRequestId(),
            pushMessageToAndroidResponse.getMessageId());
      }
    } catch (ClientException e) {
      log.error("doPush方法失败,参数:[tenant={},message={},storeCode={}]", tenant, message, storeCode);
      throw e;
    }
  }

}
