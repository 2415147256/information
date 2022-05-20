package com.hd123.baas.sop.job.timed.executor;

import java.util.UUID;

import org.springframework.stereotype.Component;

import com.hd123.baas.sop.job.entity.TimedJob;
import com.hd123.baas.sop.job.timed.TimedJobExecutor;

import lombok.extern.slf4j.Slf4j;

/**
 * @author zhengzewang on 2020/11/10.
 */
@Component
@Slf4j
public class DemoTimedJobExecutor implements TimedJobExecutor {

  public static final String BEAN_ID = "demoTimedJobExecutor";

  @Override
  public int execute(TimedJob job) throws Exception {
    log.info("DemoTimedJobExecutor执行第{}次", job.getRunTimes());
    if (job.getRunTimes() < 5) {
      return 1;
    }
    return 0;
  }

  public String buildTranId() {
    return "Demo" + "_" + UUID.randomUUID().toString();
  }
}
