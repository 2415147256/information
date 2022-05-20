package com.hd123.baas.sop.service.api.pomdata;

import lombok.Data;

@Data
public class PosSqlDataDownloadTask {
  private String tenant;
  private String posNo;
  private String taskId;
}
