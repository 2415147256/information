/**
 * 版权所有(C)，上海海鼎信息工程股份有限公司，2019，所有权利保留。
 * <p>
 * 项目名：	mas-product-api
 * 文件名：	Spu.java
 * 模块说明：
 * 修改历史：
 * Sep 11, 2019 - sulin - 创建。
 */
package com.hd123.baas.sop.remote.rsmas.spu;

import com.hd123.baas.sop.remote.rsmas.RsMasEntity;
import com.hd123.baas.sop.remote.rsmas.RsParameter;
import com.hd123.baas.sop.remote.rsmas.goods.RsProductAttribute;
import com.hd123.baas.sop.remote.rsmas.sku.RsSku;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

/**
 * @author sulin
 */
@Getter
@Setter
@ApiModel("Spu")
public class RsSpu extends RsMasEntity {
  private static final long serialVersionUID = 645082526795339813L;

  /**
   * 级联副图
   */
  public static final String PART_SPU_IMAGES = "spu_images";
  /**
   * 级联自定义属性
   */
  public static final String PART_SPU_PROPERTIES = "spu_properties";
  /**
   * 级联商品特性
   */
  public static final String PART_SPU_ATTRIBUTES = "spu_attributes";
  /**
   * 级联自定义属性
   */
  public static final String PART_SPU_PARAMETER = "spu_parameters";
  /**
   * 级联SKU
   */
  public static final String PART_SPU_SKU = "spu_sku";
  /**
   * 级联自定义属性
   */
  public static final String PART_SKU_PROPERTIES = "properties";
  /**
   * 级联SKU,不带SKU级联关系
   */
  public static final String PART_SPU_SKU_SIMPLE = "spu_sku_simple";

  @ApiModelProperty(value = "组织类型")
  private String orgType;
  @ApiModelProperty(value = "组织id")
  private String orgId;
  @ApiModelProperty(value = "名称")
  private String name;
  @ApiModelProperty(value = "主图")
  private String image;
  @ApiModelProperty(value = "副图")
  private List<String> images;
  @ApiModelProperty(value = "销售属性")
  List<RsProductProperties> properties = new ArrayList<>();
  @ApiModelProperty(value = "描述")
  private String description;
  @ApiModelProperty(value = "商品特性")
  private List<RsProductAttribute> attributes = new ArrayList<RsProductAttribute>();
  @ApiModelProperty(value = "自定义属性")
  private List<RsParameter> customFields = new ArrayList<RsParameter>();
  @ApiModelProperty(value = "sku信息")
  private List<RsSku> skus = new ArrayList<RsSku>();
}
