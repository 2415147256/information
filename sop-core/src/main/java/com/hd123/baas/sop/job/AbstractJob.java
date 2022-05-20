/**
 * 版权所有(C)，上海海鼎信息工程股份有限公司，2016，所有权利保留。
 * 
 * 项目名：	dpos-service
 * 文件名：	AbstractJob.java
 * 模块说明：	
 * 修改历史：
 * 2016年9月18日 - huzexiong - 创建。
 */
package com.hd123.baas.sop.job;

import java.text.MessageFormat;
import java.util.Date;

import org.quartz.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import com.hd123.baas.sop.job.converter.JobInstanceConverter;
import com.hd123.baas.sop.job.dao.JobInstanceDao;
import com.hd123.baas.sop.job.entity.JobInstanceResult;
import com.hd123.baas.sop.job.entity.JobInstanceState;
import com.hd123.baas.sop.job.entity.PJobInstance;
import com.hd123.baas.sop.job.tools.JobContext;
import com.hd123.baas.sop.job.tools.JobNotifier;
import com.hd123.baas.sop.job.tools.Progresser;
import com.hd123.rumba.commons.lang.Assert;

/**
 * 所有作业的基类。
 * 
 * @author huzexiong
 * @since 1.0
 * 
 */
public abstract class AbstractJob implements InterruptableJob {

  private static final Logger logger = LoggerFactory.getLogger(AbstractJob.class);

  public static final String TENANT = "job_tenant";

  public static final String ORG_ID = "job_org_id";

  public static final String TRACE_ID = "trace_id";

  protected JobExecutionContext context;

  private JobInstanceDao jobInstanceDao;

  public JobInstanceDao getJobInstanceDao(JobExecutionContext context) throws SchedulerException {
    if (null == jobInstanceDao) {
      jobInstanceDao = (JobInstanceDao) context.getScheduler().getContext().get(JobInstanceDao.class.getName());
    }
    return jobInstanceDao;
  }

  @Override
  public void execute(JobExecutionContext context) throws JobExecutionException {
    JobContext.initial(context);
    this.context = context;
    String traceID = getContextStringValue(AbstractJob.TRACE_ID);
    if (traceID != null) {
      MDC.put(AbstractJob.TRACE_ID, traceID);
    }
    try {
      preProcess();

      doExecute();

      postProcess(JobInstanceResult.completed, null);
    } catch (InterruptedException e) {
      postProcess(JobInstanceResult.interrupted, e);
    } catch (Exception e) {
      postProcess(JobInstanceResult.aborted, e);
    } finally {
      JobContext.free();
    }
  }

  public String getFileName() {
    return null;
  }

  /**
   * 执行作业。
   *
   * @throws InterruptedException
   * @throws Exception
   */
  protected abstract void doExecute() throws InterruptedException, Exception;

  /**
   * 取得作业结果通知器对象。<br>
   * 子类开发者可以通过重写此方法，实现发出作业执行结果通知的功能。
   *
   * @return 返回null将不会发出通知。
   */
  protected JobNotifier getNotifier() {
    return null;
  }

  @Override
  public void interrupt() throws UnableToInterruptJobException {
    logger.debug("Try to interrupt the job.");

    JobContext.interrupt(context);
  }

  /**
   * 预处理。在{@link #doExecute()}之前被调用。
   */
  protected void preProcess() {
    assert context != null;
    saveForStartingJob(context);
  }

  /**
   * 后处理。在{@link #doExecute()}之后被调用。
   *
   * @param result
   *          作业执行结果。禁止传入null。
   * @param e
   *          异常对象。
   * @throws IllegalArgumentException
   *           当参数result为null时抛出。
   */
  protected void postProcess(JobInstanceResult result, Exception e) throws IllegalArgumentException {
    assert context != null;
    Assert.assertArgumentNotNull(result, "result");

    log(result, e);

    saveForFinishingJob(context, result);

    notify(result, e);
  }

  private void log(JobInstanceResult result, Exception e) {
    if (JobInstanceResult.completed.equals(result)) {
      logger.debug("Job Completed.");
    } else if (JobInstanceResult.interrupted.equals(result)) {
      logger.debug("Job Interrupted.");
    } else if (JobInstanceResult.aborted.equals(result)) {
      logger.error("Job Aborted.", e);
    } else {
      assert false;
    }
  }

  private void saveForStartingJob(JobExecutionContext context) {
    assert context != null;
    try {
      PJobInstance jobInstance = new JobInstanceConverter().convert(context);
      getJobInstanceDao(context).saveModify(context.getJobDetail().getJobDataMap().getString(TENANT), jobInstance);
    } catch (Exception e) {
      logger.error("Fail to save for starting job.", e);
    }
  }

  private void saveForFinishingJob(JobExecutionContext context, JobInstanceResult result) {
    assert context != null;
    assert result != null;
    try {
      PJobInstance jobInstance = new JobInstanceConverter().convert(context);

      jobInstance.setState(JobInstanceState.over);
      jobInstance.setFinishedAt(new Date());
      jobInstance.setResult(result);

      getJobInstanceDao(context).saveModify(context.getJobDetail().getJobDataMap().getString(TENANT), jobInstance);
    } catch (Exception e) {
      logger.error(MessageFormat.format("Fail to save for finishing job: {0}.", result), e);
    }
  }

  private void notify(JobInstanceResult result, Exception caught) {
    assert result != null;
    JobNotifier notifier = getNotifier();
    if (notifier == null) {
      return;
    }
    try {
      notifier.nodify(result, caught, context);
    } catch (Exception e) {
      logger.error("Fail to notify.", e);
    }
  }

  /**
   * 处理中
   */
  protected void processing() {
    assert context != null;
    saveForExecutingJob(context);
  }

  private void saveForExecutingJob(JobExecutionContext context) {
    assert context != null;
    try {
      PJobInstance jobInstance = new JobInstanceConverter().convert(context);
      getJobInstanceDao(context).updateDataMap(context.getJobDetail().getJobDataMap().getString(TENANT),
          jobInstance.getInstanceId(), jobInstance.getDataMap());
    } catch (Exception e) {
      logger.error("Fail to save for executing job.", e);
    }
  }

  protected <T> T getContextObject(Class<T> entity) throws Exception {
    return (T) JobContext.getContext().getScheduler().getContext().get(entity.getName());
  }

  protected String getStringParam(String key) {
    if (JobContext.getJobDetail() == null) {
      return null;
    }
    return JobContext.getJobDetail().getJobDataMap().getString(key);
  }

  protected JobDataMap getDataMap() {
    if (JobContext.getJobDetail() == null) {
      return null;
    }
    return JobContext.getJobDetail().getJobDataMap();
  }

  /**
   * 更新进度
   */
  public void updateProgress(int step, String message) {
    Progresser progresser = JobContext.getProgresser();
    try {
      progresser.stepBy(step);
      progresser.addMessage(message);
      PJobInstance jobInstance = new JobInstanceConverter().convert(context);
      getJobInstanceDao(context).updateDataMap(context.getJobDetail().getJobDataMap().getString(TENANT),
          jobInstance.getInstanceId(), jobInstance.getDataMap());
    } catch (Exception e) {
      logger.error("Fail to update.", e);
    }
  }

  protected String getContextStringValue(String key) {
    return JobContext.getJobDetail().getJobDataMap().getString(key);
  }

  protected void putContextStringValue(String key, String value) {
    JobContext.getJobDetail().getJobDataMap().put(key, value);
  }
}
