 /*
  * Copyright (c) 2021. Shanghai HEADING information engineering Co., Ltd. All rights reserved.
  */
 package com.hd123.baas.sop.remote.rsmas2.aspect;


 import com.hd123.baas.sop.remote.rsmas2.RsEntityDto;
 import lombok.Getter;
 import lombok.Setter;

 import java.util.ArrayList;
 import java.util.List;

 /**
  * @author BinLee
  */
 @Getter
 @Setter
 public class RsAspectDto extends RsEntityDto {
     private String name;
     private String comment;
     private List<RsAspectField> fields=new ArrayList<>();
 }