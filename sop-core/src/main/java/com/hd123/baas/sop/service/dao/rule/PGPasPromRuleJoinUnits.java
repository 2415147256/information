package com.hd123.baas.sop.service.dao.rule; /**
 * <p>
 * 项目名: sop-pms-parent
 * 文件名: PromActvivityStore.java
 * 模块说明:
 * 修改历史:
 * 2020年10月28日 - wushuaijun- 创建
 */

import com.hd123.baas.sop.service.dao.PEntity;
import com.hd123.baas.sop.service.api.promotion.PromotionJoinUnits;
import com.hd123.devops.ebt.annotation.Column;
import com.hd123.devops.ebt.annotation.Entity;
import com.hd123.devops.ebt.annotation.Index;
import com.hd123.devops.ebt.annotation.Table;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

/**
 * @author wushuaijun
 * @since 1.0
 */

@Entity
@Table(caption = PGPasPromRuleJoinUnits.TABLE_CAPTION, name = PGPasPromRuleJoinUnits.TABLE_NAME, indexes = {
        @Index(name = "idx_g_pas_prom_rule_join_units_1", columnNames = PGPasPromRuleJoinUnits.RULE_UUID),
})
public class PGPasPromRuleJoinUnits extends PEntity {
  public static final String TABLE_NAME = "sop_gpas_prom_rule_join_units";
  public static final String TABLE_CAPTION = "批发订货促销规则单-适用门店关联表";

  @Column(title = "批发订货促销规则单uuid", name = PGPasPromRuleJoinUnits.RULE_UUID, length = 38)
  public static final String RULE_UUID = "ruleUuid";
  @Column(title = "门店uuid", name = PGPasPromRuleJoinUnits.JOIN_UNIT_UUID, length = 38)
  public static final String JOIN_UNIT_UUID = "joinUnitUuid";
  @Column(title = "门店Code", name = PGPasPromRuleJoinUnits.JOIN_UNIT_CODE, length = 64)
  public static final String JOIN_UNIT_CODE = "joinUnitCode";
  @Column(title = "门店名称", name = PGPasPromRuleJoinUnits.JOIN_UNIT_NAME, length = 128)
  public static final String JOIN_UNIT_NAME = "joinUnitName";
  @Column(title = "门店类型", name = PGPasPromRuleJoinUnits.JOIN_UNIT_TYPE, length = 20)
  public static final String JOIN_UNIT_TYPE = "joinUnitType";

  public static final String[] COLUMNS = { PEntity.UUID, RULE_UUID, JOIN_UNIT_UUID, JOIN_UNIT_CODE, JOIN_UNIT_NAME, JOIN_UNIT_TYPE};

  public static Map<String, Object> toFieldValues(String uuid, PromotionJoinUnits.JoinUnit entity) {
    Map<String, Object> fvm = new HashMap<>();
    fvm.put(RULE_UUID, uuid);
    fvm.put(UUID, java.util.UUID.randomUUID().toString());
    fvm.put(JOIN_UNIT_UUID, entity.getUuid());
    fvm.put(JOIN_UNIT_CODE, entity.getCode());
    fvm.put(JOIN_UNIT_NAME, entity.getName());
    fvm.put(JOIN_UNIT_TYPE, entity.getJoinUnitType());
    return fvm;
  }

  public static PromotionJoinUnits.JoinUnit mapRow(ResultSet rs, int rowNum) throws SQLException {
    PromotionJoinUnits.JoinUnit target = new PromotionJoinUnits.JoinUnit();
    target.setCode(rs.getString(JOIN_UNIT_CODE));
    target.setName(rs.getString(JOIN_UNIT_NAME));
    target.setUuid(rs.getString(JOIN_UNIT_UUID));
    target.setJoinUnitType(rs.getString(JOIN_UNIT_TYPE));
    return target;

  }
}
