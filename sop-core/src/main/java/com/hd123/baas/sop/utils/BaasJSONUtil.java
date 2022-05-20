package com.hd123.baas.sop.utils;

/**
 * 版权所有(C)，上海海鼎信息工程股份有限公司，2019，所有权利保留。
 * <p>
 * 项目名：	workspace
 * 文件名：	JsonUtil.java
 * 模块说明：
 * 修改历史：
 * 2019年03月05日 - yanghaixiao - 创建。
 */

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.TreeNode;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.NullNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.POJONode;
import com.qianfan123.baas.common.BaasException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.Assert;

import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;

/**
 * @author yanghaixiao
 **/
@Slf4j
public class BaasJSONUtil {
  private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

  static {
    SimpleDateFormat smt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    OBJECT_MAPPER.setDateFormat(smt);
    OBJECT_MAPPER.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
    OBJECT_MAPPER.setSerializationInclusion(JsonInclude.Include.NON_NULL);
    // OBJECT_MAPPER.setSerializationInclusion(JsonInclude.Include.NON_DEFAULT);
  }

  public static String safeToJson(Object object) throws BaasException {
    if (object == null || object instanceof NullNode) {
      return null;
    } else {
      try {
        return OBJECT_MAPPER.writeValueAsString(object);
      } catch (Exception e) {
        log.error("序列化错误", e);
        throw new BaasException(e);
      }
    }
  }

  public static <T> T safeToObject(InputStream inputStream, Class<T> clazz) throws BaasException {
    Assert.notNull(clazz, "clazz");
    if (inputStream == null) {
      return null;
    } else {
      try {
        return OBJECT_MAPPER.readValue(inputStream, clazz);
      } catch (IOException e) {
        log.error("反序列化<{}>错误", clazz.getName(), e);
        throw new BaasException(e);
      }
    }
  }

  public static <T> T safeToObject(String json, Class<T> clazz) throws BaasException {
    Assert.notNull(clazz, "clazz");
    if (StringUtils.isBlank(json)) {
      return null;
    } else {
      try {
        return OBJECT_MAPPER.readValue(json, clazz);
      } catch (IOException e) {
        log.error("字符串<{}>反序列化<{}>错误", json, clazz.getName(), e);
        throw new BaasException(e);
      }
    }
  }

  protected static <T> T safeToObject(TreeNode treeNode, Class<T> clazz) throws BaasException {
    Assert.notNull(clazz, "clazz");
    if (treeNode == null || treeNode instanceof NullNode) {
      return null;
    } else {
      try {
        if (treeNode instanceof POJONode) {
          return (T) ((POJONode) treeNode).getPojo();
        }
        return OBJECT_MAPPER.treeToValue(treeNode, clazz);
      } catch (IOException e) {
        log.error("TreeNode<{}>反序列化<{}>错误", treeNode, clazz.getName(), e);
        throw new BaasException(e);
      }
    }
  }

  protected static <T> T safeToObject(TreeNode treeNode, TypeReference<T> typeReference) throws BaasException {
    Assert.notNull(typeReference, "typeReference");
    if (treeNode == null || treeNode instanceof NullNode) {
      return null;
    } else {
      try {
        if (treeNode instanceof POJONode) {
          return (T) ((POJONode) treeNode).getPojo();
        }
        return OBJECT_MAPPER.readValue(OBJECT_MAPPER.treeAsTokens(treeNode), typeReference);
      } catch (IOException e) {
        log.error("TreeNode<{}>反序列化<{}>错误", treeNode, typeReference.getType().getTypeName(), e);
        throw new BaasException(e);
      }
    }
  }

  /**
   * 从大字段中读取数据。只读取第一层
   *
   */
  public static String getFormJson(String json, String key) throws BaasException {
    if (StringUtils.isBlank(json)) {
      return null;
    }
    Map<String, Object> objectMap;
    try {
      objectMap = OBJECT_MAPPER.readValue(json, Map.class);
    } catch (IOException e) {
      log.error("读取字段错误", e);
      throw new BaasException(e);
    }
    Object o = objectMap.get(key);
    if (o == null) {
      return null;
    }
    return o.toString();
  }

  public static String sort(String json) throws BaasException {
    if (json == null) {
      return null;
    }
    json = json.trim();
    try {
      if (json.startsWith("[")) {
        return OBJECT_MAPPER.writeValueAsString(sortArrayJson(json));
      } else if (json.startsWith("{")) {
        return OBJECT_MAPPER.writeValueAsString(sortMapJson(json));
      }
    } catch (IOException e) {
      log.error("排序<{}>错误", json, e);
    }
    return json;
  }

  public static LinkedHashMap<String, Object> sortMapJson(String json) throws BaasException {
    if (StringUtils.isBlank(json)) {
      return null;
    }
    try {
      HashMap<String, Object> hashMap = OBJECT_MAPPER.readValue(json, HashMap.class);
      return sortMap(hashMap);
    } catch (IOException e) {
      log.error("字符串<{}>反序列化MAP错误", json, e);
      throw new BaasException(e);
    }
  }

  public static ArrayList<Object> sortArrayJson(String json) throws BaasException {
    if (StringUtils.isBlank(json)) {
      return null;
    }
    try {
      List<Object> list = OBJECT_MAPPER.readValue(json, List.class);
      return sortList(list);
    } catch (IOException e) {
      log.error("字符串<{}>反序列化LIST错误", json, e);
      throw new BaasException(e);
    }
  }

  public static <T> T safeToObject(String json, TypeReference<T> typeReference) throws BaasException {
    Assert.notNull(typeReference, "typeReference");
    if (StringUtils.isBlank(json)) {
      return null;
    } else {
      try {
        return OBJECT_MAPPER.readValue(json, typeReference);
      } catch (IOException e) {
        log.error("字符串<{}>反序列化<{}>错误", json, typeReference.getType().getTypeName(), e);
        throw new BaasException(e);
      }
    }
  }

  protected static ObjectNode createObjectNode() {
    return OBJECT_MAPPER.createObjectNode();
  }

  private static LinkedHashMap<String, Object> sortMap(Map<String, Object> map) {
    if (map == null) {
      return null;
    }
    LinkedHashMap<String, Object> result = new LinkedHashMap<>();
    TreeSet<String> treeSet = new TreeSet<>(map.keySet());
    for (String key : treeSet) {
      Object o = map.get(key);
      if (o == null) {
        continue;
      } else if (o instanceof Map) {
        result.put(key, sortMap((Map) o));
      } else if (o instanceof List) {
        result.put(key, sortList((List) o));
      } else {
        result.put(key, o);
      }
    }
    return result;
  }

  private static ArrayList<Object> sortList(List<Object> list) {
    if (list == null) {
      return null;
    }
    ArrayList result = new ArrayList<>();
    for (Object o : list) {
      if (o == null) {
        continue;
      } else if (o instanceof Map) {
        result.add(sortMap((Map<String, Object>) o));
      } else if (o instanceof List) {
        result.add(sortList((List<Object>) o));
      } else {
        result.add(o);
      }
    }
    Collections.sort(result);
    return result;
  }
}
