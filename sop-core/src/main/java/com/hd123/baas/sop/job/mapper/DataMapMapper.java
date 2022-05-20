/**
 * 版权所有(C)，上海海鼎信息工程股份有限公司，2016，所有权利保留。
 * 
 * 项目名：	dpos-service
 * 文件名：	DataMapMapper.java
 * 模块说明：	
 * 修改历史：
 * 2016年9月18日 - huzexiong - 创建。
 */
package com.hd123.baas.sop.job.mapper;

import java.util.HashMap;
import java.util.Map;

import com.hd123.rumba.commons.codec.CodecUtilsBean;

/**
 * 提供将作业数据对象的序列化/反序列化的功能。
 * 
 * @author huzexiong
 * @since 1.0
 * 
 */
public class DataMapMapper {

  private static final CodecUtilsBean codecBean = new CodecUtilsBean();

  /**
   * 序列化。
   * 
   * @param dataMap
   *          传入null导致返回null。
   */
  public static String serialize(Map<String, Object> dataMap) {
    if (dataMap == null) {
      return null;
    }
    Map<String, Object> prepared = prepareDataMap(dataMap);
    return codecBean.encode(prepared);
  }

  /**
   * 反序列化。
   * 
   * @param str
   *          传入null导致返回null。
   */
  public static Map<String, Object> deserialize(String str) {
    if (str == null) {
      return null;
    }
    return codecBean.decode(str, HashMap.class);
  }

  private static Map<String, Object> prepareDataMap(Map<String, Object> dataMap) {
    Map<String, Object> prepared = new HashMap<String, Object>();
    for (Map.Entry<String, Object> entry : dataMap.entrySet()) {
      String key = entry.getKey();
      Object value = entry.getValue();

      try {
        codecBean.encode(value);
      } catch (Exception e) {
        value = "Fail to serialize!";
      }

      prepared.put(key, value);
    }
    return prepared;
  }

}
