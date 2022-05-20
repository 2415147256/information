package com.hd123.baas.sop.service.dao.explosive;

import com.hd123.baas.sop.service.dao.PEntity;
import com.hd123.baas.sop.service.api.promotion.PromotionJoinUnits;
import com.hd123.devops.ebt.annotation.Column;
import com.hd123.devops.ebt.annotation.Entity;
import com.hd123.devops.ebt.annotation.Index;
import com.hd123.devops.ebt.annotation.Table;
import com.hd123.rumba.commons.biz.entity.UCN;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

/**
 * @author wushuaijun
 * @since 1.0
 */

@Entity
@Table(caption = PExplosiveActivityJoinUnits.TABLE_CAPTION, name = PExplosiveActivityJoinUnits.TABLE_NAME, indexes = {
        @Index(name = "idx_explosive_activity_join_units_1", columnNames = PExplosiveActivityJoinUnits.ACTIVITY_UUID),

})
public class PExplosiveActivityJoinUnits extends PEntity {
  public static final String TABLE_NAME = "sop_explosive_activity_join_units";
  public static final String TABLE_CAPTION = "爆品活动-适用门店关联表";

  @Column(title = "促销活动uuid", name = PExplosiveActivityJoinUnits.ACTIVITY_UUID, length = 38)
  public static final String ACTIVITY_UUID = "activityUuid";
  @Column(title = "门店uuid", name = PExplosiveActivityJoinUnits.JOIN_UNIT_UUID, length = 38)
  public static final String JOIN_UNIT_UUID = "joinUnitUuid";
  @Column(title = "门店Code", name = PExplosiveActivityJoinUnits.JOIN_UNIT_CODE, length = 64)
  public static final String JOIN_UNIT_CODE = "joinUnitCode";

  @Column(title = "名称", name = PExplosiveActivityJoinUnits.JOIN_UNIT_NAME, length = 128)
  public static final String JOIN_UNIT_NAME = "joinUnitName";

  public static final String[] COLUMNS = { PEntity.UUID, ACTIVITY_UUID, JOIN_UNIT_UUID, JOIN_UNIT_CODE, JOIN_UNIT_NAME};

  public static Map<String, Object> toFieldValues(String uuid, UCN entity) {
    Map<String, Object> fvm = new HashMap<>();
    fvm.put(ACTIVITY_UUID, uuid);
    fvm.put(UUID, java.util.UUID.randomUUID().toString());
    fvm.put(JOIN_UNIT_UUID, entity.getUuid());
    fvm.put(JOIN_UNIT_CODE, entity.getCode());
    fvm.put(JOIN_UNIT_NAME, entity.getName());
    return fvm;
  }

  public static PromotionJoinUnits.JoinUnit mapRow(ResultSet rs, int rowNum) throws SQLException {
    PromotionJoinUnits.JoinUnit target = new PromotionJoinUnits.JoinUnit();
    target.setCode(rs.getString(JOIN_UNIT_CODE));
    target.setName(rs.getString(JOIN_UNIT_NAME));
    target.setUuid(rs.getString(JOIN_UNIT_UUID));
    return target;
  }
}
