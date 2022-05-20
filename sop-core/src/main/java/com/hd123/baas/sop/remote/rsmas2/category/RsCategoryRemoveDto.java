 /*
  * Copyright (c) 2021. Shanghai HEADING information engineering Co., Ltd. All rights reserved.
  */
 package com.hd123.baas.sop.remote.rsmas2.category;

 import lombok.Getter;
 import lombok.Setter;

 /**
  * @author BinLee
  */
 @Getter
 @Setter
 public class RsCategoryRemoveDto {
   private String operator;
   private String categoryId;
   private Boolean forced;
 }