package com.hd123.baas.sop.service.api.task;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

/**
 * @Author maodapeng
 * @Since
 */
@Setter
@Getter
public class Feedback {
  private FeedbackType type;
  private List<Item> items;

  @Getter
  @Setter
  public static class Item {
    private String value;
    private String ext;
  }
}
