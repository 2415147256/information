package com.hd123.baas.sop.service.dao.pomdata;

import com.hd123.baas.sop.service.api.pomdata.SqlDataDownloadTask;
import com.hd123.devops.ebt.annotation.Column;
import com.hd123.devops.ebt.annotation.Entity;
import com.hd123.devops.ebt.annotation.Id;
import com.hd123.devops.ebt.annotation.Index;
import com.hd123.devops.ebt.annotation.Table;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @author zhuangwenting
 * @since 1.0
 */
@Entity
@Table(caption = PSqlDataDownloadTask.TABLE_CAPTION, name = PSqlDataDownloadTask.TABLE_NAME, indexes = {
        @Index(name = "idx_SqlDataDownloadTask_1", columnNames = { PSqlDataDownloadTask.POS_NO, PSqlDataDownloadTask.STATE}),
        @Index(name = "idx_SqlDataDownloadTask_2", columnNames = { PSqlDataDownloadTask.SOURCE_ID}),
})
public class PSqlDataDownloadTask {
  public static final String TABLE_NAME = "SqlDataDownloadTask";
  public static final String TABLE_CAPTION = "sql下载任务";

  @Id
  @Column(title = "主键", name = PSqlDataDownloadTask.TASK_ID, length = 38)
  public static final String TASK_ID = "taskId";
  @Column(title = "租户", name = PSqlDataDownloadTask.TENANT, length = 40)
  public static final String TENANT = "tenant";
  @Column(title = "posNo", name = PSqlDataDownloadTask.POS_NO, length = 20)
  public static final String POS_NO = "posNo";
  @Column(title = "来源标识", name = PSqlDataDownloadTask.SOURCE_ID, length = 40)
  public static final String SOURCE_ID = "sourceId";
  @Column(title = "创建时间", name = PSqlDataDownloadTask.CREATE_TIME, fieldClass = Date.class)
  public static final String CREATE_TIME = "createTime";
  @Column(title = "状态", name = PSqlDataDownloadTask.STATE, fieldClass = int.class)
  public static final String STATE = "state";
  @Column(title = "下载时间", name = PSqlDataDownloadTask.DOWNLOAD_TIME, fieldClass = Date.class)
  public static final String DOWNLOAD_TIME = "downloadTime";
  @Column(title = "错误信息", name = PSqlDataDownloadTask.ERROR_MESSAGE, length = 255)
  public static final String ERROR_MESSAGE = "errorMessage";
  @Column(title = "cls", name = PSqlDataDownloadTask.CLS, length = 10)
  public static final String CLS = "cls";

  public static final String[] COLUMNS = new String[]{TASK_ID, TENANT, POS_NO, SOURCE_ID, CREATE_TIME, STATE, DOWNLOAD_TIME, ERROR_MESSAGE, CLS};

  public static Map<String, Object> toFieldValues(SqlDataDownloadTask entity) {
    Map<String, Object> fvm = new HashMap<>();
    fvm.put(TASK_ID, entity.getTaskId());
    fvm.put(TENANT, entity.getTenant());
    fvm.put(POS_NO, entity.getPosNo());
    fvm.put(SOURCE_ID, entity.getSourceId());
    fvm.put(CREATE_TIME, entity.getCreateTime());
    fvm.put(STATE, entity.getState());
    fvm.put(DOWNLOAD_TIME, entity.getDownloadTime());
    fvm.put(ERROR_MESSAGE, entity.getErrorMessage());
    fvm.put(CLS, entity.getCls());
    return fvm;
  }

  public static SqlDataDownloadTask mapRow(ResultSet rs, int i) throws SQLException {
    SqlDataDownloadTask target = new SqlDataDownloadTask();
    target.setTaskId(rs.getString(TASK_ID));
    target.setTenant(rs.getString(TENANT));
    target.setPosNo(rs.getString(POS_NO));
    target.setSourceId(rs.getString(SOURCE_ID));
    target.setCreateTime(rs.getTimestamp(CREATE_TIME));
    target.setState(rs.getInt(STATE));
    target.setDownloadTime(rs.getString(DOWNLOAD_TIME));
    target.setErrorMessage(rs.getString(ERROR_MESSAGE));
    target.setCls(rs.getString(CLS));
    return target;
  }
}
