package com.hd123.baas.sop.service.dao.grade;

import java.util.Collection;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Repository;

import com.hd123.baas.sop.service.dao.BofBaseDao;
import com.hd123.baas.sop.service.api.entity.PriceGrade;
import com.hd123.baas.sop.service.dao.announcement.PAnnouncement;
import com.hd123.rumba.commons.biz.query.Cop;
import com.hd123.rumba.commons.biz.query.QueryDefinition;
import com.hd123.rumba.commons.biz.query.QueryResult;
import com.hd123.rumba.commons.jdbc.executor.BatchUpdater;
import com.hd123.rumba.commons.jdbc.qd.QueryProcessor;
import com.hd123.rumba.commons.jdbc.qd.QueryProcessorBuilder;
import com.hd123.rumba.commons.jdbc.sql.*;
import com.hd123.rumba.commons.lang.Assert;

@Repository
public class PriceGradeDaoBof extends BofBaseDao {
  private QueryProcessor QUERY_PROCESSOR = new QueryProcessorBuilder(PriceGrade.class, PPriceGrade.class).build();

  public int insert(String tenant, PriceGrade priceGrade) {
    Assert.notNull(priceGrade);
    Assert.notNull(tenant, "租戶");
    Assert.notNull(priceGrade.getName(), "名称");
    InsertStatement insert = buildInsert(tenant, priceGrade);
    return jdbcTemplate.update(insert);
  }

  public void batchInsert(String tenant, List<PriceGrade> priceGrades) {
    Assert.notNull(tenant, "租戶");
    Assert.notEmpty(priceGrades);
    BatchUpdater updater = new BatchUpdater(jdbcTemplate);
    for (PriceGrade grade : priceGrades) {
      InsertStatement insertStatement = buildInsert(tenant, grade);
      updater.add(insertStatement);
    }
    updater.update();
  }

  public int update(String tenant, PriceGrade priceGrade) {
    Assert.notNull(priceGrade);
    Assert.notNull(tenant, "租戶");
    Assert.notNull(priceGrade.getUuid(), "uuid");
    Assert.notNull(priceGrade.getName(), "名称");
    UpdateStatement update = new UpdateBuilder().table(PPriceGrade.TABLE_NAME)
        .setValue(PPriceGrade.NAME, priceGrade.getName())
        .where(Predicates.equals(PPriceGrade.TENANT, tenant))
        .where(Predicates.equals(PPriceGrade.UUID, priceGrade.getUuid()))
        .build();
    return jdbcTemplate.update(update);
  }

  public void batchUpdate(String tenant, List<PriceGrade> priceGrades) {
    Assert.notNull(tenant, "租戶");
    Assert.notEmpty(priceGrades);
    BatchUpdater updater = new BatchUpdater(jdbcTemplate);
    for (PriceGrade priceGrade : priceGrades) {
      Assert.notNull(priceGrade.getUuid(), "uuid");
      Assert.notNull(priceGrade.getName(), "名称");
      UpdateStatement update = buildUpdate(tenant, priceGrade);
      updater.add(update);
    }
    updater.update();
  }

  public void setDefault(String tenant, List<PriceGrade> priceGrades) {
    Assert.notNull(tenant, "租戶");
    Assert.notEmpty(priceGrades);
    BatchUpdater updater = new BatchUpdater(jdbcTemplate);
    for (PriceGrade priceGrade : priceGrades) {
      Assert.notNull(priceGrade.getDft(), "dft");
      UpdateStatement update = new UpdateBuilder().table(PPriceGrade.TABLE_NAME)
          .setValue(PPriceGrade.DFT, priceGrade.getDft())
          .where(Predicates.equals(PPriceGrade.TENANT, tenant))
          .where(Predicates.equals(PPriceGrade.UUID, priceGrade.getUuid()))
          .build();
      updater.add(update);
    }
    updater.update();
  }

  public void delete(String tenant, List<Integer> uuids) {
    Assert.notNull(tenant, "租戶");
    Assert.notNull(uuids, "uuids");
    DeleteStatement delete = new DeleteBuilder().table(PPriceGrade.TABLE_NAME)
        .where(Predicates.in2(PPriceGrade.UUID, uuids.toArray()))
        .where(Predicates.equals(PPriceGrade.TENANT, tenant))
        .build();
    jdbcTemplate.update(delete);
  }

