package com.hd123.baas.sop.configuration.oss;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

@Configuration
@ConditionalOnProperty(name = "sop-service.oss", havingValue = "huawei")
@ImportResource("classpath:META-INF/oss/rumba-oss-huawei.xml")
public class OssHuawei {
  private static final int EXPIRE_TIME = 43200; // 12*60*60s
  // ISO 8601 format
  private static final String ISO8601_DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";

  @Value("${rumba-oss-huawei.bucketName}")
  private String bucketName;
  @Value("${rumba-oss-huawei.connection.region}")
  private String region;
  @Value("${rumba-oss-huawei.connection.accessKeyId}")
  private String accessKeyId;
  @Value("${rumba-oss-huawei.connection.secretAccessKey}")
  private String secretAccessKey;

  /** OSS服务地址 */
  private static final String HOST_TEMPLATE = "https://%s.obs.%s.myhuaweicloud.com";

  @Bean
  public OssSignature signature() {
    return path -> {
      Map<String, Object> params = new HashMap<>();
      params.put("type", "huawei");
      params.put("host", String.format(HOST_TEMPLATE, bucketName, region));
      params.put("key", path);
      params.put("accessKeyId", accessKeyId);
      String policy = buildPolicy(bucketName, path);
      params.put("policy", policy);
      params.put("signature", hmacSha1(secretAccessKey, policy));
      params.put("aclRule", "public-read");
      return params;
    };
  }

  public String hmacSha1(String secretKey, String input) throws NoSuchAlgorithmException, InvalidKeyException {
    SecretKeySpec signingKey = new SecretKeySpec(secretKey.getBytes(StandardCharsets.UTF_8), "HmacSHA1");
    Mac mac = Mac.getInstance("HmacSHA1");
    mac.init(signingKey);
    return Base64.getEncoder().encodeToString(mac.doFinal(input.getBytes(StandardCharsets.UTF_8)));
  }

  private String buildPolicy(String bucket, String path) {
    JSONObject policyRaw = new JSONObject();
    long expireEndTime = System.currentTimeMillis() + EXPIRE_TIME * 1000;
    Date expiration = new Date(expireEndTime);
    policyRaw.put("expiration", getISO8601Timestamp(expiration));
    JSONArray conditions = new JSONArray();
    JSONObject bucketName = new JSONObject();
    bucketName.put("bucket", bucket);
    conditions.put(bucketName);
    List<String> startWith = new ArrayList<>();
    startWith.add("starts-with");
    startWith.add("$key");
    startWith.add(path);
    conditions.put(startWith);
    JSONObject obsAcl = new JSONObject();
    obsAcl.put("x-obs-acl", "public-read");
    conditions.put(obsAcl);
    policyRaw.put("conditions", conditions);
    return Base64.getEncoder().encodeToString(policyRaw.toString().getBytes(StandardCharsets.UTF_8));
  }

  /**
   * 传入Data类型日期，返回字符串类型时间（ISO8601标准时间）
   */
  public static String getISO8601Timestamp(Date date) {
    TimeZone tz = TimeZone.getTimeZone("Asia/Shanghai");
    DateFormat df = new SimpleDateFormat(ISO8601_DATE_FORMAT);
    df.setTimeZone(tz);
    return df.format(date);
  }
}
