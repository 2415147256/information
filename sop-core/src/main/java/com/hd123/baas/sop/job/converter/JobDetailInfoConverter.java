/**
 * 版权所有(C)，上海海鼎信息工程股份有限公司，2016，所有权利保留。
 * 
 * 项目名：	dpos-service
 * 文件名：	JobDetailInfoConverter.java
 * 模块说明：	
 * 修改历史：
 * 2016年9月18日 - huzexiong - 创建。
 */
package com.hd123.baas.sop.job.converter;

import com.hd123.baas.sop.job.entity.PJobDetailInfo;
import com.hd123.baas.sop.job.mapper.DataMapMapper;
import org.quartz.JobDetail;

import com.hd123.rumba.commons.util.converter.ConversionException;
import com.hd123.rumba.commons.util.converter.Converter;

/**
 * 
 * @author huzexiong
 *
 */
public class JobDetailInfoConverter implements Converter<JobDetail, PJobDetailInfo> {

  @Override
  public PJobDetailInfo convert(JobDetail source) throws ConversionException {
    if (source == null) {
      return null;
    }

    PJobDetailInfo target = new PJobDetailInfo();
    target.setGroup(source.getKey().getGroup());
    target.setName(source.getKey().getName());
    target.setJobClassName(source.getJobClass().getName());
    target.setDataMap(DataMapMapper.serialize(source.getJobDataMap()));
    target.setDesciption(source.getDescription());
    return target;
  }

}
