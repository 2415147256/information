package com.hd123.baas.sop.service.dao.faq;

import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.jdbc.core.SingleColumnRowMapper;
import org.springframework.stereotype.Repository;

import com.hd123.baas.sop.service.dao.BofBaseDao;
import com.hd123.baas.sop.service.api.faq.FaqArticle;
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
public class FaqArticleDaoBof extends BofBaseDao {

  private static final TEMapper<FaqArticle> FAQ_ARTICLE_TE_MAPPER = TEMapperBuilder
      .of(FaqArticle.class, FaqArticle.FaqArticleSchema.class)
      .primaryKey(FaqArticle.FaqArticleSchema.UUID)
      .build();

  private final QueryProcessor QUERY_PROCESSOR = new QueryProcessorBuilder(FaqArticle.class,
      FaqArticle.FaqArticleSchema.class).addConditionProcessor((condition, context) -> {
        String alias = context.getPerzAlias();
        // 文章id和文章名称查询
        if (StringUtils.equalsIgnoreCase(FaqArticle.Queries.ARTICLE_KEYWORD_LIKE, condition.getOperation())) {
          String value = (String) condition.getParameter();
          SelectStatement select = new SelectBuilder().select("1") //
              .from(FaqArticle.FaqArticleSchema.TABLE_NAME, FaqArticle.FaqArticleSchema.TABLE_ALIAS) //
              .where(Predicates.or(
                  // 文章id
                  Predicates.like(FaqArticle.FaqArticleSchema.TABLE_ALIAS, FaqArticle.FaqArticleSchema.ARTICLE_ID,
                      value),
                  // 文章名称
                  Predicates.like(FaqArticle.FaqArticleSchema.TABLE_ALIAS, FaqArticle.FaqArticleSchema.ARTICLE_NAME,
                      value)))
              .where(Predicates.equals(FaqArticle.FaqArticleSchema.TABLE_ALIAS, FaqArticle.FaqArticleSchema.UUID, alias,
                  FaqArticle.FaqArticleSchema.UUID))
              .where(Predicates.equals(FaqArticle.FaqArticleSchema.TABLE_ALIAS, FaqArticle.FaqArticleSchema.TENANT,
                  alias, FaqArticle.FaqArticleSchema.TENANT))
              .build();
          return Predicates.exists(select);
        }
        // 文章名称和文章内容查询
        if (StringUtils.equalsIgnoreCase(FaqArticle.Queries.ARTICLE_XCX_KEYWORD_LIKE, condition.getOperation())) {
          String value = (String) condition.getParameter();
          SelectStatement select = new SelectBuilder().select("1")
              .from(FaqArticle.FaqArticleSchema.TABLE_NAME, FaqArticle.FaqArticleSchema.TABLE_ALIAS)
              .where(Predicates.or(
                  // 文章名称
                  Predicates.like(FaqArticle.FaqArticleSchema.TABLE_ALIAS, FaqArticle.FaqArticleSchema.ARTICLE_NAME,
                      value),
                  // 文章内容
                  Predicates.like(FaqArticle.FaqArticleSchema.TABLE_ALIAS, FaqArticle.FaqArticleSchema.CONTENT, value)))
              .where(Predicates.equals(FaqArticle.FaqArticleSchema.TABLE_ALIAS, FaqArticle.FaqArticleSchema.UUID, alias,
                  FaqArticle.FaqArticleSchema.UUID))
              .where(Predicates.equals(FaqArticle.FaqArticleSchema.TABLE_ALIAS, FaqArticle.FaqArticleSchema.TENANT,
                  alias, FaqArticle.FaqArticleSchema.TENANT))
              .build();
          return Predicates.exists(select);
        }
        // 创建人或创建代码模糊查询
        if (StringUtils.equalsIgnoreCase(FaqArticle.Queries.CREATOR_KEYWORD_LIKE, condition.getOperation())) {
          String value = (String) condition.getParameter();
          SelectStatement select = new SelectBuilder().select("1") //
              .from(FaqArticle.FaqArticleSchema.TABLE_NAME, FaqArticle.FaqArticleSchema.TABLE_ALIAS) //
              .where(Predicates.or(
                  // 创建人
                  Predicates.like(FaqArticle.FaqArticleSchema.TABLE_ALIAS,
                      FaqArticle.FaqArticleSchema.CREATE_INFO_OPERATOR_FULL_NAME, value),
                  // 创建代码
                  Predicates.like(FaqArticle.FaqArticleSchema.TABLE_ALIAS,
                      FaqArticle.FaqArticleSchema.CREATE_INFO_OPERATOR_ID, value)))
              .where(Predicates.equals(FaqArticle.FaqArticleSchema.TABLE_ALIAS, FaqArticle.FaqArticleSchema.UUID, alias,
                  FaqArticle.FaqArticleSchema.UUID))
              .where(Predicates.equals(FaqArticle.FaqArticleSchema.TABLE_ALIAS, FaqArticle.FaqArticleSchema.TENANT,
                  alias, FaqArticle.FaqArticleSchema.TENANT))
              .build();
          return Predicates.exists(select);
        }
        return null;
      }).build();

