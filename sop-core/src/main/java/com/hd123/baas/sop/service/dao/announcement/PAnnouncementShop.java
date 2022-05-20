package com.hd123.baas.sop.service.dao.announcement;

import com.hd123.rumba.commons.jdbc.entity.PEntity;

/**
 * @author zhengzewang on 2020/11/20.
 */
public class PAnnouncementShop extends PEntity {

  public static final String TABLE_NAME = "announcement_shop";
  public static final String TABLE_ALIAS = "_announcement_shop";

  public static final String TENANT = "tenant";
  public static final String OWNER = "owner";
  public static final String SHOP = "shop";
  public static final String SHOP_CODE = "shop_code";
  public static final String SHOP_NAME = "shop_name";

  public static String[] allColumns() {
    return toColumnArray(PEntity.allColumns(), TENANT, OWNER, SHOP, SHOP_CODE, SHOP_NAME);
  }

}
