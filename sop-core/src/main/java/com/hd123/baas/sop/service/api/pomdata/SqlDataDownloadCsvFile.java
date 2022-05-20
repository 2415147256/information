package com.hd123.baas.sop.service.api.pomdata;

import lombok.Data;

@Data
public class SqlDataDownloadCsvFile {
  private String taskId;
  private String dataSource = "jpos.bo.dataSource.sop.pomdata";
  private String fileName;
  private String querySql;
}