  public List<PriceGrade> list(String tenant, String orgId) {
    Assert.notNull(tenant, "租戶");
    SelectStatement select = new SelectBuilder().select(PPriceGrade.allColumns())
        .from(PPriceGrade.TABLE_NAME)
        .where(Predicates.equals(PPriceGrade.TENANT, tenant))
        .orderBy(PPriceGrade.SEQ, PPriceGrade.UUID)
        .build();
    if (StringUtils.isNotBlank(orgId)) {
      select.where(Predicates.equals(PPriceGrade.ORG_ID, orgId));
    }
    return jdbcTemplate.query(select, new PriceGradeMapper());
  }

  public QueryResult<PriceGrade> query(String tenant, QueryDefinition qd) {
    Assert.hasLength(tenant, "tenant");
    Assert.notNull(qd, "qd");
    qd.addByField(PriceGrade.Queries.TENANT, Cop.EQUALS, tenant);
    SelectStatement select = QUERY_PROCESSOR.process(qd);
    return executor.query(select, new PriceGradeMapper());
  }

  public List<PriceGrade> query(String tenant, String orgId, Collection<String> names) {
    Assert.hasLength(tenant, "tenant");
    Assert.notNull(names, "names");
    QueryDefinition qd = new QueryDefinition();
    qd.addByField(PriceGrade.Queries.ORG_ID, Cop.EQUALS, orgId);
    qd.addByField(PriceGrade.Queries.NAME, Cop.IN, names.toArray());
    SelectStatement select = QUERY_PROCESSOR.process(qd);
    return executor.query(select, new PriceGradeMapper()).getRecords();
  }

  public PriceGrade getDftPriceGrade(String tenant, String orgId) {
    QueryDefinition gradeQd = new QueryDefinition();
    gradeQd.addByField(PriceGrade.Queries.DFT, Cop.EQUALS, true);
    if (orgId != null) {
      gradeQd.addByField(PriceGrade.Queries.ORG_ID, Cop.EQUALS, orgId);
    }
    QueryResult<PriceGrade> query = query(tenant, gradeQd);
    PriceGrade first = getFirst(query.getRecords());
    return first;
  }

  public PriceGrade query(String tenant, Integer priceGradeId) {
    Assert.notNull(priceGradeId, "priceGradeId");
    QueryDefinition gradeQd = new QueryDefinition();
    gradeQd.addByField(PriceGrade.Queries.UUID, Cop.EQUALS, priceGradeId);
    QueryResult<PriceGrade> query = query(tenant, gradeQd);
    PriceGrade first = getFirst(query.getRecords());
    return first;
  }

  public List<PriceGrade> queryByIds(String tenant, List<Integer> priceGradeIds) {
    Assert.notEmpty(priceGradeIds);
    QueryDefinition gradeQd = new QueryDefinition();
    gradeQd.addByField(PriceGrade.Queries.UUID, Cop.IN, priceGradeIds.toArray());
    QueryResult<PriceGrade> query = query(tenant, gradeQd);
    return query.getRecords();
  }

  private UpdateStatement buildUpdate(String tenant, PriceGrade priceGrade) {
    UpdateStatement update = new UpdateBuilder().table(PPriceGrade.TABLE_NAME)
        .setValue(PPriceGrade.NAME, priceGrade.getName())
        .setValue(PPriceGrade.SEQ, priceGrade.getSeq())
        .where(Predicates.equals(PPriceGrade.TENANT, tenant))
        .where(Predicates.equals(PPriceGrade.UUID, priceGrade.getUuid()))
        .build();
    return update;
  }

  private InsertStatement buildInsert(String tenant, PriceGrade priceGrade) {
    InsertStatement insert = new InsertBuilder().table(PPriceGrade.TABLE_NAME)
        .addValue(PPriceGrade.TENANT, tenant)
        .addValue(PPriceGrade.ORG_ID, priceGrade.getOrgId())
        .addValue(PPriceGrade.UUID, priceGrade.getUuid())
        .addValue(PPriceGrade.NAME, priceGrade.getName())
        .addValue(PPriceGrade.SEQ, priceGrade.getSeq())
        .addValue(PPriceGrade.DFT, priceGrade.getDft())
        .build();
    return insert;
  }

  public PriceGrade get(String tenant, String uuid) {
    Assert.hasLength(tenant, "tenant");
    Assert.notNull(uuid, "uuid");

    SelectStatement select = new SelectBuilder().select(PPriceGrade.allColumns())
        .from(PPriceGrade.TABLE_NAME)
        .where(Predicates.equals(PAnnouncement.TENANT, tenant))
        .where(Predicates.equals(PAnnouncement.UUID, uuid))
        .build();
    return getFirst(jdbcTemplate.query(select, new PriceGradeMapper()));

  }

}