  public void save(String tenant, FaqArticle faqArticle) throws BaasException {
    Assert.notNull(tenant, "租户");
    Assert.notNull(faqArticle, "保存文章参数");
    Assert.notNull(faqArticle.getUuid(), "uuid");

    InsertStatement insert = new InsertBuilder().table(FaqArticle.FaqArticleSchema.TABLE_NAME)
        .addValues(FAQ_ARTICLE_TE_MAPPER.forInsert(faqArticle, true))
        .build();
    int count = jdbcTemplate.update(insert);
    if (1 != count) {
      throw new BaasException("新增文章失败");
    }
  }

  /**
   * 逻辑删除faq分类
   *
   * @param tenant
   *          租户标记
   * @param articleId
   *          分类id
   * @return
   */
  public void delete(String tenant, String articleId) throws BaasException {
    Assert.notNull(tenant, "租户");
    Assert.notNull(articleId, "articleId");

    UpdateStatement update = new UpdateBuilder().table(FaqArticle.FaqArticleSchema.TABLE_NAME)
        .addValue(FaqArticle.FaqArticleSchema.DELETED, 1)
        .where(Predicates.equals(FaqArticle.FaqArticleSchema.ARTICLE_ID, articleId),
            Predicates.equals(FaqArticle.FaqArticleSchema.TENANT, tenant))
        .build();
    int count = jdbcTemplate.update(update);
    if (1 != count) {
      throw new BaasException("删除文章失败");
    }
  }

  public void modify(String tenant, FaqArticle faqArticle) throws BaasException {
    Assert.notNull(tenant, "租户");
    Assert.notNull(faqArticle, "修改文章参数");
    Assert.notNull(faqArticle.getArticleId(), "faqArticle");

    UpdateStatement update = new UpdateBuilder().table(FaqArticle.FaqArticleSchema.TABLE_NAME)
        .addValues(FAQ_ARTICLE_TE_MAPPER.forUpdate(faqArticle, true))
        .where(Predicates.equals(FaqArticle.FaqArticleSchema.ARTICLE_ID, faqArticle.getArticleId()),
            Predicates.equals(FaqArticle.FaqArticleSchema.TENANT, tenant))
        .build();
    int count = jdbcTemplate.update(update);
    if (1 != count) {
      throw new BaasException("修改文章失败!");
    }
  }

  public FaqArticle get(String tenant, String articleId) throws BaasException {
    SelectStatement select = new SelectBuilder().from(FaqArticle.FaqArticleSchema.TABLE_NAME)
        .where(Predicates.equals(FaqArticle.FaqArticleSchema.TENANT, tenant),
            Predicates.equals(FaqArticle.FaqArticleSchema.ARTICLE_ID, articleId),
            Predicates.equals(FaqArticle.FaqArticleSchema.DELETED, 0))
        .build();
    return getFirst(select, FAQ_ARTICLE_TE_MAPPER);
  }

  public QueryResult<FaqArticle> query(String tenant, QueryDefinition qd) {
    Assert.hasText(tenant, "tenant");
    Assert.notNull(qd, "qd");

    qd.addByField(FaqArticle.Queries.TENANT, Cop.EQUALS, tenant);
    qd.addByField(FaqArticle.Queries.DELETED, Cop.EQUALS, 0);
    SelectStatement select = QUERY_PROCESSOR.process(qd);
    return executor.query(select, FAQ_ARTICLE_TE_MAPPER);
  }

  /**
   * 批量删除文章
   * 
   * @param tenant
   *          租户
   * @param owner
   *          分类id
   */
  public void deleteByOwner(String tenant, String owner) {
    Assert.notNull(tenant, "tenant");
    Assert.notNull(owner, "owner");

    UpdateStatement update = new UpdateBuilder().table(FaqArticle.FaqArticleSchema.TABLE_NAME)
        .addValue(FaqArticle.FaqArticleSchema.DELETED, 1)
        .where(Predicates.equals(FaqArticle.FaqArticleSchema.OWNER, owner),
            Predicates.equals(FaqArticle.FaqArticleSchema.TENANT, tenant))
        .build();
    int count = jdbcTemplate.update(update);
  }

  /**
   * 统计文章 数量
   * 
   * @param tenant
   *          租户
   * @param articleName
   *          文章名称
   * @return
   */
  public int countByArticleName(String tenant, String articleId, String articleName) {
    Assert.notNull(tenant, "tenant");
    Assert.notNull(articleName, "articleName");
    SelectStatement select = new SelectBuilder().from(FaqArticle.FaqArticleSchema.TABLE_NAME)
        .select("count(1)")
        .where(Predicates.equals(FaqArticle.FaqArticleSchema.TENANT, tenant))
        .where(Predicates.equals(FaqArticle.FaqArticleSchema.ARTICLE_NAME, articleName))
        .where(Predicates.equals(FaqArticle.FaqArticleSchema.DELETED, 0))
        .build();
    if (StringUtils.isNotBlank(articleId)) {
      select.where(Predicates.not(Predicates.equals(FaqArticle.FaqArticleSchema.ARTICLE_ID, articleId)));
    }
    List<Integer> count = jdbcTemplate.query(select, new SingleColumnRowMapper<>(Integer.class));
    if (CollectionUtils.isEmpty(count)) {
      return new Integer(0);
    }
    return count.get(0);
  }
}
