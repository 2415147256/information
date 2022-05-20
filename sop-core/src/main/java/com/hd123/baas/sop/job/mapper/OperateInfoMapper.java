/**
 * 版权所有(C)，上海海鼎信息工程股份有限公司，2014，所有权利保留。
 * 
 * 项目名：	rumba-quartz-api
 * 文件名：	OperateInfoMapper.java
 * 模块说明：	
 * 修改历史：
 * 2014-2-20 - Li Ximing - 创建。
 */
package com.hd123.baas.sop.job.mapper;

import java.util.Date;
import java.util.Map;

import com.hd123.rumba.commons.biz.entity.OperateInfo;
import com.hd123.rumba.commons.biz.entity.Operator;
import com.hd123.rumba.commons.json.JsonObject;
import com.hd123.rumba.commons.lang.Assert;

/**
 * 操作信息到作业映射表的读写工具。
 * 
 * @author Li Ximing
 * @since 1.0
 *
 */
public class OperateInfoMapper implements JobDataMapper<OperateInfo> {

  public static final String JOB_DATA_MAP_KEY = "RB$OperateInfo";

  private static final String ATTR_TIME = "time";
  private static final String ATTR_OPERATOR = "operate";
  private static final String ATTR_OPERATOR_ID = "id";
  private static final String ATTR_OPERATOR_NAMESPACE = "namespace";
  private static final String ATTR_OPERATOR_FULL_NAME = "fullName";

  @Override
  public OperateInfo readFrom(Map<String, Object> jobDataMap) throws IllegalArgumentException {
    return readFrom(jobDataMap, null);
  }

  @Override
  public OperateInfo readFrom(Map<String, Object> jobDataMap, String key) throws IllegalArgumentException {
    Assert.assertArgumentNotNull(jobDataMap, "jobDataMap");
    String str = (String) jobDataMap.get(key == null ? getJobDataMapKey() : key);
    return decode(str);
  }

  @Override
  public void writeTo(OperateInfo data, Map<String, Object> jobDataMap) throws IllegalArgumentException {
    writeTo(data, jobDataMap, null);
  }

  @Override
  public void writeTo(OperateInfo data, Map<String, Object> jobDataMap, String key) throws IllegalArgumentException {
    Assert.assertArgumentNotNull(jobDataMap, "jobDataMap");
    String ripeKey = key == null ? getJobDataMapKey() : key;
    if (data == null) {
      jobDataMap.remove(ripeKey);
    } else {
      jobDataMap.put(ripeKey, encode(data));
    }
  }

  /**
   * 返回默认的作业数据映射表键。
   */
  protected String getJobDataMapKey() {
    return JOB_DATA_MAP_KEY;
  }

  private static String encode(OperateInfo info) {
    if (info == null) {
      return null;
    } else {
      return toJson(info).toString();
    }
  }

  private static OperateInfo decode(String str) {
    if (str == null) {
      return null;
    } else {
      return fromJson(new JsonObject(str));
    }
  }

  private static JsonObject toJson(OperateInfo info) {
    if (info == null) {
      return null;
    }
    JsonObject json = new JsonObject();
    if (info.getTime() != null) {
      json.put(ATTR_TIME, info.getTime().getTime());
    }
    if (info.getOperator() != null) {
      JsonObject jsonOperator = new JsonObject();
      jsonOperator.put(ATTR_OPERATOR_ID, info.getOperator().getId());
      jsonOperator.put(ATTR_OPERATOR_NAMESPACE, info.getOperator().getNamespace());
      jsonOperator.put(ATTR_OPERATOR_FULL_NAME, info.getOperator().getFullName());
      json.put(ATTR_OPERATOR, jsonOperator);
    }
    return json;
  }

  private static OperateInfo fromJson(JsonObject json) {
    if (json == null) {
      return null;
    }
    OperateInfo info = new OperateInfo();
    if (json.has(ATTR_TIME)) {
      info.setTime(new Date(json.getLong(ATTR_TIME)));
    } else {
      info.setTime(null);
    }
    if (json.has(ATTR_OPERATOR)) {
      JsonObject jsonOperator = json.getJsonObject(ATTR_OPERATOR);
      Operator operator = new Operator();
      if (jsonOperator.has(ATTR_OPERATOR_ID)) {
        operator.setId(jsonOperator.getString(ATTR_OPERATOR_ID));
      }
      if (jsonOperator.has(ATTR_OPERATOR_NAMESPACE)) {
        operator.setNamespace(jsonOperator.getString(ATTR_OPERATOR_NAMESPACE));
      }
      if (jsonOperator.has(ATTR_OPERATOR_FULL_NAME)) {
        operator.setFullName(jsonOperator.getString(ATTR_OPERATOR_FULL_NAME));
      }
      info.setOperator(operator);
    }
    return info;
  }

}
