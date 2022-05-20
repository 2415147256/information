package com.hd123.baas.sop.configuration.oss;

import com.hd123.rumba.oss.api.Bucket;
import com.hd123.rumba.oss.tencent.TencentOssConnection;
import com.tencent.cloud.cos.util.Base64;
import com.tencent.cloud.cos.util.SHA1;
import org.apache.commons.codec.digest.HmacAlgorithms;
import org.apache.commons.codec.digest.HmacUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;
import org.springframework.core.env.Environment;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

@Configuration
@ConditionalOnProperty(name = "sop-service.oss", havingValue = "tencent")
@ImportResource("classpath:META-INF/oss/rumba-oss-tencent.xml")
public class OssTencent {

  @Autowired(required = false)
  private Bucket bucket;
  @Autowired
  private Environment env;

  @Bean
  public OssSignature signature() {
    return path -> {
      // 签名开始时间、结束时间
      Date now = new Date();
      Date nextDate = getNextDate(now);

      JSONObject policyRaw = new JSONObject();
      policyRaw.put("expiration", getISO8601Timestamp(nextDate));
      JSONArray conditions = new JSONArray();
      JSONObject bucketName = new JSONObject();
      bucketName.put("bucket", bucket.getBucketName());
      conditions.put(bucketName);
      List<String> startWith = new ArrayList<>();
      startWith.add("starts-with");
      startWith.add("key");
      startWith.add(path);
      conditions.put(startWith);
      JSONObject algorithm = new JSONObject();
      algorithm.put("q-sign-algorithm", "sha1");
      conditions.put(algorithm);
      JSONObject ak = new JSONObject();
      ak.put("q-ak", env.getProperty("rumba-oss-tencent.connection.secretId"));
      conditions.put(ak);
      JSONObject signTime = new JSONObject();
      String keyTime = getSecondTimestamp(now) + ";" + getSecondTimestamp(nextDate);
      signTime.put("q-sign-time", keyTime);
      conditions.put(signTime);
      policyRaw.put("conditions", conditions);
      TencentOssConnection connection = (TencentOssConnection) bucket.getConnection();
      String secretId = env.getProperty("rumba-oss-tencent.connection.secretId", "");
      String secretKey = env.getProperty("rumba-oss-tencent.connection.secretKey", "");

      String signKey = new HmacUtils(HmacAlgorithms.HMAC_SHA_1, secretKey).hmacHex(keyTime.getBytes());
      String stringToSign = SHA1.stringToSHA(policyRaw.toString());
      String signature = new HmacUtils(HmacAlgorithms.HMAC_SHA_1, signKey).hmacHex(stringToSign.getBytes());
      String policy = Base64.encode(policyRaw.toString().getBytes());

      Map<String, Object> params = new HashMap<String, Object>();
      params.put("policy", policy);
      params.put("algorithm", "sha1");
      params.put("secretId", secretId);
      params.put("keyTime", keyTime);
      params.put("signature", signature);
      params.put("key", path);
      params.put("type", "tencent");
      params.put("host", "https://" + connection.getHost(bucket.getBucketName()));
      return params;
    };
  }

  /**
   * 传入Data类型日期，返回字符串类型时间（ISO8601标准时间）
   */
  public static String getISO8601Timestamp(Date date) {
    TimeZone tz = TimeZone.getTimeZone("Asia/Shanghai");
    DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
    df.setTimeZone(tz);
    String nowAsISO = df.format(date);
    return nowAsISO;
  }

  public static Date getNextDate(Date date) {
    if (date == null) {
      return null;
    }
    Calendar calendar = Calendar.getInstance();
    calendar.setTime(date);
    calendar.add(Calendar.DATE, 1);
    return calendar.getTime();
  }

  /**
   * 获取精确到秒的时间戳
   */
  public static String getSecondTimestamp(Date date) {
    if (null == date) {
      return null;
    }
    String timestamp = String.valueOf(date.getTime() / 1000);
    return timestamp;
  }
}
