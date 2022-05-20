package com.hd123.baas.sop.service.dao.rule;

import com.hd123.baas.sop.service.dao.PEntity;
import com.hd123.devops.ebt.annotation.Column;
import com.hd123.devops.ebt.annotation.Entity;
import com.hd123.devops.ebt.annotation.Index;
import com.hd123.devops.ebt.annotation.Table;
import com.hd123.rumba.commons.biz.entity.UCN;

import java.util.HashMap;
import java.util.Map;

@Entity
@Table(caption = PPromRuleJoinUnits.TABLE_CAPTION, name = PPromRuleJoinUnits.TABLE_NAME, indexes = {
        @Index(name = "idx_prom_rule_store_1", columnNames = { PPromRuleJoinUnits.RULE_UUID})
})
public class PPromRuleJoinUnits extends PEntity {
    public static final String TABLE_NAME = "sop_prom_rule_join_unit";
    public static final String TABLE_CAPTION = "促销规则门店关联表";

    @Column(title = "促销规则uuid", name = PPromRuleJoinUnits.RULE_UUID, length = 38)
    public static final String RULE_UUID = "ruleUuid";

    @Column(title = "门店uuid", name = PPromRuleJoinUnits.JOIN_UNIT_UUID, length = 38)
    public static final String JOIN_UNIT_UUID = "joinUnitUuid";
    @Column(title = "门店代码", name = PPromRuleJoinUnits.JOIN_UNIT_CODE, length = 64)
    public static final String JOIN_UNIT_CODE = "joinUnitCode";
    @Column(title = "门店名称", name = PPromRuleJoinUnits.JOIN_UNIT_NAME, length = 128)
    public static final String JOIN_UNIT_NAME = "joinUnitName";

    public static final String[] COLUMNS = new String[]{UUID, RULE_UUID, JOIN_UNIT_UUID, JOIN_UNIT_CODE, JOIN_UNIT_NAME,};

    public static Map<String, Object> toFieldValues(String ruleUuid, UCN store) {
        Map<String, Object> fvm = new HashMap<>();
        fvm.put(UUID, java.util.UUID.randomUUID().toString().replace("-", ""));
        fvm.put(RULE_UUID, ruleUuid);
        if (store != null) {
            fvm.put(JOIN_UNIT_UUID, store.getUuid());
            fvm.put(JOIN_UNIT_CODE, store.getCode());
            fvm.put(JOIN_UNIT_NAME, store.getName());
        }
        return fvm;
    }
}
