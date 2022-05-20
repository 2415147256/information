/**
 * 版权所有(C)，上海海鼎信息工程股份有限公司，2016，所有权利保留。
 * 
 * 项目名：	dpos-service
 * 文件名：	TriggerInfoConverter.java
 * 模块说明：	
 * 修改历史：
 * 2016年9月18日 - huzexiong - 创建。
 */
package com.hd123.baas.sop.job.converter;

import com.hd123.baas.sop.job.entity.PTriggerInfo;
import com.hd123.baas.sop.job.mapper.DataMapMapper;
import org.quartz.CronTrigger;
import org.quartz.Trigger;

import com.hd123.rumba.commons.util.converter.ConversionException;
import com.hd123.rumba.commons.util.converter.Converter;

/**
 * @author huzexiong
 *
 */
public class TriggerInfoConverter implements Converter<Trigger, PTriggerInfo> {

  @Override
  public PTriggerInfo convert(Trigger source) throws ConversionException {
    if (source == null) {
      return null;
    }
    PTriggerInfo target = new PTriggerInfo();
    target.setGroup(source.getKey().getGroup());
    target.setName(source.getKey().getName());
    target.setTriggerClassName(source.getClass().getName());
    if (source instanceof CronTrigger) {
      target.setCronExpression(((CronTrigger) source).getCronExpression());
    }
    target.setDataMap(DataMapMapper.serialize(source.getJobDataMap()));
    target.setStartTime(source.getStartTime());
    target.setEndTime(source.getEndTime());
    target.setDescription(source.getDescription());
    return target;
  }

}
