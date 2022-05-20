package com.hd123.baas.sop.service.dao.template;

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
 * @author zhuangwenting
 * @since 1.0
 */
@Entity
@Table(caption = PPromTemplateGrantUnits.TABLE_CAPTION, name = PPromTemplateGrantUnits.TABLE_NAME, indexes = {
        @Index(name = "idx_prom_template_grant_units_1", columnNames = { PPromTemplateGrantUnits.TEMPLATE_UUID})
})
public class PPromTemplateGrantUnits extends PEntity {
  public static final String TABLE_NAME = "sop_prom_template_grant_units";
  public static final String TABLE_CAPTION = "促销模板-门店关联表";

  @Column(title = "模板uuid", name = PPromTemplateGrantUnits.TEMPLATE_UUID, length = 40)
  public static final String TEMPLATE_UUID = "templateUuid";

  @Column(title = "门店uuid", name = PPromTemplateGrantUnits.GRANT_UNIT_UUID, length = 40)
  public static final String GRANT_UNIT_UUID = "grantUnitUuid";
  @Column(title = "门店代码", name = PPromTemplateGrantUnits.GRANT_UNIT_CODE, length = 64)
  public static final String GRANT_UNIT_CODE = "grantUnitCode";
  @Column(title = "门店名称", name = PPromTemplateGrantUnits.GRANT_UNIT_NAME, length = 128)
  public static final String GRANT_UNIT_NAME = "grantUnitName";

  public static final String[] COLUMNS = new String[]{UUID, GRANT_UNIT_UUID, GRANT_UNIT_CODE, GRANT_UNIT_NAME, TEMPLATE_UUID};

  public static Map<String, Object> toFieldValues(String uuid, UCN entity) {
    Map<String, Object> fvm = new HashMap<>();
    fvm.put(UUID, java.util.UUID.randomUUID().toString().replace("-", ""));
    fvm.put(TEMPLATE_UUID, uuid);
    if (entity != null) {
      fvm.put(GRANT_UNIT_UUID, entity.getUuid());
      fvm.put(GRANT_UNIT_CODE, entity.getCode());
      fvm.put(GRANT_UNIT_NAME, entity.getName());
    }
    return fvm;
  }

  public static PromotionJoinUnits.JoinUnit mapRow(ResultSet rs, int i) throws SQLException {
    PromotionJoinUnits.JoinUnit target = new PromotionJoinUnits.JoinUnit();
    target.setUuid(rs.getString(GRANT_UNIT_UUID));
    target.setCode(rs.getString(GRANT_UNIT_CODE));
    target.setName(rs.getString(GRANT_UNIT_NAME));
    return target;
  }
}
