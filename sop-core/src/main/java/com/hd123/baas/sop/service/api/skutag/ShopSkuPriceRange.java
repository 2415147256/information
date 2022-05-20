package com.hd123.baas.sop.service.api.skutag;

import com.hd123.rumba.commons.biz.entity.StandardEntity;
import com.hd123.rumba.commons.biz.query.QueryEntity;
import com.hd123.rumba.commons.biz.query.QueryFactors;
import com.hd123.rumba.commons.biz.query.QueryField;
import com.hd123.rumba.commons.biz.query.QueryOperation;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * 门店商品价格带
 */
@Getter
@Setter
public class ShopSkuPriceRange extends StandardEntity {

  public static final String PART_TAGS = "part_tags";

  //标题
  private String title;

  //租户
  private String tenant;
  //组织Id
  private String orgId;

  //商品ID
  private String skuId;
  //商品code
  private String skuCode;
  //商品名称
  private String skuName;
  //商品Gid
  private String gdGid;
  //商品规格
  private BigDecimal skuQpc;
  //输入码
  private String inputCode;
  //售价
  private BigDecimal salePrice;

  //门店ID
  private String shopId;
  //门店code
  private String shopCode;
  //门店名称
  private String shopName;
  //标签Id集合
  private String tagIds;
  //是否已删除，0:正常；1:删除
  private byte deleted;

  //标签集合
  private List<Tag> tags = new ArrayList<>();


  @QueryEntity(ShopSkuPriceRange.class)
  public static class Queries extends QueryFactors.StandardEntity {
    private static final String PREFIX = ShopSkuPriceRange.class.getName() + "::";
    @QueryField
    public static final String UUID = PREFIX + "uuid";
    @QueryField
    public static final String ORG_ID = PREFIX + "orgId";
    @QueryField
    public static final String TENANT = PREFIX + "tenant";
    @QueryField
    public static final String SKU_ID = PREFIX + "skuId";
    @QueryField
    public static final String SKU_CODE = PREFIX + "skuCode";
    @QueryField
    public static final String SKU_NAME = PREFIX + "skuName";
    @QueryField
    public static final String SHOP_CODE = PREFIX + "shopCode";
    @QueryField
    public static final String SHOP_ID = PREFIX + "shopId";
    @QueryField
    public static final String DELETED = PREFIX + "deleted";
    @QueryOperation
    public static final String KEYWORD_LIKE = PREFIX + "keyword like";
  }

}