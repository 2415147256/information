package com.hd123.baas.sop.service.dao.template;

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
 * @author zhuangwenting
 * @since 1.0
 */
@Entity
@Table(caption = PPromTemplateFavorSharing.TABLE_CAPTION, name = PPromTemplateFavorSharing.TABLE_NAME, indexes = {
        @Index(name = "idx_prom_template_favor_sharing_1", columnNames = { PPromTemplateFavorSharing.TEMPLATE_UUID})
})
public class PPromTemplateFavorSharing extends PEntity {
  public static final String TABLE_NAME = "sop_prom_template_favor_sharing";
  public static final String TABLE_CAPTION = "促销模板-促销费用承担关联表";

  @Column(title = "模板uuid", name = PPromTemplateFavorSharing.TEMPLATE_UUID, length = 40)
  public static final String TEMPLATE_UUID = "templateUuid";

  @Column(title = "促销费用承担uuid", name = PPromTemplateFavorSharing.TARGET_UNIT_UUID, length = 40)
  public static final String TARGET_UNIT_UUID = "targetUnitUuid";
  @Column(title = "促销费用承担代码", name = PPromTemplateFavorSharing.TARGET_UNIT_CODE, length = 64)
  public static final String TARGET_UNIT_CODE = "targetUnitCode";
  @Column(title = "促销费用承担名称", name = PPromTemplateFavorSharing.TARGET_UNIT_NAME, length = 128)
  public static final String TARGET_UNIT_NAME = "targetUnitName";
  @Column(title = "承担比例", name = PPromTemplateFavorSharing.RATE, fieldClass = BigDecimal.class, precision = 19 ,scale = 4)
  public static final String RATE = "rate";

  public static final String[] COLUMNS = new String[]{UUID, TARGET_UNIT_UUID, TARGET_UNIT_CODE, TARGET_UNIT_NAME, TEMPLATE_UUID, RATE};

  public static Map<String, Object> toFieldValues(String uuid, FavorSharing entity) {
    Map<String, Object> fvm = new HashMap<>();
    fvm.put(UUID, java.util.UUID.randomUUID().toString().replace("-", ""));
    fvm.put(TEMPLATE_UUID, uuid);
    if (entity.getTargetUnit() != null) {
      fvm.put(TARGET_UNIT_UUID, entity.getTargetUnit().getUuid());
      fvm.put(TARGET_UNIT_CODE, entity.getTargetUnit().getCode());
      fvm.put(TARGET_UNIT_NAME, entity.getTargetUnit().getName());
    }
    fvm.put(RATE, entity.getRate());
    return fvm;
  }

  public static FavorSharing mapRow(ResultSet rs, int i) throws SQLException {
    FavorSharing target = new FavorSharing();
    target.setRate(rs.getBigDecimal(RATE));
    target.setTargetUnit(new UCN(rs.getString(TARGET_UNIT_UUID), rs.getString(TARGET_UNIT_CODE), rs.getString(TARGET_UNIT_NAME)));
    return target;
  }
}
