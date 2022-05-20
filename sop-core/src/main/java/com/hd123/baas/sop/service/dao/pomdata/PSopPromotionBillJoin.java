package com.hd123.baas.sop.service.dao.pomdata;

import com.hd123.baas.sop.service.api.pomdata.SopPromotionBillJoin;
import com.hd123.devops.ebt.annotation.Column;
import com.hd123.rumba.commons.biz.entity.UCN;
import com.hd123.rumba.commons.lang.StringUtil;
import com.hd123.spms.commons.bean.OperateInfo;
import com.hd123.spms.manager.dao.bill.PPromotionBillJoin;
import com.hd123.spms.service.bill.JoinUnitRange;
import com.hd123.spms.service.bill.PromotionBill;
import com.hd123.spms.service.bill.PromotionBillJoin;
import org.apache.commons.lang3.ArrayUtils;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;

public class PSopPromotionBillJoin extends PPromotionBillJoin {
  @Column(title = "周期促销标识", name = PSopPromotionBillJoin.STORE_TAG, length = 20)
  public static final String STORE_TAG = "storeTag";

  public static final String[] COLUMNS = ArrayUtils.addAll(
          ArrayUtils.addAll(PPromotionBillJoin.COLUMNS, STORE_TAG));

  public static SopPromotionBillJoin mapRow2(ResultSet rs, int rowNum) throws SQLException {
    SopPromotionBillJoin target = new SopPromotionBillJoin();
    target.setUuid(rs.getString(UUID));
    target.setBill(new PromotionBill(rs.getString(BILL_UUID)));
    target.setJoinUnit(new UCN(rs.getString(JOIN_ORG_UUID), rs.getString(JOIN_ORG_CODE), rs.getString(JOIN_ORG_NAME)));
    target.setJoinInfo(new OperateInfo(rs.getTimestamp(JOIN_TIME), rs.getString(JOIN_OPERATOR)));
    target.setStart(rs.getTimestamp(START));
    target.setForwardEndDays(StringUtil.toInteger(rs.getString(FORWARD_END_DAYS)));
    target.setForwardStartDays(StringUtil.toInteger(rs.getString(FORWARD_START_DAYS)));
    target.setDemo(rs.getBoolean(DEMO));
    target.setEffectDate(rs.getTimestamp(EFFECT_DATE));
    target.setUnitRange(StringUtil.toEnum(rs.getString(UNIT_RANGE), JoinUnitRange.class));

    target.setProvince(rs.getString(PROVINCE));
    target.setCity(rs.getString(CITY));
    target.setProperty(StringUtil.toInteger(rs.getString(PROPERTY)));
    target.setStoreTag(rs.getString(STORE_TAG));

    String areaUuuid = rs.getString(AREA_UUID);
    if (StringUtil.isNullOrBlank(areaUuuid) == false) {
      target.setArea(new UCN(areaUuuid, rs.getString(AREA_CODE), rs.getString(AREA_NAME)));
    }
    return target;
  }

  public static Map<String, Object> toFieldValues(PromotionBillJoin entity) {
    Map<String, Object> fvm = PPromotionBillJoin.toFieldValues(entity);
    if (entity instanceof SopPromotionBillJoin) {
      fvm.put(STORE_TAG, ((SopPromotionBillJoin) entity).getStoreTag());
    }
    return fvm;
  }
}
