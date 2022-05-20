package com.hd123.baas.sop.service.dao.pomdata;

import com.hd123.h5.pom.execseq.ExecSeqGroup;
import com.hd123.rumba.commons.jdbc.sql.DeleteBuilder;
import com.hd123.rumba.commons.jdbc.sql.DeleteStatement;
import com.hd123.rumba.commons.jdbc.sql.MultilineInsertBuilder;
import com.hd123.rumba.commons.jdbc.sql.MultilineInsertStatement;
import com.hd123.rumba.commons.jdbc.sql.Predicates;
import com.hd123.rumba.commons.jdbc.sql.SelectBuilder;
import com.hd123.rumba.commons.jdbc.sql.SelectStatement;
import com.hd123.spms.manager.dao.option.execseq.PExecSeqGroup;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author zhuangwenting
 * @since 1.0
 */
@Repository
public class ExecSeqGroupDao {
  @Autowired
  private JdbcTemplate jdbcTemplate;

  public List<ExecSeqGroup> list(String tenant) {
    SelectStatement select = new SelectBuilder().from(PExecSeqGroup.TABLE_NAME)
            .where(Predicates.equals(PExecSeqGroup.TENANT_ID, tenant))
            .build();
    return jdbcTemplate.query(select, PExecSeqGroup::mapRow);
  }

  public void save(List<ExecSeqGroup> list) {
    DeleteStatement delete = new DeleteBuilder()
            .table(PExecSeqGroup.TABLE_NAME)
            .build();
    jdbcTemplate.update(delete);

    if (list.isEmpty()) {
      return;
    }

    MultilineInsertStatement multilineInsert = new MultilineInsertBuilder()
            .table(PExecSeqGroup.TABLE_NAME)
            .build();
    for (ExecSeqGroup group : list) {
      multilineInsert.addValuesLine(PExecSeqGroup.toFieldValues(group));
    }
    jdbcTemplate.update(multilineInsert);
  }
}
