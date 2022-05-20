package com.hd123.baas.sop.service.dao.rule;

import com.hd123.baas.sop.service.dao.PEntity;
import com.hd123.baas.sop.service.api.promotion.FavorSharing;
import com.hd123.devops.ebt.annotation.Column;
import com.hd123.devops.ebt.annotation.Entity;
import com.hd123.devops.ebt.annotation.Index;
import com.hd123.devops.ebt.annotation.Table;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@Entity
@Table(caption = PPromRuleFavorSharing.TABLE_CAPTION, name = PPromRuleFavorSharing.TABLE_NAME, indexes = {
        @Index(name = "idx_prom_rule_favor_sharing_1", columnNames = { PPromRuleFavorSharing.RULE_UUID})
})
public class PPromRuleFavorSharing extends PEntity {
  public static final String TABLE_NAME = "sop_prom_rule_favor_sharing";
  public static final String TABLE_CAPTION = "促销规则费用承担方关联表";

  @Column(title = "促销规则uuid", name = PPromRuleFavorSharing.RULE_UUID, length = 38)
  public static final String RULE_UUID = "ruleUuid";

  @Column(title = "费用承担方uuid", name = PPromRuleFavorSharing.TARGET_UNIT_UUID, length = 38)
  public static final String TARGET_UNIT_UUID = "targetUnitUuid";
  @Column(title = "费用承担方代码", name = PPromRuleFavorSharing.TARGET_UNIT_CODE, length = 64)
  public static final String TARGET_UNIT_CODE = "targetUnitCode";
  @Column(title = "费用承担方名称", name = PPromRuleFavorSharing.TARGET_UNIT_NAME, length = 128)
  public static final String TARGET_UNIT_NAME = "targetUnitName";
  @Column(title = "费用承担方承担比例", name = PPromRuleFavorSharing.RATE, fieldClass = BigDecimal.class, precision = 19 ,scale = 4)
  public static final String RATE = "rate";

  public static final String[] COLUMNS = new String[]{UUID, RULE_UUID, TARGET_UNIT_UUID, TARGET_UNIT_CODE, TARGET_UNIT_NAME, RATE,};

  public static Map<String, Object> toFieldValues(String ruleUuid, FavorSharing favorSharing) {
    Map<String, Object> fvm = new HashMap<>();
    fvm.put(UUID, java.util.UUID.randomUUID().toString().replace("-", ""));
    fvm.put(RULE_UUID, ruleUuid);
    fvm.put(RATE, favorSharing.getRate());
    if (favorSharing.getTargetUnit() != null) {
      fvm.put(TARGET_UNIT_UUID, favorSharing.getTargetUnit().getUuid());
      fvm.put(TARGET_UNIT_CODE, favorSharing.getTargetUnit().getCode());
      fvm.put(TARGET_UNIT_NAME, favorSharing.getTargetUnit().getName());
    }
    return fvm;
  }
}
