package com.hd123.baas.sop.service.api.feedback;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * @Author maodapeng
 * @Since
 */
@Setter
@Getter
public class FeedbackReasonSaver {
  private String orgId;
  private FeedbackReasonType type;
  private String sortCode;
  private String sortName;
  private List<String> reasons;
}
