package com.hd123.baas.sop.service.api.entity;
/*
 版权所有(C)，上海海鼎信息工程股份有限公司，2021，所有权利保留。
 
 项目名：	com.hd123.baas.sop.service.api.entity
 文件名：	SkuGradeConfig.java
 模块说明：	
 修改历史：
 2021年02月26日 - wangdanhua - 创建。
 */

import com.hd123.rumba.commons.biz.query.QueryEntity;
import com.hd123.rumba.commons.biz.query.QueryFactors;
import com.hd123.rumba.commons.biz.query.QueryField;

import lombok.Getter;
import lombok.Setter;

/**
 * 商品价格级配置
 *
 * @author wangdanhua
 **/
@Getter
@Setter
public class SkuGradeConfig {
  /** 主键 */
  private int uuid;
  /** 组织id */
  private String orgId;
  /** 商品id */
  private String skuId;
  /** 商品code */
  private String skuCode;
  /** 商品名称 */
  private String skuName;
  /** 价格级集合 */
  private String priceGradeJson;

  @QueryEntity(SkuGradeConfig.class)
  public static class Queries extends QueryFactors.Entity {

    private static final String PREFIX = SkuGradeConfig.class.getName() + "::";

    @QueryField
    public static final String SKU_ID = PREFIX + "skuId";
    @QueryField
    public static final String ORG_ID = PREFIX + "orgId";
  }

}
