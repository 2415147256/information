package com.hd123.baas.sop.service.dao.skugrade;
/*
 版权所有(C)，上海海鼎信息工程股份有限公司，2021，所有权利保留。
 
 项目名：	com.hd123.baas.sop.service.dao.skugrade
 文件名：	PSkuGradeConfig.java
 模块说明：	
 修改历史：
 2021年02月26日 - wangdanhua - 创建。
 */

import com.hd123.baas.sop.service.api.entity.SkuGradeConfig;
import com.hd123.rumba.commons.jdbc.entity.PEntity;
import java.util.Map;
import java.util.HashMap;

/**
 * @author wangdanhua
 */
public class PSkuGradeConfig extends PEntity {
  public static final String TABLE_NAME = "sku_grade_config";
  public static final String TABLE_ALIAS = "_SkuGradeConfig";

  public static final String TENANT = "tenant";
  public static final String ORG_ID = "org_id";
  public static final String SKU_ID = "sku_id";
  public static final String SKU_CODE = "sku_code";
  public static final String SKU_NAME = "sku_name";
  public static final String PRICE_GRADE_JSON = "price_grade_json";

  public static String[] allColumns() {
    return toColumnArray(PEntity.allColumns(), TENANT, ORG_ID, SKU_ID, SKU_CODE, SKU_NAME, PRICE_GRADE_JSON);
  }

  public static Map<String, Object> getBizMap(String tenant, SkuGradeConfig skuGradeConfig) {
    Map<String, Object> map = new HashMap<>();
    map.put(TENANT, tenant);
    map.put(ORG_ID, skuGradeConfig.getOrgId());
    map.put(SKU_ID, skuGradeConfig.getSkuId());
    map.put(SKU_CODE, skuGradeConfig.getSkuCode());
    map.put(SKU_NAME, skuGradeConfig.getSkuName());
    map.put(PRICE_GRADE_JSON, skuGradeConfig.getPriceGradeJson());
    return map;
  }

}
