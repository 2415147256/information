/**
 * 版权所有(C)，上海海鼎信息工程股份有限公司，2016，所有权利保留。
 * <p>
 * 项目名：	dpos-service
 * 文件名：	JobInstanceDao.java
 * 模块说明：
 * 修改历史：
 * 2016年9月18日 - huzexiong - 创建。
 */
package com.hd123.baas.sop.job.dao;

import com.hd123.baas.sop.job.entity.PJobInstance;
import com.qianfan123.baas.common.BaasException;

/**
 * @author huzexiong
 */
public interface JobInstanceDao {

  /**
   * 新增
   *
   * @param tenant
   * @param jobInstance
   * @throws IllegalArgumentException
   */
  void saveModify(String tenant, PJobInstance jobInstance) throws IllegalArgumentException, BaasException;

  /**
   * 根据文件uuid
   *
   * @param tenant
   * @param excelUuid
   * @return
   */
  PJobInstance get(String tenant, String excelUuid) throws BaasException;

  PJobInstance getByName(String tenant, String jobName) throws BaasException;

  /**
   * 根据job实例id
   *
   * @param tenant
   * @param instanceId
   * @return
   */
  PJobInstance getByInstanceId(String tenant, String instanceId) throws BaasException;

  /**
   * 更新数据
   *
   * @param tenant
   * @param instanceId
   * @param dataMap
   */
  void updateDataMap(String tenant, String instanceId, String dataMap) throws BaasException;
}
