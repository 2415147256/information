package com.hd123.baas.sop.service.dao.faq;

import java.util.Collection;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.jdbc.core.SingleColumnRowMapper;
import org.springframework.stereotype.Repository;

import com.hd123.baas.sop.service.dao.BofBaseDao;
import com.hd123.baas.sop.service.api.faq.FaqArticleRecord;
import com.hd123.rumba.commons.biz.query.Cop;
import com.hd123.rumba.commons.biz.query.QueryDefinition;
import com.hd123.rumba.commons.biz.query.QueryResult;
import com.hd123.rumba.commons.jdbc.qd.QueryProcessor;
import com.hd123.rumba.commons.jdbc.qd.QueryProcessorBuilder;
import com.hd123.rumba.commons.jdbc.sql.InsertBuilder;
import com.hd123.rumba.commons.jdbc.sql.InsertStatement;
import com.hd123.rumba.commons.jdbc.sql.Predicates;
import com.hd123.rumba.commons.jdbc.sql.SelectBuilder;
import com.hd123.rumba.commons.jdbc.sql.SelectStatement;
import com.hd123.rumba.commons.jdbc.sql.UpdateBuilder;
import com.hd123.rumba.commons.jdbc.sql.UpdateStatement;
import com.hd123.rumba.commons.jdbc.temapper.TEMapper;
import com.hd123.rumba.commons.jdbc.temapper.TEMapperBuilder;
import com.hd123.rumba.commons.lang.Assert;
import com.qianfan123.baas.common.BaasException;

/**
 * @author liuhaoxin
 */
@Repository
public class FaqArticleRecordDaoBof extends BofBaseDao {
  private static final TEMapper<FaqArticleRecord> FAQ_ARTICLE_RECORD_TE_MAPPER = TEMapperBuilder
      .of(FaqArticleRecord.class, FaqArticleRecord.FaqArticleRecordSchema.class)
      .primaryKey(FaqArticleRecord.FaqArticleRecordSchema.UUID)
      .build();

  private final QueryProcessor QUERY_PROCESSOR = new QueryProcessorBuilder(FaqArticleRecord.class,
      FaqArticleRecord.FaqArticleRecordSchema.class).build();

  public void save(String tenant, FaqArticleRecord faqArticleRecord) throws BaasException {
    Assert.notNull(tenant, "租户");
    Assert.notNull(faqArticleRecord, "保存文章参数");
    Assert.notNull(faqArticleRecord.getUuid(), "uuid");

    InsertStatement insert = new InsertBuilder().table(FaqArticleRecord.FaqArticleRecordSchema.TABLE_NAME)
        .addValues(FAQ_ARTICLE_RECORD_TE_MAPPER.forInsert(faqArticleRecord, true))
        .build();
    int count = jdbcTemplate.update(insert);
    if (1 != count) {
      throw new BaasException("新增文章记录失败");
    }
  }

  public void delete(String tenant, FaqArticleRecord faqArticleRecord) throws BaasException {
    Assert.notNull(tenant, "租户");
    Assert.notNull(faqArticleRecord, "faqArticleRecord");
    faqArticleRecord.setDeleted(1);
    UpdateStatement update = new UpdateBuilder().table(FaqArticleRecord.FaqArticleRecordSchema.TABLE_NAME)
        .addValues(FAQ_ARTICLE_RECORD_TE_MAPPER.forUpdate(faqArticleRecord, true))
        .where(Predicates.equals(FaqArticleRecord.FaqArticleRecordSchema.UUID, faqArticleRecord.getUuid()),
            Predicates.equals(FaqArticleRecord.FaqArticleRecordSchema.TENANT, tenant))
        .build();
    int count = jdbcTemplate.update(update);
    if (1 != count) {
      throw new BaasException("删除文章记录失败");
    }
  }

  public void deleteByOwner(String tenant, String owner) {
    Assert.notNull(tenant, "tenant");
    Assert.notNull(owner, "owner");

    UpdateStatement update = new UpdateBuilder().table(FaqArticleRecord.FaqArticleRecordSchema.TABLE_NAME)
        .addValue(FaqArticleRecord.FaqArticleRecordSchema.DELETED, 1)
        .where(Predicates.equals(FaqArticleRecord.FaqArticleRecordSchema.OWNER, owner),
            Predicates.equals(FaqArticleRecord.FaqArticleRecordSchema.TENANT, tenant))
        .build();
    jdbcTemplate.update(update);
  }

