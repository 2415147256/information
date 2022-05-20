package com.hd123.baas.sop.service.dao.price.pricepromotion;

import com.hd123.baas.sop.service.api.price.pricepromotion.PricePromotion;
import com.hd123.rumba.commons.jdbc.annotation.MapToEntity;
import com.hd123.rumba.commons.jdbc.annotation.SchemaMeta;
import com.hd123.rumba.commons.jdbc.entity.PStandardEntity;

/**
 * @author zhengzewang on 2020/11/13.
 */
@SchemaMeta
@MapToEntity(PricePromotion.class)
public class PPricePromotion extends PStandardEntity {

  public static final String TABLE_NAME = "price_promotion";
  public static final String TABLE_ALIAS = "_price_promotion";

  public static final String TENANT = "tenant";
  public static final String ORG_ID = "org_id";
  public static final String FLOW_NO = "flow_no";
  public static final String EFFECTIVE_START_DATE = "effective_start_date";
  public static final String EFFECTIVE_END_DATE = "effective_end_date";
  public static final String STATE = "state";
  public static final String ALL_SHOPS = "all_shops";
  public static final String REASON = "reason";
  public static final String TYPE = "type";
  public static final String PROMOTION_TARGETS = "promotion_targets";
  public static final String ORD_LIMIT_AMOUNT = "ord_limit_amount";
  public static final String ORD_LIMIT_QTY = "ord_limit_qty";
  public static final String HEAD_SHARING_RATE = "head_sharing_rate";
  public static final String SUPERVISOR_SHARING_RATE = "supervisor_sharing_rate";
  public static final String NOTE = "note";

  public static String[] allColumns() {
    return toColumnArray(PStandardEntity.allColumns(), TENANT,ORG_ID,FLOW_NO, EFFECTIVE_START_DATE, EFFECTIVE_END_DATE, STATE,
        ALL_SHOPS, REASON, TYPE, PROMOTION_TARGETS, ORD_LIMIT_AMOUNT, ORD_LIMIT_QTY, HEAD_SHARING_RATE, SUPERVISOR_SHARING_RATE, NOTE);
  }

}
