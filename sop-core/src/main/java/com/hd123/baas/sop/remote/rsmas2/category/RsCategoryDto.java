 /*
  * Copyright (c) 2021. Shanghai HEADING information engineering Co., Ltd. All rights reserved.
  */
 package com.hd123.baas.sop.remote.rsmas2.category;

 import com.hd123.baas.sop.remote.rsmas2.RsEntityDto;
 import lombok.Getter;
 import lombok.Setter;

 /**
  * @author BinLee
  */
 @Getter
 @Setter
 public class RsCategoryDto extends RsEntityDto {
  private String categoryId;
  private String catalogId;
  private String code;
  private String name;
  private String parentCategoryId;
 }