  public void deleteByOwners(String tenant, Collection<String> owners) {
    Assert.notNull(tenant, "tenant");
    Assert.notNull(owners, "owners");

    UpdateStatement update = new UpdateBuilder().table(FaqArticleRecord.FaqArticleRecordSchema.TABLE_NAME)
        .addValue(FaqArticleRecord.FaqArticleRecordSchema.DELETED, 1)
        .where(Predicates.in2(FaqArticleRecord.FaqArticleRecordSchema.OWNER, owners.toArray()),
            Predicates.equals(FaqArticleRecord.FaqArticleRecordSchema.TENANT, tenant))
        .build();
    jdbcTemplate.update(update);
  }

  public void modify(String tenant, FaqArticleRecord faqArticleRecord) throws BaasException {
    Assert.notNull(tenant, "租户");
    Assert.notNull(faqArticleRecord, "修改文章记录参数");
    Assert.notNull(faqArticleRecord.getUuid(), "uuid");

    UpdateStatement update = new UpdateBuilder().table(FaqArticleRecord.FaqArticleRecordSchema.TABLE_NAME)
        .addValues(FAQ_ARTICLE_RECORD_TE_MAPPER.forUpdate(faqArticleRecord, true))
        .where(Predicates.equals(FaqArticleRecord.FaqArticleRecordSchema.UUID, faqArticleRecord.getUuid()),
            Predicates.equals(FaqArticleRecord.FaqArticleRecordSchema.TENANT, tenant))
        .build();
    int count = jdbcTemplate.update(update);
    if (1 != count) {
      throw new BaasException("修改文章记录失败!");
    }
  }

  public FaqArticleRecord getByOwner(String tenant, String articleId, String operator) {
    SelectStatement select = new SelectBuilder().from(FaqArticleRecord.FaqArticleRecordSchema.TABLE_NAME)
        .where(Predicates.equals(FaqArticleRecord.FaqArticleRecordSchema.OWNER, articleId),
            Predicates.equals(FaqArticleRecord.FaqArticleRecordSchema.OPERATOR, operator),
            Predicates.equals(FaqArticleRecord.FaqArticleRecordSchema.TENANT, tenant),
            Predicates.equals(FaqArticleRecord.FaqArticleRecordSchema.DELETED, 0))
        .build();

    return getFirst(select, FAQ_ARTICLE_RECORD_TE_MAPPER);
  }

  public QueryResult<FaqArticleRecord> query(String tenant, QueryDefinition qd) {
    Assert.hasText(tenant, "tenant");
    Assert.notNull(qd, "qd");

    qd.addByField(FaqArticleRecord.Queries.TENANT, Cop.EQUALS, tenant);
    qd.addByField(FaqArticleRecord.Queries.DELETED, Cop.EQUALS, 0);
    SelectStatement select = QUERY_PROCESSOR.process(qd);
    return executor.query(select, FAQ_ARTICLE_RECORD_TE_MAPPER);
  }

  /**
   * 统计对应帮助状态数量
   * 
   * @param tenant
   *          租户
   * @param owner
   *          文章id
   * @param help
   *          0 无帮助 1 有帮助
   * @return 帮助状态数量
   */
  public int countByOwnerAndHelp(String tenant, String owner, int help) {
    Assert.notNull(tenant, "tenant");
    Assert.notNull(owner, "articleId");
    Assert.notNull(help, "help");

    SelectStatement select = new SelectBuilder().from(FaqArticleRecord.FaqArticleRecordSchema.TABLE_NAME)
        .select("count(1)")
        .where(Predicates.equals(FaqArticleRecord.FaqArticleRecordSchema.TENANT, tenant))
        .where(Predicates.equals(FaqArticleRecord.FaqArticleRecordSchema.OWNER, owner))
        .where(Predicates.equals(FaqArticleRecord.FaqArticleRecordSchema.DELETED, 0))
        .where(Predicates.equals(FaqArticleRecord.FaqArticleRecordSchema.IS_HELP, help))
        .build();

    List<Integer> count = jdbcTemplate.query(select, new SingleColumnRowMapper<>(Integer.class));
    if (CollectionUtils.isEmpty(count)) {
      return new Integer(0);
    }
    return count.get(0);
  }

}
