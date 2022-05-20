package com.hd123.baas.sop.service.api.formula;

import com.hd123.baas.sop.service.api.TenantEntity;
import com.hd123.rumba.commons.biz.query.QueryEntity;
import com.hd123.rumba.commons.biz.query.QueryField;
import com.hd123.rumba.commons.biz.query.QueryOperation;
import lombok.Getter;
import lombok.Setter;

/**
 * @Author maodapeng
 * @Since 说明 1、等号左边右边只会出现一个商品 2、formula用作后端计算 3、formulaDesc用作前端展示
 */
@Setter
@Getter
public class PriceSkuFormula extends TenantEntity {
  private String orgId;
  private String skuId;
  private String skuCode;
  private String skuName;
  // 商品到店价*1.2 + 1
  private String formula;
  // 梨到店价 = 苹果到店价*1.2 + 1
  private String formulaDesc;
  // 等号右边的商品ID
  private String dependOnSkuId;

  @QueryEntity(PriceSkuFormula.class)
  public static class Queries {
    private static final String PREFIX = PriceSkuFormula.class.getName() + "::";
    @QueryField
    public static final String TENANT = PREFIX + "tenant";
    @QueryField
    public static final String ORG_ID = PREFIX + "orgId";
    @QueryOperation
    public static final String SKU_KEYWORD = PREFIX + "skuKeyword";
  }
}
