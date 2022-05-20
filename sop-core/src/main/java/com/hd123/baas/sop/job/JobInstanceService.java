/**
 * 版权所有(C)，上海海鼎信息工程股份有限公司，2014，所有权利保留。
 *
 * 项目名：	rumba-quartz-core
 * 文件名：	JobInstanceServiceImpl.java
 * 模块说明：	
 * 修改历史：
 * 2014-1-14 - Li Ximing - 创建。
 */
package com.hd123.baas.sop.job;

import java.util.List;

import org.apache.commons.lang3.ObjectUtils;
import org.quartz.JobExecutionContext;
import org.quartz.Scheduler;
import org.quartz.UnableToInterruptJobException;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Service;

import com.hd123.baas.sop.job.converter.JobInstanceConverter;
import com.hd123.baas.sop.job.dao.JobInstanceDaoImpl;
import com.hd123.baas.sop.job.entity.PJobInstance;
import com.hd123.baas.sop.job.mapper.InterruptInfoMapper;
import com.hd123.rumba.commons.biz.entity.OperateContext;
import com.hd123.rumba.commons.biz.entity.OperateInfo;
import com.hd123.rumba.commons.lang.Assert;
import com.qianfan123.baas.common.BaasException;

/**
 * 作业实例业务层接口实现。
 *
 * @see JobInstanceService
 * @author Li Ximing
 * @since 1.0
 *
 */
@Service
public class JobInstanceService implements ApplicationContextAware {

  private ApplicationContext appCtx;
  @Autowired(required = false)
  private Scheduler scheduler;

  @Override
  public void setApplicationContext(ApplicationContext appCtx) throws BeansException {
    this.appCtx = appCtx;
  }

  public boolean interrupt(String jobInstanceId, OperateContext<?> operCtx)
      throws BaasException, IllegalStateException {
    Assert.assertAttributeNotNull(scheduler, "scheduler");
    try {
      OperateInfo interruptInfo = OperateInfo.newInstance(operCtx);
      InterruptInfoMapper mapper = new InterruptInfoMapper();
      List<JobExecutionContext> contexts = scheduler.getCurrentlyExecutingJobs();
      for (JobExecutionContext context : contexts) {
        if (ObjectUtils.equals(jobInstanceId, context.getFireInstanceId())) {
          mapper.writeTo(interruptInfo, context.getMergedJobDataMap());
          break;
        }
      }

      return scheduler.interrupt(jobInstanceId);
    } catch (UnableToInterruptJobException e) {
      assert false;
      return false;
    } catch (Exception e) {
      throw new BaasException(e);
    }
  }

  public PJobInstance get(String tenant, String excelUuid) throws IllegalStateException, BaasException {
    Assert.assertAttributeNotNull(scheduler, "scheduler");
    Assert.assertAttributeNotNull(appCtx, "appCtx");

    try {
      JobInstanceDaoImpl dao = appCtx.getBean(JobInstanceDaoImpl.class);
      PJobInstance perz = dao.get(tenant, excelUuid);
      if (perz == null) {
        return null;
      }
      List<JobExecutionContext> contexts = scheduler.getCurrentlyExecutingJobs();

      return refreshJobInstance(perz, contexts);
    } catch (RuntimeException e) {
      throw e;
    } catch (Exception e) {
      throw new BaasException(e);
    }
  }

  public PJobInstance getByName(String tenant, String jobName) throws IllegalArgumentException, BaasException {
    JobInstanceDaoImpl dao = appCtx.getBean(JobInstanceDaoImpl.class);
    return dao.getByName(tenant, jobName);
  }

  public boolean isExecuting(String jobInstanceId) throws BaasException {
    try {
      List<JobExecutionContext> contexts = scheduler.getCurrentlyExecutingJobs();
      for (JobExecutionContext context : contexts) {
        if (ObjectUtils.equals(jobInstanceId, context.getFireInstanceId())) {
          return true;
        }
      }
      return false;
    } catch (RuntimeException e) {
      throw e;
    } catch (Exception e) {
      throw new BaasException(e);
    }
  }

  private PJobInstance refreshJobInstance(PJobInstance instance, List<JobExecutionContext> contexts) {
    assert instance != null;
    assert contexts != null;

    for (JobExecutionContext context : contexts) {
      if (instance.getInstanceId().equals(context.getFireInstanceId())) {
        PJobInstance pinstance = new JobInstanceConverter().convert(context);
        return pinstance;
      }
    }
    return instance;
  }

  private void refreshJobInstances(List<PJobInstance> instances, List<JobExecutionContext> contexts) {
    assert instances != null;
    assert contexts != null;
    if (instances.isEmpty() || contexts.isEmpty()) {
      return;
    }

    JobInstanceConverter bic = new JobInstanceConverter();

    for (int index = 0; index < instances.size(); index++) {
      PJobInstance instance = instances.get(index);
      for (JobExecutionContext context : contexts) {
        if (instance.getInstanceId().equals(context.getFireInstanceId())) {
          instance = bic.convert(context);
          instances.set(index, instance);
        }
      }
    }
  }

}
