/**
 * 版权所有(C)，上海海鼎信息工程股份有限公司，2016，所有权利保留。
 * 
 * 项目名：	dpos-service
 * 文件名：	SchedulerInfoConverter.java
 * 模块说明：	
 * 修改历史：
 * 2016年9月18日 - huzexiong - 创建。
 */
package com.hd123.baas.sop.job.converter;

import com.hd123.baas.sop.job.entity.PSchedulerInfo;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hd123.rumba.commons.util.converter.ConversionException;
import com.hd123.rumba.commons.util.converter.Converter;

/**
 * @author huzexiong
 *
 */
public class SchedulerInfoConverter implements Converter<Scheduler, PSchedulerInfo> {

  private static final Logger logger = LoggerFactory.getLogger(SchedulerInfoConverter.class);

  @Override
  public PSchedulerInfo convert(Scheduler source) throws ConversionException {
    if (source == null) {
      return null;
    }
    try {
      PSchedulerInfo target = new PSchedulerInfo();
      target.setName(source.getSchedulerName());
      target.setInstanceId(source.getSchedulerInstanceId());
      return target;
    } catch (SchedulerException e) {
      logger.error("", e);
      return null;
    }
  }

}
