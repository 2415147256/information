package com.hd123.baas.sop.remote.rsmas2.catalog;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * @author zhangweigang
 */
@Getter
@Setter
public class RsCatalogApply {
  private String operator;
  private String catalogId;
  private List<String> applyTo;
}
