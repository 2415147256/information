package com.hd123.baas.sop.service.api.basedata.pos;

import lombok.Data;

import java.util.Date;

@Data
public class PromDataDownloadTask {
  private String taskId;
  private String posNo;
  private Date createTime;
  private int state;
  private String downloadTime;
  private String errorMessage;
  private String cls;
}
