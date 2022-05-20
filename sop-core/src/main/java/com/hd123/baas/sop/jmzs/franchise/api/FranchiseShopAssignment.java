package com.hd123.baas.sop.jmzs.franchise.api;

import com.hd123.baas.sop.service.api.TenantStandardEntity;
import com.hd123.rumba.commons.biz.entity.UCN;
import com.hd123.rumba.commons.biz.query.QueryEntity;
import com.hd123.rumba.commons.biz.query.QueryFactorName;
import com.hd123.rumba.commons.biz.query.QueryFactors;
import com.hd123.rumba.commons.biz.query.QueryField;
import com.hd123.rumba.commons.util.converter.Converter;
import com.hd123.rumba.commons.util.converter.ConverterBuilder;
import com.qianfan123.baas.common.entity.BUcn;
import lombok.Getter;
import lombok.Setter;

/**
 * 加盟商-门店
 */
@Setter
@Getter
public class FranchiseShopAssignment extends TenantStandardEntity {

  public static final Converter<FranchiseShopAssignment, UCN> CONVERTER = ConverterBuilder //
      .newBuilder(FranchiseShopAssignment.class, UCN.class) //
      .map("shopId","uuid")
      .map("shopCode","code")
      .map("shopName","name")
      .build();

  public static final Converter<FranchiseShopAssignment, BUcn> B_CONVERTER = ConverterBuilder //
      .newBuilder(FranchiseShopAssignment.class, BUcn.class) //
      .map("shopId","uuid")
      .map("shopCode","code")
      .map("shopName","name")
      .build();
  /**
   * 门店Id
   */
  private String shopId;
  private String shopCode;
  private String shopName;

  private String franchiseId;
  private String franchiseName;
  private String franchiseCode;
  private String franchiseUuid;



  @QueryEntity(FranchiseShopAssignment.class)
  public static class Queries extends QueryFactors.StandardEntity {
    private static final QueryFactorName PREFIX = QueryFactorName.prefix(FranchiseShopAssignment.class);
    @QueryField
    public static final String TENANT = PREFIX.nameOf("tenant");
    // @QueryField
    // public static final String ORG_ID = PREFIX.nameOf("orgId");
    // @QueryField
    // public static final String SHOP_ID = PREFIX.nameOf("shopId");
    @QueryField
    public static final String FRANCHISE_UUID = PREFIX.nameOf("franchiseUuid");
    @QueryField
    public static final String FRANCHISE_ID = PREFIX.nameOf("franchiseId");
    @QueryField
    public static final String FRANCHISE_NAME = PREFIX.nameOf("franchiseName");
    @QueryField
    public static final String FRANCHISE_CODE = PREFIX.nameOf("franchiseCode");
    @QueryField
    public static final String SHOP_NAME = PREFIX.nameOf("shopName");
    @QueryField
    public static final String SHOP_ID = PREFIX.nameOf("shopId");
    @QueryField
    public static final String SHOP_CODE = PREFIX.nameOf("shopCode");

  }

}
