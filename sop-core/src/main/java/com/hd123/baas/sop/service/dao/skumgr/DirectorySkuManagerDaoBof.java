package com.hd123.baas.sop.service.dao.skumgr;

import java.util.Date;
import java.util.List;

import org.springframework.stereotype.Repository;

import com.hd123.baas.sop.service.dao.BofBaseDao;
import com.hd123.baas.sop.service.api.skumgr.DirectorySkuManager;
import com.hd123.rumba.commons.biz.query.Cop;
import com.hd123.rumba.commons.biz.query.QueryDefinition;
import com.hd123.rumba.commons.biz.query.QueryResult;
import com.hd123.rumba.commons.jdbc.executor.BatchUpdater;
import com.hd123.rumba.commons.jdbc.qd.QueryProcessor;
import com.hd123.rumba.commons.jdbc.qd.QueryProcessorBuilder;
import com.hd123.rumba.commons.jdbc.sql.*;
import com.hd123.rumba.commons.lang.Assert;

/**
 * @Author maodapeng
 * @Since
 */
@Repository
public class DirectorySkuManagerDaoBof extends BofBaseDao {
  private QueryProcessor QUERY_PROCESSOR = new QueryProcessorBuilder(DirectorySkuManager.class, PDirectoryManager.class)
      .build();

  public void insert(String tenant, List<DirectorySkuManager> directorySkuManager) {
    Assert.hasText(tenant, "tenant");
    Assert.notEmpty(directorySkuManager, "shopSkuManager");
    BatchUpdater batchUpdater = new BatchUpdater(jdbcTemplate);
    for (DirectorySkuManager skuManager : directorySkuManager) {
      InsertStatement insert = new InsertBuilder().table(PDirectoryManager.TABLE_NAME)
          .addValues(PDirectoryManager.getBizMap(tenant, skuManager))
          .build();
      batchUpdater.add(insert);
    }
    batchUpdater.update();
  }

  public void deleteBeforeDate(String tenant, Date date) {
    Assert.hasText(tenant, "tenant");
    Assert.notNull(date, "date");
    DeleteStatement deleteStatement = new DeleteBuilder().table(PDirectoryManager.TABLE_NAME)
        .where(Predicates.equals(PDirectoryManager.TENANT, tenant))
        .where(Predicates.less(PDirectoryManager.ISSUE_DATE, date))
        .build();
    jdbcTemplate.update(deleteStatement);
  }

  public QueryResult<DirectorySkuManager> query(String tenant, QueryDefinition qd) {
    qd.addByField(DirectorySkuManager.Queries.TENANT, Cop.EQUALS, tenant);
    SelectStatement select = QUERY_PROCESSOR.process(qd);
    return executor.query(select, new DirectorySkuManagerMapper());
  }
}
