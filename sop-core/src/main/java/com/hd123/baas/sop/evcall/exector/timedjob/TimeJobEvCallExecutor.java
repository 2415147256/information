package com.hd123.baas.sop.evcall.exector.timedjob;

import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

import org.slf4j.MDC;
import org.springframework.stereotype.Component;

import com.hd123.baas.sop.evcall.AbstractEvCallExecutor;
import com.hd123.baas.sop.job.entity.TimedJob;
import com.hd123.baas.sop.job.timed.TimedJobExecutor;
import com.hd123.baas.sop.job.timed.TimedJobService;
import com.hd123.baas.sop.utils.BaasJSONUtil;
import com.hd123.baas.sop.utils.DateUtil;
import com.hd123.rumba.commons.lang.Assert;
import com.hd123.rumba.evcall.EvCallExecutionContext;
import com.qianfan123.baas.common.BaasException;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class TimeJobEvCallExecutor extends AbstractEvCallExecutor<TimedJobMsg> {

  public static final String TIME_JOB_CREATE_EXECUTOR_ID = TimeJobEvCallExecutor.class.getSimpleName();

  @Override
  protected void doExecute(TimedJobMsg msg, EvCallExecutionContext context) throws Exception {
    MDC.put("trace_id", UUID.randomUUID().toString());
    Assert.notNull(msg, "msg");
    Assert.notNull(msg.getJob(), "msg.job");
    TimedJob job = msg.getJob();
    Assert.notNull(job.getCallbackBeanName(), "msg.job.callbackBeanName");
    int code = getBean(job.getCallbackBeanName(), TimedJobExecutor.class).execute(job);
    if (code == TimedJobExecutor.RETRY) {
      job.setRunTimes(job.getRunTimes() + 1);
      int runTimes = job.getRunTimes();
      int[] intervals = job.getIntervals();
      int len = intervals.length;
      if (len >= runTimes) {
        int seconds = intervals[runTimes - 1];
        job.setExpectedRunTime(DateUtil.add(new Date(), Calendar.SECOND, seconds));
      } else if (len > 0) {
        // 如果执行次数已经超过设置的执行间隔数，那么采用最后一个时间间隔
        int seconds = intervals[len - 1];
        job.setExpectedRunTime(DateUtil.add(new Date(), Calendar.SECOND, seconds));
      } else {
        // 如果需要重新执行但没有指定重试时间间隔，就不再执行了。
        return;
      }
      getBean(TimedJobService.class).submit(job);
    }
  }

  @Override
  protected TimedJobMsg decodeMessage(String msg) throws BaasException {
    log.info("收到timedJobMsg:{}", BaasJSONUtil.safeToJson(msg));
    return BaasJSONUtil.safeToObject(msg, TimedJobMsg.class);
  }
}
