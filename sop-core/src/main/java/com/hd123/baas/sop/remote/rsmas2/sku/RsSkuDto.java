 /*
  * Copyright (c) 2021. Shanghai HEADING information engineering Co., Ltd. All rights reserved.
  */
 package com.hd123.baas.sop.remote.rsmas2.sku;

 import com.hd123.baas.sop.remote.rsmas2.RsEntityDto;
 import lombok.Getter;
 import lombok.Setter;

 import java.math.BigDecimal;
 import java.util.ArrayList;
 import java.util.HashMap;
 import java.util.List;
 import java.util.Map;

 /**
  * @author BinLee
  */
 @Getter
 @Setter
 public class RsSkuDto extends RsEntityDto {

  private String skuId;
  private String code;
  private String name;
  private String munit;
  private String brand;
  private String spec;
  private BigDecimal qpc;
  private String qpcStr;
  private String mappedId;

  private List<String> inputCodes =new ArrayList<>();
  private Map<String,String> category = new HashMap<>();
  private Map<String,Map<String,Object>> aspect =new HashMap<>();

 }