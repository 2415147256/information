package com.hd123.baas.sop.remote.dingtalk;

import java.util.Arrays;
import java.util.List;

import com.qianfan123.baas.common.util.JSONUtil;
import org.apache.poi.util.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import com.alibaba.fastjson.JSONObject;
import com.hd123.baas.config.core.BaasConfigClient;
import com.hd123.baas.sop.config.DingTalkConfig;
import com.hd123.rumba.commons.lang.Assert;
import com.qianfan123.baas.common.BaasException;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class DingTalkClient {
  @Autowired
  private BaasConfigClient client;
  @Autowired
  private RestTemplate restTemplate;

  public String getUserId(String tenant, String mobile) throws BaasException {
    Assert.notNull(tenant, "tenant");
    Assert.notNull(mobile, "mobile");
    String accessToken = getAccessToken(tenant);
    return getUserId(tenant, mobile, accessToken);
  }

  /**
   * 获取token
   *
   */
  private String getAccessToken(String tenant) throws BaasException {
    Assert.notNull(tenant, "tenant");
    DingTalkConfig config = client.getConfig(tenant, DingTalkConfig.class);
    StringBuffer url = new StringBuffer();
    url.append(config.getAcquireTokenUrl())
        .append("?appkey=")
        .append(config.getAppKey())
        .append("&appsecret=")
        .append(config.getAppSecret());
    try {
      ResponseEntity responseEntity = restTemplate.exchange(url.toString(), HttpMethod.GET, null, String.class);
      String body = (String) responseEntity.getBody();
      log.info("获取token的结果是: {}", body);
      AcquireTokenResponse tokenVO = JSONObject.parseObject(body, AcquireTokenResponse.class);
      if (tokenVO == null) {
        log.error("获取钉钉token失败，url:{}，appKey={}", config.getAcquireTokenUrl(), config.getAppKey());
        throw new BaasException("获取钉钉token失败");
      }
      return tokenVO.getAccess_token();
    } catch (Exception e) {
      log.error("获取钉钉token异常，url:{}，appKey={}，appSecret={}", config.getAcquireTokenUrl(), config.getAppKey(),
          config.getAppSecret(), e);
      throw new BaasException("获取钉钉token异常");
    }
  }

  /**
   * 获取用户id
   *
   */
  private String getUserId(String tenant, String mobile, String accessToken) throws BaasException {
    Assert.notNull(tenant, "tenant");
    Assert.notNull(mobile, "mobile");
    Assert.notNull(accessToken, "accessToken");
    DingTalkConfig config = client.getConfig(tenant, DingTalkConfig.class);
    StringBuffer url = new StringBuffer();
    url.append(config.getAcquireUserIdByMobileUrl()).append("?access_token=").append(accessToken);
    try {
      HttpHeaders headers = new HttpHeaders();
      headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
      LinkedMultiValueMap<String, Object> params = new LinkedMultiValueMap<>();
      params.add("mobile", mobile);
      HttpEntity<MultiValueMap<String, Object>> httpEntity = new HttpEntity<>(params, headers);
      ResponseEntity responseEntity = restTemplate.exchange(url.toString(), HttpMethod.POST, httpEntity, String.class);
      String body = (String) responseEntity.getBody();
      log.info("获取用户信息的结果是: {}", body);
      OapiV2UserGetbymobileResponse response = JSONObject.parseObject(body, OapiV2UserGetbymobileResponse.class);
      if (response == null) {
        throw new BaasException("获取钉钉用户UserId失败");
      }
      if (response.getErrcode() != 0) {
        throw new BaasException("获取钉钉用户userId失败,errMsg:{}", response.getErrmsg());
      }
      if (response.getResult() == null || response.getResult().getUserid() == null) {
        throw new BaasException("获取钉钉用户UserId未空");
      }
      return response.getResult().getUserid();
    } catch (Exception e) {
      log.error("获取钉钉用户信息异常，url:{}，access_token={}，mobile={},exception={}", config.getAcquireUserIdByMobileUrl(),
          accessToken, mobile, e);
      throw new BaasException("获取钉钉用户信息异常,exception={}", e.getMessage());
    }
  }

  public void sendMessage(String tenant, String mobile, DingTalkLinkMsg linkMsg) throws BaasException {
    String userId = getUserId(tenant, mobile);
    sendMessage(tenant, Arrays.asList(userId), linkMsg);
  }

  /**
   * 发送消息
   */
  public void sendMessage(String tenant, List<String> userIds, DingTalkLinkMsg linkMsg) throws BaasException {
    String accessToken = getAccessToken(tenant);
    sendMessage(tenant, userIds, linkMsg, accessToken);
  }

  /**
   * 发送消息
   */
  private void sendMessage(String tenant, List<String> userIds, Object msg, String accessToken) throws BaasException {
    Assert.notNull(tenant, "tenant");
    Assert.notNull(userIds, "userIds");
    Assert.notNull(msg, "msg");
    DingTalkConfig config = client.getConfig(tenant, DingTalkConfig.class);
    StringBuffer url = new StringBuffer();
    url.append(config.getSendMessageUrl()).append("?access_token=").append(accessToken);
    try {
      HttpHeaders headers = new HttpHeaders();
      headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
      LinkedMultiValueMap<String, Object> params = new LinkedMultiValueMap<>();
      params.add("agent_id", config.getAgentId());
      params.add("userid_list", StringUtil.join(userIds.toArray(), ","));
      params.add("msg", JSONObject.toJSON(msg));
      HttpEntity<MultiValueMap<String, Object>> httpEntity = new HttpEntity<>(params, headers);
      log.info("钉钉消息发送内容：{}", JSONUtil.safeToJson(httpEntity));
      ResponseEntity responseEntity = restTemplate.exchange(url.toString(), HttpMethod.POST, httpEntity, String.class);
      String body = (String) responseEntity.getBody();
      log.info("发送钉钉消息的结果是: {}", body);
      DingTalkMessageResponse response = JSONObject.parseObject(body, DingTalkMessageResponse.class);
      if (response == null) {
        throw new BaasException("发送消息失败");
      }
      if (response.getErrcode() != 0) {
        throw new BaasException("errMsg:{}", response.getErrmsg());
      }
    } catch (Exception e) {
      log.error("发送钉钉消息异常，url:{}，access_token={}，exception={}", config.getSendMessageUrl(), accessToken, e);
      throw new BaasException("获取钉钉用户信息异常");
    }
  }

}
