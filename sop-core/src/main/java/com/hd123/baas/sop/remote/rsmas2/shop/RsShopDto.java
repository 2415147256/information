 /*
  * Copyright (c) 2021. Shanghai HEADING information engineering Co., Ltd. All rights reserved.
  */
 package com.hd123.baas.sop.remote.rsmas2.shop;

 import com.hd123.baas.sop.remote.rsmas2.RsEntityDto;
 import lombok.Getter;
 import lombok.Setter;

 /**
  * @author BinLee
  */
 @Getter
 @Setter
 public class RsShopDto extends RsEntityDto {
  private String shopId;
  private String code;
  private String name;
  private Boolean enabled;
  private String mappedId;

  private RsDailyOpenTime dailyOpenTime;

  @Getter
  @Setter
  public static class RsDailyOpenTime {
   private String start;
   private String end;
  }

 }