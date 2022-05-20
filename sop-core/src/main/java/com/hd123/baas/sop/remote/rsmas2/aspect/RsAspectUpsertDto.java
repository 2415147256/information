 /*
  * Copyright (c) 2021. Shanghai HEADING information engineering Co., Ltd. All rights reserved.
  */
 package com.hd123.baas.sop.remote.rsmas2.aspect;

 import lombok.Getter;
 import lombok.Setter;

 import java.util.ArrayList;
 import java.util.List;

 /**
  * @author BinLee
  */
 @Getter
 @Setter
 public class RsAspectUpsertDto {
     private String operator;
     private String name;
     private String comment;
     private List<RsAspectField> fields=new ArrayList<>();
 }