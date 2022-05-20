package com.hd123.baas.sop.remote.rsh6sop.systemconfig;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

/**
 * @Author maodapeng
 * @Since
 */
@Setter
@Getter
public class H6SystemConfig {
  private Integer orgGid;
  private List<Item> configs;

  @Getter
  @Setter
  public static class Item {
    private String key;
    private String value;
  }
}
