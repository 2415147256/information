package com.hd123.baas.sop.service.dao.pomdata;

import com.hd123.baas.sop.annotation.PmsTx;
import com.hd123.baas.sop.service.api.pomdata.PosSqlDataDownloadTask;
import com.hd123.baas.sop.service.api.pomdata.SqlDataDownloadCsvFile;
import com.hd123.baas.sop.service.api.pomdata.SqlDataDownloadTask;
import com.hd123.rumba.commons.jdbc.executor.BatchUpdater;
import com.hd123.rumba.commons.jdbc.sql.DeleteBuilder;
import com.hd123.rumba.commons.jdbc.sql.DeleteStatement;
import com.hd123.rumba.commons.jdbc.sql.MultilineInsertBuilder;
import com.hd123.rumba.commons.jdbc.sql.MultilineInsertStatement;
import com.hd123.rumba.commons.jdbc.sql.Predicates;
import com.hd123.rumba.commons.jdbc.sql.SelectBuilder;
import com.hd123.rumba.commons.jdbc.sql.SelectStatement;
import com.hd123.rumba.commons.jdbc.sql.UpdateBuilder;
import com.hd123.rumba.commons.jdbc.sql.UpdateStatement;
import com.hd123.rumba.commons.jdbc.sql.UpsertBuilder;
import com.hd123.rumba.commons.jdbc.sql.UpsertStatement;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public class SqlDataDownloadTaskDao {
  @Autowired
  private JdbcTemplate jdbcTemplate;

  public List<SqlDataDownloadTask> queryTasks(String tenant, List<String> posNoList) {
    if (posNoList == null || posNoList.isEmpty()) {
      return null;
    }
    SelectStatement select = new SelectBuilder()
            .select("t.*")
            .from(PPosSqlDataDownloadTask.TABLE_NAME, "p")
            .leftJoin(PSqlDataDownloadTask.TABLE_NAME, "t", Predicates.equals("t", PSqlDataDownloadTask.TASK_ID, "p", PPosSqlDataDownloadTask.TASK_ID))
            .where(Predicates.equals("p", PPosSqlDataDownloadTask.TENANT, tenant))
            .where(Predicates.in("p", PPosSqlDataDownloadTask.POS_NO, posNoList.toArray()))
            .where(Predicates.isNotNull("t", PSqlDataDownloadTask.TASK_ID))
            .build();
    return jdbcTemplate.query(select, PSqlDataDownloadTask::mapRow);
  }

  @PmsTx
  public void createTasks(List<SqlDataDownloadTask> tasks) {
    if (tasks == null || tasks.isEmpty()) {
      return;
    }
    MultilineInsertStatement multilineInsert = new MultilineInsertBuilder()
            .table(PSqlDataDownloadTask.TABLE_NAME)
            .build();
    for (SqlDataDownloadTask task : tasks) {
      multilineInsert.addValuesLine(PSqlDataDownloadTask.toFieldValues(task));
    }
    jdbcTemplate.update(multilineInsert);
  }

  @PmsTx
  public void createPosTasks(List<PosSqlDataDownloadTask> tasks) {
    if (tasks == null || tasks.isEmpty()) {
      return;
    }

    BatchUpdater batchUpdater = new BatchUpdater(jdbcTemplate);
    for (PosSqlDataDownloadTask task : tasks) {
      UpsertStatement update = new UpsertBuilder()
              .table(PPosSqlDataDownloadTask.TABLE_NAME)
              .addValues(PPosSqlDataDownloadTask.toFieldValues(task))
              .build();
      jdbcTemplate.update(update);
      batchUpdater.add(update);
    }
    batchUpdater.update();
  }

  @PmsTx
  public void updateTasks(List<SqlDataDownloadTask> tasks) {
    if (tasks == null || tasks.isEmpty()) {
      return;
    }
    BatchUpdater batchUpdater = new BatchUpdater(jdbcTemplate);
    for (SqlDataDownloadTask task : tasks) {
      UpdateStatement statement = new UpdateBuilder()
              .table(PSqlDataDownloadTask.TABLE_NAME)
              .addValues(PSqlDataDownloadTask.toFieldValues(task))
              .where(Predicates.equals(PSqlDataDownloadTask.TASK_ID, task.getTaskId()))
              .build();
      batchUpdater.add(statement);
    }
    batchUpdater.update();
  }

  @PmsTx
  public void updateStateBySourceId(String sourceId, int state) {
    UpdateStatement update = new UpdateBuilder()
            .table(PSqlDataDownloadTask.TABLE_NAME)
            .setValue(PSqlDataDownloadTask.STATE, state)
            .where(Predicates.equals(PSqlDataDownloadTask.SOURCE_ID, sourceId))
            .build();
    jdbcTemplate.update(update);
  }

  @PmsTx
  public void createCsvFiles(List<SqlDataDownloadCsvFile> csvFiles) {
    if (csvFiles == null || csvFiles.isEmpty()) {
      return;
    }
    MultilineInsertStatement multilineInsert = new MultilineInsertBuilder()
            .table(PSqlDataDownloadCsvFile.TABLE_NAME)
            .build();
    for (SqlDataDownloadCsvFile task : csvFiles) {
      multilineInsert.addValuesLine(PSqlDataDownloadCsvFile.toFieldValues(task));
    }
    jdbcTemplate.update(multilineInsert);
  }

  @PmsTx
  public void cleanValid(List<String> posNoList) {
    Date time = DateUtils.addMonths(new Date(), -1);
    DeleteStatement statement = new DeleteBuilder()
            .tableAndAlias(PSqlDataDownloadCsvFile.TABLE_NAME, "c")
            .where(Predicates.in("c", PSqlDataDownloadCsvFile.TASK_ID,
                                 new SelectBuilder()
                                         .select("t.taskId")
                                         .from(PSqlDataDownloadTask.TABLE_NAME, "t")
                                         .where(Predicates.notEquals("t", PSqlDataDownloadTask.STATE, 0))
                                         .where(Predicates.in("t", PSqlDataDownloadTask.POS_NO, posNoList.toArray()))
                                         .where(Predicates.less("t", PSqlDataDownloadTask.CREATE_TIME, time))
                                         .build()))
            .build();
    jdbcTemplate.update(statement);

    statement = new DeleteBuilder()
            .table(PSqlDataDownloadTask.TABLE_NAME)
            .where(Predicates.notEquals(PSqlDataDownloadTask.STATE, 0))
            .where(Predicates.in2(PSqlDataDownloadTask.POS_NO, posNoList.toArray()))
            .where(Predicates.less(PSqlDataDownloadTask.CREATE_TIME, time))
            .build();
    jdbcTemplate.update(statement);

  }
}
