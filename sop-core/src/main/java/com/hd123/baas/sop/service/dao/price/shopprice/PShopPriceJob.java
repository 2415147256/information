package com.hd123.baas.sop.service.dao.price.shopprice;

import com.hd123.rumba.commons.jdbc.entity.PStandardEntity;

/**
 * @author zhengzewang on 2020/11/23.
 */
public class PShopPriceJob extends PStandardEntity {

  public static final String TABLE_NAME = "shop_price_job";
  public static final String TABLE_ALIAS = "_shop_price_job";

  public static final String TENANT = "tenant";
  public static final String ORG_ID = "org_id";
  public static final String SHOP = "shop";

  public static final String TASK_ID = "task_id";
  public static final String EXECUTE_DATE = "execute_date";
  public static final String PRICE_ADJUSTMENT = "price_adjustment";
  public static final String STATE = "state";

  public static final String SHOP_CODE = "shop_code";
  public static final String SHOP_NAME = "shop_name";
  public static final String ERR_MSG = "err_msg";

  public static String[] allColumns() {
    return toColumnArray(PStandardEntity.allColumns(), TENANT, ORG_ID, SHOP, TASK_ID, PRICE_ADJUSTMENT, EXECUTE_DATE,
        STATE, SHOP_CODE, SHOP_NAME, ERR_MSG);
  }

}
