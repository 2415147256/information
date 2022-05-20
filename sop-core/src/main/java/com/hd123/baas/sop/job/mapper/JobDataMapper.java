/**
 * 版权所有(C)，上海海鼎信息工程股份有限公司，2016，所有权利保留。
 * 
 * 项目名：	dpos-service
 * 文件名：	JobDataMapper.java
 * 模块说明：	
 * 修改历史：
 * 2016年9月18日 - huzexiong - 创建。
 */
package com.hd123.baas.sop.job.mapper;

import java.util.Map;

/**
 * 指示类提供将指定类型写入或读取自作业映射表的功能。
 * 
 * @author huzexiong
 * @since 1.0
 * 
 */
public interface JobDataMapper<T> {

  /**
   * 从作业映射表中读取数据对象。
   * 
   * @param jobDataMap
   *          作业映射表，禁止传入null。
   * @return 返回取得的数据对象。
   * @throws IllegalArgumentException
   *           当参数jobDataMap为null时抛出。
   */
  T readFrom(Map<String, Object> jobDataMap) throws IllegalArgumentException;

  /**
   * 从作业映射表中读取数据对象。
   * 
   * @param jobDataMap
   *          作业映射表，禁止传入null。
   * @param key
   *          数据对应的键，传入null意味着从默认键中读取。
   * @return 返回取得的数据对象。
   * @throws IllegalArgumentException
   *           当参数jobDataMap为null时抛出。
   */
  T readFrom(Map<String, Object> jobDataMap, String key) throws IllegalArgumentException;

  /**
   * 将数据对象写入作业映射表中。
   * 
   * @param data
   *          数据对象。
   * @param jobDataMap
   *          作业映射表，禁止传入null。
   * @throws IllegalArgumentException
   *           当参数jobDataMap为null时抛出。
   */
  void writeTo(T data, Map<String, Object> jobDataMap) throws IllegalArgumentException;

  /**
   * 将数据对象写入作业映射表中。
   * 
   * @param data
   *          数据对象。
   * @param jobDataMap
   *          作业映射表，禁止传入null。
   * @param key
   *          数据对应的键，传入null意味着写入默认键。
   * @throws IllegalArgumentException
   *           当参数jobDataMap为null时抛出。
   */
  void writeTo(T data, Map<String, Object> jobDataMap, String key) throws IllegalArgumentException;

}
