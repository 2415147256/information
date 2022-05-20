package com.hd123.baas.sop.service.api.basedata.store;

import com.hd123.rumba.commons.biz.entity.Entity;
import com.hd123.rumba.commons.biz.entity.UCN;
import com.hd123.rumba.commons.biz.query.QueryEntity;
import com.hd123.rumba.commons.biz.query.QueryFactors;
import com.hd123.rumba.commons.biz.query.QueryField;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * @author Silent
 **/
@Getter
@Setter
@ApiModel(description = "门店")
public class Store extends Entity {

  /**
   * 级联价格组
   */
  public static final String PART_PRICE_GROUP = "priceGroup";
  /**
   * 级联默认POS机
   */
  public static final String PART_DEFAULT_POS = "defaultPos";
  /**
   * 级联默认POS机
   */
  public static final String PART_HAS_PLAT_SHOP_CATEGORY = "hasPlatShopCategory";

  @ApiModelProperty("组织类型")
  private String orgType;
  @ApiModelProperty(value = "所属组织ID")
  private String orgId;
  @ApiModelProperty(value = "ID")
  public String id;
  @ApiModelProperty(value = "代码")
  public String code;
  @ApiModelProperty(value = "名称")
  public String name;
  @ApiModelProperty(value = "门店类型")
  private String type;
  @ApiModelProperty("状态")
  private String state;
  @ApiModelProperty("营业状态，normal（营业中）、rest（休息中）、closed（停止营业）")
  private String businessState;
  @ApiModelProperty(value = "别名")
  private String alias;
  @ApiModelProperty(value = "详细地址")
  public String address;

  @ApiModelProperty(value = "区域")
  private UCN area;

  @ApiModelProperty(value = "营业时间")
  private String businessHour;
  @ApiModelProperty("营业时间")
  private List<String> businessHours;
  @ApiModelProperty("配置")
  private Map<String, String> configs;
  @ApiModelProperty("是否启用")
  private Boolean enabled;
  @ApiModelProperty("联系人电话")
  private String telephone;
  @ApiModelProperty("联系人")
  private String contactMan;
  @ApiModelProperty(value = "价格组")
  public UCN priceGroup;
  @ApiModelProperty(value = "默认POS机")
  public UCN defaultPos;
  @ApiModelProperty("经度")
  private String longitude;
  @ApiModelProperty("纬度")
  private String latitude;
  @ApiModelProperty("距离")
  private BigDecimal distance;
  @ApiModelProperty("是否常用的")
  private Boolean popular;
  @ApiModelProperty("是否已收藏")
  private Boolean collected;

  @ApiModelProperty("是否有门店类目，默认为false")
  private Boolean hasPlatShopCategory = Boolean.FALSE;


  @QueryEntity(Store.class)
  public static class Queries extends QueryFactors.Entity {

    private static final String PREFIX = Store.class.getName() + "::";
    @QueryField
    public static final String TENANT = PREFIX + "tenant";
    @QueryField
    public static final String ID = PREFIX + "id";
    @QueryField
    public static final String CODE = PREFIX + "code";
    @QueryField
    public static final String NAME = PREFIX + "name";
  }
}
