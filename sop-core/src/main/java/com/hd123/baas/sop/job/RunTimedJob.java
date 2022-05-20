package com.hd123.baas.sop.job;

import java.util.Date;
import java.util.UUID;

import org.quartz.DisallowConcurrentExecution;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import com.hd123.baas.sop.job.entity.TimedJob;
import com.hd123.baas.sop.job.timed.TimedJobService;
import com.hd123.rumba.commons.biz.query.Cop;
import com.hd123.rumba.commons.biz.query.QueryDefinition;
import com.hd123.rumba.commons.biz.query.QueryOrderDirection;
import com.hd123.rumba.commons.biz.query.QueryResult;
import com.qianfan123.baas.common.http.ControllerHelper;

import lombok.extern.slf4j.Slf4j;

/**
 * 检查{@link com.qianfan123.sas.job.entity.TimedJob#expectedRunTime}>=系统当前时间的记录，投递到mq并删除
 * 
 * @author yanghaixiao
 */
@Slf4j
@DisallowConcurrentExecution
public class RunTimedJob implements Job {

  @Autowired
  private TimedJobService timedJobService;
  @Value("${spring.application.name}")
  private String stack;

  @Override
  public void execute(JobExecutionContext context) {
    MDC.put("trace_id", UUID.randomUUID().toString());
    Long start = System.currentTimeMillis();
    log.info("{} RunTimedJob running.", stack);
    int page = 0;
    int pageSize = 100;
    Integer total = 0;
    do {
      QueryDefinition qd = new QueryDefinition();
      qd.addByField(TimedJob.Queries.EXPECTED_RUN_TIME, Cop.LESS_OR_EQUALS, new Date());
      qd.setPage(page++);
      qd.setPageSize(pageSize);
      qd.addOrder(TimedJob.Queries.EXPECTED_RUN_TIME, QueryOrderDirection.asc);
      QueryResult<TimedJob> result = timedJobService.query(qd);
      int size = result.getRecords().size();
      if (size == 0) {
        break;
      }
      total = total + size;
      for (TimedJob job : result.getRecords()) {
        try {
          timedJobService.executeAsync(job);
        } catch (Exception e) {
          log.error("{}  TimedJob {} submit error, waiting for the next round.", stack, job, e);
        }
      }
      if (!ControllerHelper.isMore(result)) {
        break;
      }
    } while (true);
    Long end = System.currentTimeMillis();
    log.info("{} RunTimedJob finished, submit message count {}, cost {} ms.", stack, total, end - start);
  }
}
