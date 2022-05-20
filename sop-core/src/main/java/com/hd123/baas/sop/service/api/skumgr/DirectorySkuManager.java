package com.hd123.baas.sop.service.api.skumgr;

import java.math.BigDecimal;
import java.util.Date;

import com.hd123.baas.sop.service.api.TenantEntity;
import com.hd123.rumba.commons.biz.query.QueryEntity;
import com.hd123.rumba.commons.biz.query.QueryFactors;
import com.hd123.rumba.commons.biz.query.QueryField;

import lombok.Getter;
import lombok.Setter;

/**
 * @Author maodapeng
 * @Since
 */
@Getter
@Setter
public class DirectorySkuManager extends TenantEntity {
  private String shop;
  private String shopCode;
  private String shopName;
  private String skuId;
  private String skuCode;
  private String skuName;
  private BigDecimal skuQpc;
  private String skuGid;
  private Date issueDate;
  private boolean channelRequired = false;
  private boolean directoryRequired = false;

  @QueryEntity(DirectorySkuManager.class)
  public static class Queries extends QueryFactors.Entity {

    private static final String PREFIX = DirectorySkuManager.class.getName() + "::";

    @QueryField
    public static final String TENANT = PREFIX + "tenant";
    @QueryField
    public static final String ISSUE_DATE = PREFIX + "issueDate";
    @QueryField
    public static final String SHOP = PREFIX + "shop";

  }
}
