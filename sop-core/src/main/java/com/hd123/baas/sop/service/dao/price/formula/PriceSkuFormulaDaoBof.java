package com.hd123.baas.sop.service.dao.price.formula;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Repository;

import com.hd123.baas.sop.service.dao.BofBaseDao;
import com.hd123.baas.sop.service.api.formula.PriceSkuFormula;
import com.hd123.rumba.commons.biz.query.Cop;
import com.hd123.rumba.commons.biz.query.QueryDefinition;
import com.hd123.rumba.commons.biz.query.QueryResult;
import com.hd123.rumba.commons.jdbc.qd.QueryProcessor;
import com.hd123.rumba.commons.jdbc.qd.QueryProcessorBuilder;
import com.hd123.rumba.commons.jdbc.sql.DeleteBuilder;
import com.hd123.rumba.commons.jdbc.sql.DeleteStatement;
import com.hd123.rumba.commons.jdbc.sql.InsertBuilder;
import com.hd123.rumba.commons.jdbc.sql.InsertStatement;
import com.hd123.rumba.commons.jdbc.sql.Predicates;
import com.hd123.rumba.commons.jdbc.sql.SelectBuilder;
import com.hd123.rumba.commons.jdbc.sql.SelectStatement;
import com.hd123.rumba.commons.lang.Assert;

/**
 * @Author maodapeng
 * @Since
 */
@Repository
public class PriceSkuFormulaDaoBof extends BofBaseDao {
  private QueryProcessor QUERY_PROCESSOR = new QueryProcessorBuilder(PriceSkuFormula.class, PPriceSkuFormula.class)
      .addConditionProcessor((condition, context) -> {
        if (condition == null) {
          return null;
        }
        String alias = context.getPerzAlias();
        if (StringUtils.equalsIgnoreCase(PriceSkuFormula.Queries.SKU_KEYWORD, condition.getOperation())) {
          String value = (String) condition.getParameter();
          SelectStatement select = new SelectBuilder().select("1") //
              .from(PPriceSkuFormula.TABLE_NAME, PPriceSkuFormula.TABLE_ALIAS) //
              .where(Predicates.or(Predicates.like(PPriceSkuFormula.TABLE_ALIAS, PPriceSkuFormula.SKU_CODE, value),
                  Predicates.like(PPriceSkuFormula.TABLE_ALIAS, PPriceSkuFormula.SKU_NAME, value)))
              .where(Predicates.equals(PPriceSkuFormula.TABLE_ALIAS, PPriceSkuFormula.TENANT, alias,
                      PPriceSkuFormula.TENANT))
              .build();
          return Predicates.exists(select);
        }
        return null;
      })
      .build();

  public void batchInsert(String tenant, List<PriceSkuFormula> formulaList) {
    Assert.notNull(tenant, "tenant");
    Assert.notEmpty(formulaList, "formulaList");
    List<InsertStatement> statements = new ArrayList<>();
    for (PriceSkuFormula formula : formulaList) {
      statements.add(buildInsertStatement(tenant, formula));
    }
    batchUpdate(statements);
  }

  public void insert(String tenant, PriceSkuFormula formula) {
    Assert.notNull(tenant, "tenant");
    Assert.notNull(formula, "formula");
    InsertStatement insertStatement = buildInsertStatement(tenant, formula);
    jdbcTemplate.update(insertStatement);
  }

  public void delete(String tenant, String uuid) {
    Assert.notNull(tenant, "tenant");
    Assert.notNull(uuid, "uuid");
    DeleteStatement delete = new DeleteBuilder().table(PPriceSkuFormula.TABLE_NAME)
        .where(Predicates.equals(PPriceSkuFormula.TENANT, tenant))
        .where(Predicates.equals(PPriceSkuFormula.UUID, uuid))
        .build();
    jdbcTemplate.update(delete);
  }

  public void deleteAll(String tenant, String orgId) {
    Assert.notNull(tenant, "tenant");
    DeleteStatement delete = new DeleteBuilder().table(PPriceSkuFormula.TABLE_NAME)
        .where(Predicates.equals(PPriceSkuFormula.TENANT, tenant))
        .where(Predicates.equals(PPriceSkuFormula.ORG_ID, orgId))
        .build();
    jdbcTemplate.update(delete);
  }

  public PriceSkuFormula getBySkuId(String tenant, String orgId, String skuId) {
    Assert.notNull(tenant, "tenant");
    Assert.notNull(skuId, "skuId");
    SelectStatement select = new SelectBuilder().from(PPriceSkuFormula.TABLE_NAME)
        .where(Predicates.equals(PPriceSkuFormula.TENANT, tenant))
        .where(Predicates.equals(PPriceSkuFormula.SKU_ID, skuId))
        .where(Predicates.equals(PPriceSkuFormula.ORG_ID, orgId))
        .build();
    return getFirst(jdbcTemplate.query(select, new PriceSkuFormulaMapper()));
  }

  public List<PriceSkuFormula> getByDependOnSkuId(String tenant, String orgId, String dependOnSkuId) {
    Assert.notNull(tenant, "tenant");
    Assert.notNull(dependOnSkuId, "dependOnSkuId");
    SelectStatement select = new SelectBuilder().from(PPriceSkuFormula.TABLE_NAME)
        .where(Predicates.equals(PPriceSkuFormula.TENANT, tenant))
        .where(Predicates.equals(PPriceSkuFormula.DEPEND_ON_SKU_ID, dependOnSkuId))
        .where(Predicates.equals(PPriceSkuFormula.ORG_ID, orgId))
        .build();
    return jdbcTemplate.query(select, new PriceSkuFormulaMapper());
  }

  public QueryResult<PriceSkuFormula> query(String tenant, QueryDefinition qd) {
    qd.addByField(PriceSkuFormula.Queries.TENANT, Cop.EQUALS, tenant);
    SelectStatement select = QUERY_PROCESSOR.process(qd);
    return executor.query(select, new PriceSkuFormulaMapper());
  }

  private InsertStatement buildInsertStatement(String tenant, PriceSkuFormula formula) {
    if (StringUtils.isBlank(formula.getUuid())) {
      formula.setUuid(UUID.randomUUID().toString());
    }
    InsertStatement insertStatement = new InsertBuilder().table(PPriceSkuFormula.TABLE_NAME)
        .addValues(PPriceSkuFormula.forSaveNew(formula))
        .addValue(PPriceSkuFormula.TENANT, tenant)
        .addValue(PPriceSkuFormula.ORG_ID, formula.getOrgId())
        .addValue(PPriceSkuFormula.SKU_ID, formula.getSkuId())
        .addValue(PPriceSkuFormula.SKU_CODE, formula.getSkuCode())
        .addValue(PPriceSkuFormula.SKU_NAME, formula.getSkuName())
        .addValue(PPriceSkuFormula.FORMULA, formula.getFormula())
        .addValue(PPriceSkuFormula.FORMULA_DESC, formula.getFormulaDesc())
        .addValue(PPriceSkuFormula.DEPEND_ON_SKU_ID, formula.getDependOnSkuId())
        .build();
    return insertStatement;
  }
}
