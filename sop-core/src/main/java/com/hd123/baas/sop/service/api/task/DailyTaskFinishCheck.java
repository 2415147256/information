package com.hd123.baas.sop.service.api.task;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

/**
 * @author mibo
 */
@Setter
@Getter
public class DailyTaskFinishCheck {
  private Boolean isFinish;
  private String tips;
  private Date earliestFinishTime;
}
