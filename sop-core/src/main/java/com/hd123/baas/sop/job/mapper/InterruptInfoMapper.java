/**
 * 版权所有(C)，上海海鼎信息工程股份有限公司，2014，所有权利保留。
 * 
 * 项目名：	rumba-quartz-api
 * 文件名：	InterruptInfoMapper.java
 * 模块说明：	
 * 修改历史：
 * 2014-2-11 - Li Ximing - 创建。
 */
package com.hd123.baas.sop.job.mapper;

/**
 * 作业中断操作信息到作业映射表的读写工具。
 * 
 * @author Li Ximing
 * @since 1.0
 * 
 */
public class InterruptInfoMapper extends OperateInfoMapper {

  /** 用于作业数据映射表中存放作业中断操作信息的键。 */
  public static final String JOB_DATA_MAP_KEY = "RB$InterruptInfo";

  @Override
  protected String getJobDataMapKey() {
    return JOB_DATA_MAP_KEY;
  }

}
