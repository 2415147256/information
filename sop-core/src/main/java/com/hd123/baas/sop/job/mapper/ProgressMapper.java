/**
 * 版权所有(C)，上海海鼎信息工程股份有限公司，2016，所有权利保留。
 * 
 * 项目名：	dpos-service
 * 文件名：	ProgressMapper.java
 * 模块说明：	
 * 修改历史：
 * 2016年9月18日 - huzexiong - 创建。
 */
package com.hd123.baas.sop.job.mapper;

import java.util.Map;

import com.hd123.baas.sop.job.tools.Progress;
import com.hd123.rumba.commons.lang.Assert;

/**
 * {@link Progress}到作业数据映射表的读写工具。
 * 
 * @author huzexiong
 * @since 1.0
 *
 */
public class ProgressMapper implements JobDataMapper<Progress> {

  /** 用于作业数据映射表中存放进度数据的键。 */
  public static final String JOB_DATA_MAP_KEY = "RB$Progress";

  @Override
  public Progress readFrom(Map<String, Object> jobDataMap) throws IllegalArgumentException {
    return readFrom(jobDataMap, JOB_DATA_MAP_KEY);
  }

  @Override
  public Progress readFrom(Map<String, Object> jobDataMap, String key) throws IllegalArgumentException {
    Assert.assertArgumentNotNull(jobDataMap, "jobDataMap");
    String str = (String) jobDataMap.get(key == null ? JOB_DATA_MAP_KEY : key);
    return str == null ? new Progress() : Progress.valueOf(str);
  }

  @Override
  public void writeTo(Progress data, Map<String, Object> jobDataMap) throws IllegalArgumentException {
    writeTo(data, jobDataMap, JOB_DATA_MAP_KEY);
  }

  @Override
  public void writeTo(Progress data, Map<String, Object> jobDataMap, String key) throws IllegalArgumentException {
    Assert.assertArgumentNotNull(jobDataMap, "jobDataMap");
    String ripeKey = key == null ? JOB_DATA_MAP_KEY : key;
    if (data == null) {
      jobDataMap.remove(ripeKey);
    } else {
      jobDataMap.put(ripeKey, data.toJson().toString());
    }
  }

}