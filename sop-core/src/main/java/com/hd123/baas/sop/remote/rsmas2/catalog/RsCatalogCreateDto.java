 /*
  * Copyright (c) 2021. Shanghai HEADING information engineering Co., Ltd. All rights reserved.
  */
 package com.hd123.baas.sop.remote.rsmas2.catalog;

 import lombok.Getter;
 import lombok.Setter;

 /**
  * @author BinLee
  */
 @Getter
 @Setter
 public class RsCatalogCreateDto {
   private String operator;
   private String catalogId;
   private String name;
   private Integer maxLevel;
   private Boolean leafOnly;
   private Boolean exclusive;
 }