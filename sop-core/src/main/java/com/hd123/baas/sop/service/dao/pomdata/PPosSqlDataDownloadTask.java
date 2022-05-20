package com.hd123.baas.sop.service.dao.pomdata;

import com.hd123.baas.sop.service.api.pomdata.PosSqlDataDownloadTask;
import com.hd123.devops.ebt.annotation.Column;
import com.hd123.devops.ebt.annotation.Entity;
import com.hd123.devops.ebt.annotation.Id;
import com.hd123.devops.ebt.annotation.Index;
import com.hd123.devops.ebt.annotation.Table;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;

/**
 * @author zhuangwenting
 * @since 1.0
 */
@Entity
@Table(caption = PPosSqlDataDownloadTask.TABLE_CAPTION, name = PPosSqlDataDownloadTask.TABLE_NAME, indexes = {
        @Index(name = "idx_pos_sql_data_download_task_1", columnNames = { PPosSqlDataDownloadTask.TASK_ID}),
        @Index(name = "idx_pos_sql_data_download_task_2", columnNames = { PPosSqlDataDownloadTask.POS_NO})
})
public class PPosSqlDataDownloadTask {
  public static final String TABLE_NAME = "sop_pos_sql_data_download_task";
  public static final String TABLE_CAPTION = "收银机sql下载任务";

  @Id
  @Column(title = "主键", name = PPosSqlDataDownloadTask.UUID, length = 60)
  public static final String UUID = "uuid";
  @Column(title = "租户", name = PPosSqlDataDownloadTask.TENANT, length = 40)
  public static final String TENANT = "tenant";
  @Column(title = "posNo", name = PPosSqlDataDownloadTask.POS_NO, length = 20)
  public static final String POS_NO = "posNo";
  @Column(title = "taskId", name = PPosSqlDataDownloadTask.TASK_ID, length = 40)
  public static final String TASK_ID = "taskId";


  public static final String[] COLUMNS = new String[]{UUID, TENANT, POS_NO, TASK_ID,};

  public static Map<String, Object> toFieldValues(PosSqlDataDownloadTask entity) {
    Map<String, Object> fvm = new HashMap<>();
    fvm.put(UUID, MessageFormat.format("{0}:{1}", entity.getTenant(), entity.getPosNo()));
    fvm.put(TENANT, entity.getTenant());
    fvm.put(POS_NO, entity.getPosNo());
    fvm.put(TASK_ID, entity.getTaskId());
    return fvm;
  }

  public static PosSqlDataDownloadTask mapRow(ResultSet rs, int i) throws SQLException {
    PosSqlDataDownloadTask target = new PosSqlDataDownloadTask();
    target.setTaskId(rs.getString(TASK_ID));
    target.setTenant(rs.getString(TENANT));
    target.setPosNo(rs.getString(POS_NO));
    return target;
  }
}
