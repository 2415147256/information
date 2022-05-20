package com.hd123.baas.sop.service.dao;

import com.hd123.rumba.commons.jdbc.entity.PVersionedEntity;
import com.hd123.rumba.commons.jdbc.entity.Schemas;

/**
 * @author W.J.H.7
 * @since 1.1.0
 **/
public abstract class PThinEntity extends PVersionedEntity {
  /** “创建时间”字段名。 */
  public static String CREATE_INFO_TIME = Schemas.StandardEntity.CREATE_INFO_TIME;

  /** “最后修改时间”字段名。 */
  public static String LAST_MODIFY_INFO_TIME = Schemas.StandardEntity.LAST_MODIFY_INFO_TIME;

  /**
   * 取得包含所有来自{@link PThinEntity}的字段名的数组。
   */
  public static String[] allColumns() {
    return toColumnArray(PVersionedEntity.allColumns(), //
        CREATE_INFO_TIME, //
        LAST_MODIFY_INFO_TIME);
  }
}
