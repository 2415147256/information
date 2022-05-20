package com.hd123.baas.sop.service.api.price;

import java.math.BigDecimal;

import com.hd123.baas.sop.service.api.basedata.category.Category;
import com.hd123.rumba.commons.biz.query.QueryEntity;
import com.hd123.rumba.commons.biz.query.QueryField;
import com.hd123.rumba.commons.biz.query.QueryOperation;

import lombok.Getter;
import lombok.Setter;

/**
 * @author zhengzewang on 2020/11/10.
 */
@Getter
@Setter
public class PriceSku {

  private String tenant;
  private String orgId;
  private Category category;
  private String id;
  private String code;
  private String goodsGid;
  private String goodsType;
  private String name;
  private BigDecimal qpc;
  private String unit;
  private int deleted;

  @QueryEntity(PriceSku.class)
  public static class Queries {

    private static final String PREFIX = PriceSku.class.getName() + "::";
    @QueryField
    public static final String TENANT = PREFIX + "tenant";
    @QueryField
    public static final String ORG_ID = PREFIX + "orgId";
    @QueryField
    public static final String DELETED = PREFIX + "deleted";
    @QueryField
    public static final String ID = PREFIX + "id";
    @QueryField
    public static final String CODE = PREFIX + "code";
    @QueryField
    public static final String NAME = PREFIX + "name";
    @QueryField
    public static final String QPC = PREFIX + "qpc";

    @QueryOperation
    public static final String SKU_KEYWORD = PREFIX + "skuKeyword";
    @QueryOperation
    public static final String POSITION_ID_EQ = PREFIX + "positionIdEq";
    @QueryOperation
    public static final String GROUP_ID_EQ = PREFIX + "groupIdEq";
    @QueryOperation
    public static final String TOLERANCE_BETWEEN = "toleranceBetween";
  }

}
