 /*
  * Copyright (c) 2021. Shanghai HEADING information engineering Co., Ltd. All rights reserved.
  */
 package com.hd123.baas.sop.remote.rsmas2;

 import lombok.Getter;
 import lombok.Setter;

 import java.util.Date;

 /**
  * @author BinLee
  */
 @Getter
 @Setter
 public abstract class RsEntityDto {
  private Date lastModified;
  private String lastModifier;
  private Long oca;
 }