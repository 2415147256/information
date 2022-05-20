/**
 * 版权所有(C)，上海海鼎信息工程股份有限公司，2015，所有权利保留。
 * <p>
 * 项目名：	soms-core
 * 文件名：	PStandardEntity.java
 * 模块说明：
 * 修改历史：
 * 2015-12-4 - zhangyanbo - 创建。
 */
package com.hd123.baas.sop.service.dao;

import com.hd123.devops.ebt.annotation.Column;
import com.hd123.rumba.commons.biz.entity.Entity;
import com.hd123.rumba.commons.biz.entity.IsEntity;
import com.hd123.rumba.commons.biz.entity.OperateInfo;
import com.hd123.rumba.commons.biz.entity.Operator;
import com.hd123.rumba.commons.biz.entity.StandardEntity;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.Map;

/**
 * @author zhangyanbo
 */
public abstract class PStandardEntity extends PEntity {

  @Column(title = "创建人id", name = PStandardEntity.CREATOR_ID, length = 40)
  public static final String CREATOR_ID = "creatorId";
  @Column(title = "创建人名称", name = PStandardEntity.CREATOR_NAME, length = 80)
  public static final String CREATOR_NAME = "creatorName";
  @Column(title = "创建人NS", name = PStandardEntity.CREATOR_NAMESPACE, length = 64)
  public static final String CREATOR_NAMESPACE = "creatorNS";
  @Column(title = "创建时间", name = PStandardEntity.CREATE_INFO_TIME, fieldClass = Date.class)
  public static final String CREATE_INFO_TIME = "created";
  @Column(title = "修改人", name = PStandardEntity.LAST_MODIFIER_ID, length = 40)
  public static final String LAST_MODIFIER_ID = "lastModifierId";
  @Column(title = "修改人名称", name = PStandardEntity.LAST_MODIFIER_NAME, length = 80)
  public static final String LAST_MODIFIER_NAME = "lastModifierName";
  @Column(title = "修改人NS", name = PStandardEntity.LAST_MODIFIER_NAMESPACE, length = 64)
  public static final String LAST_MODIFIER_NAMESPACE = "lastModifierNS";
  @Column(title = "修改时间", name = PStandardEntity.LAST_MODIFY_INFO_TIME, fieldClass = Date.class)
  public static final String LAST_MODIFY_INFO_TIME = "lastModified";

  public static final String[] COLUMNS = new String[]{
          UUID,
          CREATOR_ID, CREATOR_NAME, CREATOR_NAMESPACE, CREATE_INFO_TIME,
          LAST_MODIFIER_ID, LAST_MODIFIER_NAME, LAST_MODIFIER_NAMESPACE, LAST_MODIFY_INFO_TIME
  };

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
    public static Map<String, Object> toFieldValues(Entity entity) {
      Map<String, Object> fvm = PEntity.Utils.toFieldValues(entity);
      if (entity instanceof StandardEntity) {
        if (((StandardEntity) entity).getCreateInfo() != null) {
          if (((StandardEntity) entity).getCreateInfo().getOperator() != null) {
            fvm.put(CREATOR_ID, ((StandardEntity) entity).getCreateInfo().getOperator().getId());
            fvm.put(CREATOR_NAME, ((StandardEntity) entity).getCreateInfo().getOperator().getFullName());
            fvm.put(CREATOR_NAMESPACE, ((StandardEntity) entity).getCreateInfo().getOperator().getNamespace());
          }
          fvm.put(CREATE_INFO_TIME, ((StandardEntity) entity).getCreateInfo().getTime());
        }
        if (((StandardEntity) entity).getLastModifyInfo() != null) {
          if (((StandardEntity) entity).getLastModifyInfo().getOperator() != null) {
            fvm.put(LAST_MODIFIER_ID, ((StandardEntity) entity).getLastModifyInfo().getOperator().getId());
            fvm.put(LAST_MODIFIER_NAME, ((StandardEntity) entity).getLastModifyInfo().getOperator().getFullName());
            fvm.put(LAST_MODIFIER_NAMESPACE, ((StandardEntity) entity).getLastModifyInfo().getOperator().getNamespace());
          }
          fvm.put(LAST_MODIFY_INFO_TIME, ((StandardEntity) entity).getLastModifyInfo().getTime());
        }
      }
      return fvm;
    }

    public static <T extends StandardEntity> void mapRow(ResultSet rs, T target) throws SQLException {
      target.setUuid(rs.getString(UUID));
      target.setCreateInfo(new OperateInfo(
              rs.getTimestamp(CREATE_INFO_TIME),
              new Operator(rs.getString(CREATOR_NAMESPACE), rs.getString(CREATOR_ID), rs.getString(CREATOR_NAME))));
      target.setLastModifyInfo(new OperateInfo(
              rs.getTimestamp(LAST_MODIFY_INFO_TIME),
              new Operator(rs.getString(LAST_MODIFIER_NAMESPACE), rs.getString(LAST_MODIFIER_ID), rs.getString(LAST_MODIFIER_NAME))));
    }
  }
}
