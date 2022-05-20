package com.hd123.baas.sop.service.api.announcement;

import com.hd123.rumba.commons.biz.entity.Entity;
import com.hd123.rumba.commons.biz.query.QueryEntity;
import com.hd123.rumba.commons.biz.query.QueryFactors;
import com.hd123.rumba.commons.biz.query.QueryField;

import lombok.Getter;
import lombok.Setter;

/**
 * @author zhengzewang on 2020/11/7.
 * 
 *         关联门店
 * 
 */
@Getter
@Setter
public class AnnouncementShop extends Entity {

  private String tenant;
  private String owner;
  private String shop;
  private String shopCode;
  private String shopName;

  @QueryEntity(AnnouncementShop.class)
  public static class Queries extends QueryFactors.Entity {

    private static final String PREFIX = AnnouncementShop.class.getName() + "::";

    @QueryField
    public static final String TENANT = PREFIX + "tenant";
    @QueryField
    public static final String OWNER = PREFIX + "owner";
    @QueryField
    public static final String SHOP = PREFIX + "shop";

  }

}
