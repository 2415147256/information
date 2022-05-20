 /*
  * Copyright (c) 2021. Shanghai HEADING information engineering Co., Ltd. All rights reserved.
  */
 package com.hd123.baas.sop.remote.rsmas2;

 import lombok.Getter;
 import lombok.Setter;

 import java.util.ArrayList;
 import java.util.List;

 /**
  * @author BinLee
  */
 @Getter
 @Setter
 public class RsMasRequest<T> {
     private String operator;
     private List<T> data = new ArrayList<>();
 }