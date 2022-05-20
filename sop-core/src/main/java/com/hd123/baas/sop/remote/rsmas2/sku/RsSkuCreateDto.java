 /*
  * Copyright (c) 2021. Shanghai HEADING information engineering Co., Ltd. All rights reserved.
  */
 package com.hd123.baas.sop.remote.rsmas2.sku;

 import lombok.Getter;
 import lombok.Setter;

 import java.math.BigDecimal;
 import java.util.List;
 import java.util.Map;

 /**
  * @author BinLee
  */
 @Getter
 @Setter
 public class RsSkuCreateDto {
     private String skuId;
     private String code;
     private List<String> inputCodes;
     private String name;
     private String munit;
     private String brand;
     private String spec;
     private BigDecimal qpc;
     private String qpcStr;
     private String mappedId;

     private Map<String,String> category;
     private Map<String,Map<String,String>> aspect;
 }