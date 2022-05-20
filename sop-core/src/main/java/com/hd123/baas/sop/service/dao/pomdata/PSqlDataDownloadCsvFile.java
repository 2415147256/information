package com.hd123.baas.sop.service.dao.pomdata;

import com.hd123.baas.sop.service.api.pomdata.SqlDataDownloadCsvFile;
import com.hd123.devops.ebt.annotation.Column;
import com.hd123.devops.ebt.annotation.Entity;
import com.hd123.devops.ebt.annotation.Id;
import com.hd123.devops.ebt.annotation.Index;
import com.hd123.devops.ebt.annotation.Table;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

/**
 * @author zhuangwenting
 * @since 1.0
 */
@Entity
@Table(caption = PSqlDataDownloadCsvFile.TABLE_CAPTION, name = PSqlDataDownloadCsvFile.TABLE_NAME, indexes = {
        @Index(name = "idx_SqlDataDownloadCsvFile_1", columnNames = { PSqlDataDownloadCsvFile.TASK_ID})
})
public class PSqlDataDownloadCsvFile {
  public static final String TABLE_NAME = "SqlDataDownloadCsvFile";
  public static final String TABLE_CAPTION = "sql下载Csv文件";

  @Id
  @Column(title = "主键", name = PSqlDataDownloadCsvFile.UUID, length = 38)
  public static final String UUID = "uuid";
  @Column(title = "任务id", name = PSqlDataDownloadCsvFile.TASK_ID, length = 38)
  public static final String TASK_ID = "taskId";
  @Column(title = "数据源", name = PSqlDataDownloadCsvFile.DATA_SOURCE, length = 50)
  public static final String DATA_SOURCE = "dataSource";
  @Column(title = "文件名", name = PSqlDataDownloadCsvFile.FILE_NAME, length = 30)
  public static final String FILE_NAME = "fileName";
  @Column(title = "sql语句", name = PSqlDataDownloadCsvFile.QUERY_SQL, length = 4000)
  public static final String QUERY_SQL = "querySql";

  public static final String[] COLUMNS = new String[]{UUID, TASK_ID, DATA_SOURCE, FILE_NAME, QUERY_SQL};

  public static Map<String, Object> toFieldValues(SqlDataDownloadCsvFile entity) {
    Map<String, Object> fvm = new HashMap<>();
    fvm.put(UUID, java.util.UUID.randomUUID().toString().replace("-", ""));
    fvm.put(TASK_ID, entity.getTaskId());
    fvm.put(DATA_SOURCE, entity.getDataSource());
    fvm.put(FILE_NAME, entity.getFileName());
    fvm.put(QUERY_SQL, entity.getQuerySql());
    return fvm;
  }

  public static SqlDataDownloadCsvFile mapRow(ResultSet rs, int i) throws SQLException {
    SqlDataDownloadCsvFile target = new SqlDataDownloadCsvFile();
    target.setTaskId(rs.getString(TASK_ID));
    target.setDataSource(rs.getString(DATA_SOURCE));
    target.setFileName(rs.getString(FILE_NAME));
    target.setQuerySql(rs.getString(QUERY_SQL));
    return target;
  }
}
