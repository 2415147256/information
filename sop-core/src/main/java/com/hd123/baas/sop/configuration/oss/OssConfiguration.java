package com.hd123.baas.sop.configuration.oss;

import com.hd123.rumba.oss.api.Bucket;
import com.hd123.rumba.oss.api.LifecycleRule;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.stereotype.Component;

import java.util.Collections;

@Slf4j
@Component
// TODO: Rumba-OSS 暂未支持华为云
// @ConditionalOnExpression("'${sop-service.oss}'.equals('aliyun') or '${sop-service.oss}'.equals('tencent') or '${sop-service.oss}'.equals('huawei')")
public class OssConfiguration {

  @Autowired(required = false)
  private Bucket bucket;

  @EventListener
  public void processApplicationReadyEvent(ApplicationReadyEvent event) {
    if (event == null)
      return;

    resetLifecycleRules();
    resetImportTplFiles();
  }

  private void resetLifecycleRules() {
    log.info("设置导入结果OSS生命周期规则");
    LifecycleRule rule = new LifecycleRule();
    rule.setExpirationDays(1);
    rule.setKeyPrefix("sop/tempOf1Day");
    bucket.resetLifecycleRules(Collections.singletonList(rule), false);

    rule = new LifecycleRule();
    rule.setExpirationDays(7);
    rule.setKeyPrefix("sop/tempOf7Day");
    bucket.resetLifecycleRules(Collections.singletonList(rule), false);

    rule = new LifecycleRule();
    rule.setExpirationDays(30);
    rule.setKeyPrefix("sop/tempOf30Day");
    bucket.resetLifecycleRules(Collections.singletonList(rule), false);
  }

  private void resetImportTplFiles() {
    try {
      ResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
      org.springframework.core.io.Resource[] resources = resolver.getResources("classpath*:META-INF/imptpl/**/*.*");

      for (org.springframework.core.io.Resource r : resources) {
        String path = r.getURL().getPath();
        String[] keys = path.split("/imptpl/");
        if (keys.length < 2) {
          continue;
        }
        String key = "sop/imptpl/" + keys[1].replace("\\", "/");
        bucket.put(key, r.getInputStream());
      }

    } catch (Exception e) {
      log.error("初始化导入模版示例文件失败", e);
    }
  }
}
