package com.hd123.baas.sop.service.dao.faq;

import java.util.Collection;
import java.util.List;

import org.springframework.stereotype.Repository;

import com.hd123.baas.sop.service.dao.BofBaseDao;
import com.hd123.baas.sop.service.api.faq.FaqCategory;
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
public class FaqCategoryDaoBof extends BofBaseDao {

  private static final TEMapper<FaqCategory> FAQ_CATEGORY_TE_MAPPER = TEMapperBuilder
      .of(FaqCategory.class, FaqCategory.FaqCategorySchema.class)
      .primaryKey(FaqCategory.FaqCategorySchema.UUID)
      .build();

  private final QueryProcessor QUERY_PROCESSOR = new QueryProcessorBuilder(FaqCategory.class,
      FaqCategory.FaqCategorySchema.class).build();

  public void save(String tenant, FaqCategory category) throws BaasException {
    Assert.notNull(tenant, "租户");
    Assert.notNull(category, "保存分类参数");
    Assert.notNull(category.getUuid(), "uuid");

    InsertStatement insert = new InsertBuilder().table(FaqCategory.FaqCategorySchema.TABLE_NAME)
        .addValues(FAQ_CATEGORY_TE_MAPPER.forInsert(category, true))
        .build();
    int count = jdbcTemplate.update(insert);
    if (1 != count) {
      throw new BaasException("新增分类失败");
    }
  }

  /**
   * 逻辑删除faq分类
   *
   * @param tenant
   *          租户标记
   * @param faqCategory
   *          分类信息
   * @return
   */
  public void delete(String tenant, FaqCategory faqCategory) throws BaasException {
    Assert.notNull(tenant, "租户");
    Assert.notNull(faqCategory.getUuid(), "uuid");

    UpdateStatement update = new UpdateBuilder().table(FaqCategory.FaqCategorySchema.TABLE_NAME)
        .addValues(FAQ_CATEGORY_TE_MAPPER.forUpdate(faqCategory, true))
        .where(Predicates.equals(FaqCategory.FaqCategorySchema.UUID, faqCategory.getUuid()),
            Predicates.equals(FaqCategory.FaqCategorySchema.TENANT, tenant))
        .build();
    int count = jdbcTemplate.update(update);
    if (1 != count) {
      throw new BaasException("删除分类失败");
    }
  }

  public void modify(String tenant, FaqCategory faqCategory) throws BaasException {
    Assert.notNull(tenant, "租户");
    Assert.notNull(faqCategory, "保存分类参数");
    Assert.notNull(faqCategory.getUuid(), "uuid");

    UpdateStatement update = new UpdateBuilder().table(FaqCategory.FaqCategorySchema.TABLE_NAME)
        .addValues(FAQ_CATEGORY_TE_MAPPER.forUpdate(faqCategory, true))
        .where(Predicates.equals(FaqCategory.FaqCategorySchema.UUID, faqCategory.getUuid()),
            Predicates.equals(FaqCategory.FaqCategorySchema.TENANT, tenant))
        .build();
    int count = jdbcTemplate.update(update);
    if (1 != count) {
      throw new BaasException("修改分类失败!");
    }
  }

  public FaqCategory get(String tenant, String categoryId) throws BaasException {
    Assert.hasText(tenant, "tenant");

    SelectStatement select = new SelectBuilder().from(FaqCategory.FaqCategorySchema.TABLE_NAME)
        .where(Predicates.equals(FaqCategory.FaqCategorySchema.TENANT, tenant),
            Predicates.equals(FaqCategory.FaqCategorySchema.CATEGORY_ID, categoryId),
            Predicates.equals(FaqCategory.FaqCategorySchema.DELETED, 0))
        .build();
    return getFirst(select, FAQ_CATEGORY_TE_MAPPER);
  }

  public QueryResult<FaqCategory> query(String tenant, QueryDefinition qd) {
    Assert.hasText(tenant, "tenant");
    Assert.notNull(qd, "qd");

    qd.addByField(FaqCategory.Queries.TENANT, Cop.EQUALS, tenant);
    qd.addByField(FaqCategory.Queries.DELETED, Cop.EQUALS, 0);
    SelectStatement select = QUERY_PROCESSOR.process(qd);
    return executor.query(select, FAQ_CATEGORY_TE_MAPPER);
  }

  public List<FaqCategory> listByName(String tenant, String categoryName) {
    SelectStatement select = new SelectBuilder().from(FaqCategory.FaqCategorySchema.TABLE_NAME)
        .where(Predicates.equals(FaqCategory.FaqCategorySchema.TENANT, tenant),
            Predicates.equals(FaqCategory.FaqCategorySchema.CATEGORY_NAME, categoryName),
            Predicates.equals(FaqCategory.FaqCategorySchema.DELETED, 0))
        .build();
    List<FaqCategory> list = jdbcTemplate.query(select, FAQ_CATEGORY_TE_MAPPER);
    return list;
  }

  public List<FaqCategory> listById(String tenant, Collection<String> categoryIds) {
    SelectStatement select = new SelectBuilder().from(FaqCategory.FaqCategorySchema.TABLE_NAME)
        .where(Predicates.equals(FaqCategory.FaqCategorySchema.TENANT, tenant),
            Predicates.in2(FaqCategory.FaqCategorySchema.CATEGORY_ID, categoryIds.toArray()),
            Predicates.equals(FaqCategory.FaqCategorySchema.DELETED, 0))
        .build();
    List<FaqCategory> list = jdbcTemplate.query(select, FAQ_CATEGORY_TE_MAPPER);
    return list;
  }
}
