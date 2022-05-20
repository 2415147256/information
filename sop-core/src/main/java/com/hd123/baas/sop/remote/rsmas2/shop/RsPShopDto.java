 /*
  * Copyright (c) 2021. Shanghai HEADING information engineering Co., Ltd. All rights reserved.
  */
 package com.hd123.baas.sop.remote.rsmas2.shop;

 import lombok.Getter;
 import lombok.Setter;

 /**
  * @author BinLee
  */
 @Getter
 @Setter
 public class RsPShopDto extends RsShopDto {
     private String area;
     private RsAddress address;
     private RsGeocoor geocoor;
     private RsContact contact;

     @Getter
     @Setter
     public static class RsAddress {
         private String country;
         private String province;
         private String city;
         private String district;
         private String street;
         private String detail;
         private String postCode;
     }

     @Getter
     @Setter
     public static class RsGeocoor {
         private String longitude;
         private String latitude;
     }

     @Getter
     @Setter
     public static class RsContact {
         private String servicePhone;
         private String telephone;
         private String mobile;
         private String fax;
         private String email;
         private String person;
     }

 }