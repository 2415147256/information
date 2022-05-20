/**
 * 版权所有(C)，上海海鼎信息工程股份有限公司，2016，所有权利保留。
 * 
 * 项目名：	dpos-service
 * 文件名：	JobInstanceConverter.java
 * 模块说明：	
 * 修改历史：
 * 2016年9月18日 - huzexiong - 创建。
 */
package com.hd123.baas.sop.job.converter;

import com.hd123.baas.sop.job.entity.JobInstanceState;
import com.hd123.baas.sop.job.entity.PJobInstance;
import com.hd123.baas.sop.job.mapper.DataMapMapper;
import org.quartz.JobExecutionContext;

import com.hd123.rumba.commons.util.converter.ConversionException;
import com.hd123.rumba.commons.util.converter.Converter;

/**
 * @author huzexiong
 *
 */
public class JobInstanceConverter implements Converter<JobExecutionContext, PJobInstance> {

  private SchedulerInfoConverter schedulerConverter = new SchedulerInfoConverter();
  private JobDetailInfoConverter detailConverter = new JobDetailInfoConverter();
  private TriggerInfoConverter triggerConverter = new TriggerInfoConverter();

  @Override
  public PJobInstance convert(JobExecutionContext source) throws ConversionException {
    if (source == null) {
      return null;
    }

    PJobInstance target = new PJobInstance();
    target.setInstanceId(source.getFireInstanceId());
    target.setState(JobInstanceState.executing);
    target.setScheduler(schedulerConverter.convert(source.getScheduler()));
    target.setDetail(detailConverter.convert(source.getJobDetail()));
    target.setTrigger(triggerConverter.convert(source.getTrigger()));
    target.setDataMap(DataMapMapper.serialize(source.getMergedJobDataMap()));
    target.setStartedAt(source.getFireTime());
    return target;
  }

}
