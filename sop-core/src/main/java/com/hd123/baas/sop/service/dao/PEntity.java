/*
 * 版权所有(C)，上海海鼎信息工程股份有限公司，2016，所有权利保留。
 *
 * 项目名：	aroma-commons-jdbc
 * 文件名：	PEntity.java
 * 模块说明：
 * 修改历史：
 * 2016-8-19 - Li Ximing - 创建。
 */
package com.hd123.baas.sop.service.dao;

import com.hd123.devops.ebt.annotation.Column;
import com.hd123.devops.ebt.annotation.Id;
import com.hd123.rumba.commons.biz.entity.Entity;
import com.hd123.rumba.commons.biz.entity.IsEntity;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * 针对抽象类{@link Entity}的持久化对象类，其中包含了表结构定义类。
 *
 * @author Li Ximing
 * @since 1.2
 */
public abstract class PEntity implements Serializable {

  private static final long serialVersionUID = 2938095469446777285L;

  public static final int LENGTH_UUID = 38;

  /** “UUID”字段名。 */
  @Id
  @Column(title = "UUID", name = PEntity.UUID, length = LENGTH_UUID, nullable = false)
  public static final String UUID = "uuid";

  public static class Utils {

    /**
     * 将指定的{@link IsEntity}对象转换为包含字段名及其对应取值的映射表对象。
     *
     * @param entity
     *         禁止传入null。
     * @return 包含字段名及其对应取值的映射表对象。正常情况下不会返回null。
     * @throws IllegalArgumentException
     *         当参数entity为null是抛出。
     */
    public static Map<String, Object> toFieldValues(IsEntity entity) {
      org.springframework.util.Assert.notNull(entity, "entity");
      Map<String, Object> fvm = new HashMap<String, Object>();
      // 用于保存的话，uuid没有时，自动创建之
      if (entity.getUuid() == null) {
        entity.setUuid(java.util.UUID.randomUUID().toString().replace("-", ""));
      }
      fvm.put(UUID, entity.getUuid());
      return fvm;
    }

    /**
     * 将指定的{@link Entity}对象转换为包含字段名及其对应取值的映射表对象。
     *
     * @param entity
     *         禁止传入null。
     * @return 包含字段名及其对应取值的映射表对象。正常情况下不会返回null。
     * @throws IllegalArgumentException
     *         当参数entity为null是抛出。
     */
    public static Map<String, Object> toFieldValues(Entity entity) {
      return toFieldValues((IsEntity) entity);
    }
  }
}
