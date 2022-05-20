/**
 * <p>
 * 项目名: sop-pms-parent
 * 文件名: PPromChannel.java
 * 模块说明:
 * 修改历史:
 * 2020年11月01日 - wushuaijun- 创建
 */
package com.hd123.baas.sop.service.dao.activity;

import com.hd123.baas.sop.service.dao.PEntity;
import com.hd123.baas.sop.service.api.promotion.FavorSharing;
import com.hd123.devops.ebt.annotation.Column;
import com.hd123.devops.ebt.annotation.Entity;
import com.hd123.devops.ebt.annotation.Index;
import com.hd123.devops.ebt.annotation.Table;
import com.hd123.rumba.commons.biz.entity.UCN;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

/**
 * @author wushuaijun
 * @since 1.0
 */
@Entity
@Table(caption = PPromActivityFavorSharing.TABLE_CAPTION, name = PPromActivityFavorSharing.TABLE_NAME, indexes = {
        @Index(name = "idx_prom_activity_favor_sharing_1", columnNames = { PPromActivityFavorSharing.TARGET_UNIT_UUID})

})
public class PPromActivityFavorSharing extends PEntity {

  public static final String TABLE_NAME = "sop_prom_activity_favor_sharing";
  public static final String TABLE_CAPTION = "促销活动-费用承担方";

  @Column(title = "促销活动id", name = PPromActivityFavorSharing.ACTIVITY_UUID, length = 38)
  public static final String ACTIVITY_UUID = "activityUuid";
  @Column(title = "费用承担方uuid", name = PPromActivityFavorSharing.TARGET_UNIT_UUID, length = 38)
  public static final String TARGET_UNIT_UUID = "targetUnitUuid";
  @Column(title = "费用承担方代码", name = PPromActivityFavorSharing.TARGET_UNIT_CODE, length = 64)
  public static final String TARGET_UNIT_CODE = "targetUnitCode";
  @Column(title = "费用承担方名称", name = PPromActivityFavorSharing.TARGET_UNIT_NAME, length = 128)
  public static final String TARGET_UNIT_NAME = "targetUnitName";
  @Column(title = "费用承担方比例", name = PPromActivityFavorSharing.RATE, fieldClass = BigDecimal.class,  precision = 19 ,scale = 4)
  public static final String RATE = "rate";

  public static final String[] COLUMNS = new String[]{
          PEntity.UUID, ACTIVITY_UUID, TARGET_UNIT_UUID, TARGET_UNIT_CODE, TARGET_UNIT_NAME, RATE
  };

  public static Map<String, Object> toFieldValues(String activityUuid, FavorSharing favorSharing) {
    Map<String, Object> fvm = new HashMap<>();
    fvm.put(UUID, java.util.UUID.randomUUID().toString());
    fvm.put(ACTIVITY_UUID, activityUuid);
    fvm.put(TARGET_UNIT_UUID, favorSharing.getTargetUnit().getUuid());
    fvm.put(TARGET_UNIT_CODE, favorSharing.getTargetUnit().getCode());
    fvm.put(TARGET_UNIT_NAME, favorSharing.getTargetUnit().getName());
    fvm.put(RATE, favorSharing.getRate());
    return fvm;
  }

  public static FavorSharing mapRow(ResultSet rs, int rowNum) throws SQLException {
    FavorSharing target = new FavorSharing();
    target.setRate(rs.getBigDecimal(RATE));
    target.setTargetUnit(new UCN(rs.getString(TARGET_UNIT_UUID), rs.getString(TARGET_UNIT_CODE), rs.getString(TARGET_UNIT_NAME)));
    return target;
  }
}
