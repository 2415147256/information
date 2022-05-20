package com.hd123.baas.sop.configuration.oss;

import com.aliyun.oss.OSSClient;
import com.aliyun.oss.common.utils.BinaryUtil;
import com.aliyun.oss.model.CannedAccessControlList;
import com.aliyun.oss.model.MatchMode;
import com.aliyun.oss.model.PolicyConditions;
import com.aliyun.oss.model.SetBucketAclRequest;
import com.hd123.rumba.oss.aliyun.AliyunOssConnection;
import com.hd123.rumba.oss.api.Bucket;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;

import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;

@Configuration
@ConditionalOnProperty(name = "sop-service.oss", havingValue = "aliyun")
@ImportResource("classpath:META-INF/oss/rumba-oss-aliyun.xml")
public class OssAliyun {
  private static final int EXPIRE_TIME = 43200;

  @Autowired(required = false)
  private Bucket bucket;

  @Bean
  public OssSignature signature() {
    return this::signature;
  }

  @Cacheable(value = "oss", key = "''+#path")
  public Map<String, Object> signature(String path) {
    Map<String, Object> signature = new LinkedHashMap<String, Object>();
    signature.put("type", "aliyun");
    if (path == null) {
      return signature;
    }

    try {
      AliyunOssConnection connection = (AliyunOssConnection) bucket.getConnection();
      OSSClient client = new OSSClient(connection.getEndpoint(), connection.getAccessKeyId(), connection.getAccessKeySecret());
      client.setBucketAcl(new SetBucketAclRequest(bucket.getBucketName(), CannedAccessControlList.PublicRead));
      String host = "https://" + bucket.getBucketName() + "." + connection.getEndpoint().substring(connection.getEndpoint().indexOf("oss"));

      long expireEndTime = System.currentTimeMillis() + EXPIRE_TIME * 1000;
      Date expiration = new Date(expireEndTime);
      PolicyConditions policyConds = new PolicyConditions();
      policyConds.addConditionItem(PolicyConditions.COND_CONTENT_LENGTH_RANGE, 0, 1048576000);
      policyConds.addConditionItem(MatchMode.StartWith, PolicyConditions.COND_KEY, path);

      String postPolicy = client.generatePostPolicy(expiration, policyConds);
      byte[] binaryData = postPolicy.getBytes(StandardCharsets.UTF_8);
      String encodedPolicy = BinaryUtil.toBase64String(binaryData);
      String postSignature = client.calculatePostSignature(postPolicy);

      signature.put("OSSAccessKeyId", connection.getAccessKeyId());
      signature.put("key", path);
      signature.put("host", host);
      signature.put("policy", encodedPolicy);
      signature.put("signature", postSignature);
      signature.put("success_action_status", "200");
      signature.put("expire", String.valueOf(expireEndTime / 1000));
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
    return signature;
  }
}
