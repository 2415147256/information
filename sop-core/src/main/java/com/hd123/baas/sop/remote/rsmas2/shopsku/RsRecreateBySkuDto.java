 /*
  * Copyright (c) 2021. Shanghai HEADING information engineering Co., Ltd. All rights reserved.
  */
 package com.hd123.baas.sop.remote.rsmas2.shopsku;

 import lombok.Getter;
 import lombok.Setter;

 import java.util.ArrayList;
 import java.util.List;

 /**
  * @author BinLee
  */
 @Getter
 @Setter
 public class RsRecreateBySkuDto {
  private String operator;
  private String skuId;
  private List<String> shopIds =new ArrayList<>();
 }