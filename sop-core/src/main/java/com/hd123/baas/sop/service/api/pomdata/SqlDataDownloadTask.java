package com.hd123.baas.sop.service.api.pomdata;

import lombok.Data;

import java.util.Date;

@Data
public class SqlDataDownloadTask {
  private String tenant;
  private String taskId;
  private String posNo;
  private String sourceId;
  private Date createTime;
  private int state;
  private String downloadTime;
  private String errorMessage;
  private String cls;
}
