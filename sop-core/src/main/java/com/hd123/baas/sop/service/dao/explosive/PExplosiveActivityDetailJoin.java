package com.hd123.baas.sop.service.dao.explosive;

import com.hd123.baas.sop.service.api.pms.explosive.ExplosiveActivityDetailJoin;
import com.hd123.baas.sop.service.dao.PEntity;
import com.hd123.devops.ebt.annotation.Column;
import com.hd123.devops.ebt.annotation.Entity;
import com.hd123.devops.ebt.annotation.Index;
import com.hd123.devops.ebt.annotation.Table;
import com.hd123.rumba.commons.biz.entity.UCN;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @author wushuaijun
 * @since 1.0
 */
@Entity
@Table(caption = PExplosiveActivityDetailJoin.TABLE_CAPTION, name = PExplosiveActivityDetailJoin.TABLE_NAME, indexes = {
        @Index(name = "idx_explosive_activity_detail_join_1", columnNames = PExplosiveActivityDetailJoin.STORE_UUID),
        @Index(name = "idx_explosive_activity_detail_join_2", columnNames = PExplosiveActivityDetailJoin.DETAIL_UUID)
})
public class PExplosiveActivityDetailJoin extends PEntity {

  public static final String TABLE_NAME = "sop_explosive_activity_detail_join";
  public static final String TABLE_CAPTION = "爆品预定门店明细";

  @Column(title = "租户", name = PExplosiveActivityDetailJoin.TENANT, length = 38)
  public static final String TENANT = "tenant";
  @Column(title = "爆品预定记录uuid", name = PExplosiveActivityDetailJoin.DETAIL_UUID, length = 38)
  public static final String DETAIL_UUID = "detailUuid";

  @Column(title = "门店uuid", name = PExplosiveActivityDetailJoin.STORE_UUID, length = 38)
  public static final String STORE_UUID = "joinUnitUuid";
  @Column(title = "门店Code", name = PExplosiveActivityDetailJoin.STORE_CODE, length = 64)
  public static final String STORE_CODE = "joinUnitCode";
  @Column(title = "门店名称", name = PExplosiveActivityDetailJoin.STORE_NAME, length = 128)
  public static final String STORE_NAME = "joinUnitName";

  @Column(title = "配货规格", name = PExplosiveActivityDetailJoin.ALC_QPC, fieldClass = BigDecimal.class, precision = 19, scale = 4)
  public static final String ALC_QPC = "alcQpc";
  @Column(title = "配货单位", name = PExplosiveActivityDetailJoin.ALC_UNIT, length = 32)
  public static final String ALC_UNIT = "alcUnit";
  @Column(title = "预定日期", name = PExplosiveActivitySignJoinLine.SIGN_DATE, fieldClass = Date.class)
  public static final String SIGN_DATE = "signDate";
  @Column(title = "订货量", name = PExplosiveActivityDetailJoin.SIGN_QTY, fieldClass = BigDecimal.class, precision = 19, scale = 4)
  public static final String SIGN_QTY = "signQty";

  public static final String[] COLUMNS = { PEntity.UUID, STORE_UUID, TENANT,
          STORE_CODE, STORE_NAME, ALC_QPC, ALC_UNIT, SIGN_QTY, DETAIL_UUID, SIGN_DATE};

  public static Map<String, Object> toFieldValues(String uuid, ExplosiveActivityDetailJoin detailJoin) {
    Map<String, Object> fvm = new HashMap<>();
    fvm.put(UUID, java.util.UUID.randomUUID().toString());
    fvm.put(DETAIL_UUID, uuid);
    fvm.put(TENANT, detailJoin.getTenant());
    fvm.put(ALC_QPC, detailJoin.getAlcQpc());
    fvm.put(ALC_UNIT, detailJoin.getAlcUnit());
    fvm.put(SIGN_QTY, detailJoin.getSignQty());
    fvm.put(SIGN_DATE, detailJoin.getSignDate());
    if (detailJoin.getStore() != null) {
      fvm.put(STORE_UUID, detailJoin.getStore().getUuid());
      fvm.put(STORE_CODE, detailJoin.getStore().getCode());
      fvm.put(STORE_NAME, detailJoin.getStore().getName());
    }
    return fvm;
  }

  public static ExplosiveActivityDetailJoin mapRow(ResultSet rs, int rowNum) throws SQLException {
    ExplosiveActivityDetailJoin target = new ExplosiveActivityDetailJoin();
    target.setTenant(rs.getString(TENANT));
    target.setSignDate(rs.getTimestamp(SIGN_DATE));
    target.setSignQty(rs.getBigDecimal(SIGN_QTY));
    target.setDetailUuid(rs.getString(DETAIL_UUID));
    target.setStore(new UCN(rs.getString(STORE_UUID), rs.getString(STORE_CODE), rs.getString(STORE_NAME)));
    target.setAlcQpc(rs.getBigDecimal(ALC_QPC));
    target.setAlcUnit(rs.getString(ALC_UNIT));
    return target;
  }
}
