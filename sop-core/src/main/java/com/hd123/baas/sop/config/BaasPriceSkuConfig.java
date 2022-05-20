package com.hd123.baas.sop.config;

import com.qianfan123.baas.config.api.annotation.BcGroup;
import com.qianfan123.baas.config.api.annotation.BcKey;

import com.qianfan123.baas.config.api.field.ConfigEditor;
import lombok.Getter;
import lombok.Setter;

/**
 * @author zhengzewang on 2020/11/24.
 */
@Getter
@Setter
@BcGroup(name = "商品定价")
public class BaasPriceSkuConfig {

  private static final String PREFIX = "priceSku.";

  @BcKey(name = "售价尾数")
  private int arrears = 9;

  @BcKey(name = "商品定价规格范围，0：代表不限，1：代表仅限规格等于1；其他暂不支持", editor = "integer")
  private Integer skuQpcOfPrice = 0;

  @BcKey(name = "试算单试算商品过滤商品类型列表,按照','分隔")
  private String excludeGoodsTypes;

  @BcKey(name = "试算单商品过滤商品分类Code列表,按照','分隔")
  private String excludeCategoryCodes;

  @BcKey(name = "开启差异对比，默认=false", editor = ConfigEditor.BOOL)
  private boolean enableChecker = Boolean.FALSE;

  @BcKey(name = "查询价格商品策略，默认=空，可选值 空或V2")
  private String queryPriceSkuMgr;
}
