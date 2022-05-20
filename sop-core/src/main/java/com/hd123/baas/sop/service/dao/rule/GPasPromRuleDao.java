package com.hd123.baas.sop.service.dao.rule;

import com.hd123.baas.sop.annotation.PmsTx;
import com.hd123.baas.sop.service.api.pms.gpas.rule.GPasPromRule;
import com.hd123.baas.sop.service.api.promotion.FavorSharing;
import com.hd123.baas.sop.service.api.promotion.PromotionJoinUnits;
import com.hd123.rumba.commons.biz.query.Cop;
import com.hd123.rumba.commons.biz.query.QueryDefinition;
import com.hd123.rumba.commons.biz.query.QueryResult;
import com.hd123.rumba.commons.jdbc.executor.JdbcPagingQueryExecutor;
import com.hd123.rumba.commons.jdbc.qd.QueryProcessor;
import com.hd123.rumba.commons.jdbc.qd.QueryProcessorBuilder;
import com.hd123.rumba.commons.jdbc.sql.DeleteBuilder;
import com.hd123.rumba.commons.jdbc.sql.DeleteStatement;
import com.hd123.rumba.commons.jdbc.sql.InsertBuilder;
import com.hd123.rumba.commons.jdbc.sql.InsertStatement;
import com.hd123.rumba.commons.jdbc.sql.MultilineInsertBuilder;
import com.hd123.rumba.commons.jdbc.sql.MultilineInsertStatement;
import com.hd123.rumba.commons.jdbc.sql.Predicates;
import com.hd123.rumba.commons.jdbc.sql.SelectBuilder;
import com.hd123.rumba.commons.jdbc.sql.SelectStatement;
import com.hd123.rumba.commons.jdbc.sql.UpdateBuilder;
import com.hd123.rumba.commons.jdbc.sql.UpdateStatement;
import com.hd123.spms.commons.calendar.DateRange;
import com.hd123.spms.commons.format.DateTimeFormat;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Repository
public class GPasPromRuleDao {

  private static final QueryProcessor QUERY_PROCESSOR = new QueryProcessorBuilder(GPasPromRule.class, PGPasPromRule.class)
          .addConditionProcessor((condition, context) -> {
            if (GPasPromRule.Queries.JOIN_UNITS.equals(condition.getField())) {
              if (Cop.LIKES.equals(condition.getOperator())) {
                return Predicates.or(
                        Predicates.equals(PGPasPromRule.JOIN_UNITS_ALL_UNIT, true),
                        Predicates.exists(new SelectBuilder()
                                .select(PGPasPromRuleJoinUnits.RULE_UUID)
                                .from(PGPasPromRuleJoinUnits.TABLE_NAME)
                                .where(Predicates.equals(PGPasPromRuleJoinUnits.TABLE_NAME, PGPasPromRuleJoinUnits.RULE_UUID, context.getPerzAlias(), PGPasPromRule.UUID))
                                .where(Predicates.or(
                                        Predicates.like(PGPasPromRuleJoinUnits.TABLE_NAME, PGPasPromRuleJoinUnits.JOIN_UNIT_NAME, condition.getParameter()),
                                        Predicates.like(PGPasPromRuleJoinUnits.TABLE_NAME, PGPasPromRuleJoinUnits.JOIN_UNIT_CODE, condition.getParameter())
                                )).build()));
              }
            }
            if (GPasPromRule.Queries.PROMOTION.equals(condition.getField())) {
              if (Cop.LIKES.equals(condition.getOperator())) {
                return Predicates.and(Predicates.exists(new SelectBuilder()
                        .select(PGPasPromRuleProduct.RULE_UUID)
                        .from(PGPasPromRuleProduct.TABLE_NAME)
                        .where(Predicates.equals(PGPasPromRuleProduct.TABLE_NAME, PGPasPromRuleJoinUnits.RULE_UUID, context.getPerzAlias(), PGPasPromRule.UUID))
                        .where(Predicates.or(
                                Predicates.like(PGPasPromRuleProduct.TABLE_NAME, PGPasPromRuleProduct.ENTITY_UNIT_CODE, condition.getParameter()),
                                Predicates.like(PGPasPromRuleProduct.TABLE_NAME, PGPasPromRuleProduct.ENTITY_UNIT_NAME, condition.getParameter())
                        )).build()));
              }
            }
            return null;
          }).build();

  @Autowired
  private JdbcTemplate jdbcTemplate;

  @PmsTx
  public void create(GPasPromRule gPasPromRule) {
    Map<String, Object> activityMap = PGPasPromRule.toFieldValues(gPasPromRule);
    InsertStatement insert = new InsertBuilder()
            .table(PGPasPromRule.TABLE_NAME)
            .addValues(activityMap)
            .build();
    jdbcTemplate.update(insert);

    if (CollectionUtils.isEmpty(gPasPromRule.getFavorSharings()) == false) {
      MultilineInsertStatement multilineInsertStatement = new MultilineInsertBuilder()
              .table(PGPasPromRuleFavorSharing.TABLE_NAME).build();
      gPasPromRule.getFavorSharings().forEach(favorSharing -> {
        multilineInsertStatement.addValuesLine(PGPasPromRuleFavorSharing.toFieldValues((String) activityMap.get(PGPasPromRule.UUID), favorSharing));
      });
      jdbcTemplate.update(multilineInsertStatement);
    }

    if (gPasPromRule.getJoinUnits() != null && CollectionUtils.isEmpty(gPasPromRule.getJoinUnits().getStores()) == false) {
      MultilineInsertStatement multilineInsertStatement = new MultilineInsertBuilder()
              .table(PGPasPromRuleJoinUnits.TABLE_NAME).build();
      gPasPromRule.getJoinUnits().getStores().forEach(ucn -> {
        multilineInsertStatement.addValuesLine(PGPasPromRuleJoinUnits.toFieldValues((String) activityMap.get(PGPasPromRule.UUID), ucn));
      });
      jdbcTemplate.update(multilineInsertStatement);
    }
    if (gPasPromRule.getPromotion() != null && gPasPromRule.getPromotion().getProductCondition() != null
            && CollectionUtils.isEmpty(gPasPromRule.getPromotion().getProductCondition().getItems()) == false) {
      MultilineInsertStatement multilineInsertStatement = new MultilineInsertBuilder()
              .table(PGPasPromRuleProduct.TABLE_NAME).build();
      gPasPromRule.getPromotion().getProductCondition().getItems().forEach(item -> {
        multilineInsertStatement.addValuesLine(PGPasPromRuleProduct.toFieldValues((String) activityMap.get(PGPasPromRule.UUID),
                gPasPromRule.getPromotion().getPromotionType(), gPasPromRule.getPromotion().getProductCondition().getEntityType(), item));
      });
      jdbcTemplate.update(multilineInsertStatement);
    }
  }

  @PmsTx
  public void delete(String tenant, String uuid) {
    DeleteStatement delete = new DeleteBuilder()
            .table(PGPasPromRule.TABLE_NAME)
            .where(Predicates.and(
                    Predicates.equals(PGPasPromRule.UUID, uuid),
                    Predicates.equals(PGPasPromRule.TENANT, tenant)))
            .build();
    jdbcTemplate.update(delete);

    DeleteStatement deleteFavorSharing = new DeleteBuilder()
            .table(PGPasPromRuleFavorSharing.TABLE_NAME)
            .where(Predicates.equals(PGPasPromRuleFavorSharing.RULE_UUID, uuid))
            .build();
    jdbcTemplate.update(deleteFavorSharing);

    DeleteStatement deleteStores = new DeleteBuilder()
            .table(PGPasPromRuleJoinUnits.TABLE_NAME)
            .where(Predicates.equals(PGPasPromRuleJoinUnits.RULE_UUID, uuid))
            .build();
    jdbcTemplate.update(deleteStores);

    DeleteStatement deleteProduct = new DeleteBuilder()
            .table(PGPasPromRuleProduct.TABLE_NAME)
            .where(Predicates.equals(PGPasPromRuleProduct.RULE_UUID, uuid))
            .build();
    jdbcTemplate.update(deleteProduct);
  }

  @PmsTx
  public void update(GPasPromRule gPasPromRule) {
    UpdateStatement update = new UpdateBuilder()
            .table(PGPasPromRule.TABLE_NAME)
            .setValues(PGPasPromRule.toFieldValues(gPasPromRule))
            .where(Predicates.equals(PGPasPromRule.UUID, gPasPromRule.getUuid()))
            .build();
    jdbcTemplate.update(update);

    if (CollectionUtils.isEmpty(gPasPromRule.getFavorSharings()) == false) {
      DeleteStatement deleteFavorSharing = new DeleteBuilder()
              .table(PGPasPromRuleFavorSharing.TABLE_NAME)
              .where(Predicates.equals(PGPasPromRuleFavorSharing.RULE_UUID, gPasPromRule.getUuid()))
              .build();
      jdbcTemplate.update(deleteFavorSharing);
      MultilineInsertStatement multilineInsertStatement = new MultilineInsertBuilder()
              .table(PGPasPromRuleFavorSharing.TABLE_NAME).build();
      gPasPromRule.getFavorSharings().forEach(favorSharing -> {
        multilineInsertStatement.addValuesLine(PGPasPromRuleFavorSharing.toFieldValues(gPasPromRule.getUuid(), favorSharing));
      });
      jdbcTemplate.update(multilineInsertStatement);
    }

    if (gPasPromRule.getJoinUnits() != null && CollectionUtils.isEmpty(gPasPromRule.getJoinUnits().getStores()) == false) {
      DeleteStatement deleteStores = new DeleteBuilder()
              .table(PGPasPromRuleJoinUnits.TABLE_NAME)
              .where(Predicates.equals(PGPasPromRuleJoinUnits.RULE_UUID, gPasPromRule.getUuid()))
              .build();
      jdbcTemplate.update(deleteStores);
      MultilineInsertStatement multilineInsertStatement = new MultilineInsertBuilder()
              .table(PGPasPromRuleJoinUnits.TABLE_NAME).build();
      gPasPromRule.getJoinUnits().getStores().forEach(ucn -> {
        multilineInsertStatement.addValuesLine(PGPasPromRuleJoinUnits.toFieldValues(gPasPromRule.getUuid(), ucn));
      });
      jdbcTemplate.update(multilineInsertStatement);
    }
    if (gPasPromRule.getPromotion() != null && gPasPromRule.getPromotion().getProductCondition() != null
            && CollectionUtils.isEmpty(gPasPromRule.getPromotion().getProductCondition().getItems()) == false) {
      DeleteStatement deleteProduct = new DeleteBuilder()
              .table(PGPasPromRuleProduct.TABLE_NAME)
              .where(Predicates.equals(PGPasPromRuleProduct.RULE_UUID, gPasPromRule.getUuid()))
              .build();
      jdbcTemplate.update(deleteProduct);
      MultilineInsertStatement multilineInsertStatement = new MultilineInsertBuilder()
              .table(PGPasPromRuleProduct.TABLE_NAME).build();
      gPasPromRule.getPromotion().getProductCondition().getItems().forEach(item -> {
        multilineInsertStatement.addValuesLine(PGPasPromRuleProduct.toFieldValues(gPasPromRule.getUuid(),
                gPasPromRule.getPromotion().getPromotionType(), gPasPromRule.getPromotion().getProductCondition().getEntityType(), item));
      });
      jdbcTemplate.update(multilineInsertStatement);
    }
  }

  public GPasPromRule get(String tenant, String uuid, String... parts) {
    List<String> fetchParts = parts == null ? Collections.emptyList() : Arrays.asList(parts);
    SelectStatement select = new SelectBuilder()
            .select(PGPasPromRule.COLUMNS)
            .from(PGPasPromRule.TABLE_NAME)
            .where(Predicates.equals(PGPasPromRule.TENANT, tenant))
            .where(Predicates.equals(PGPasPromRule.UUID, uuid))
            .build();
    if (fetchParts.contains(GPasPromRule.PARTS_PROMOTION)) {
      select.select(PGPasPromRule.PROMOTION);
    }

    List<GPasPromRule> list = jdbcTemplate.query(select, new PGPasPromRule.RowMapper(fetchParts));
    fetchParts(tenant, list, fetchParts);
    return list.isEmpty() ? null : list.get(0);
  }

  public GPasPromRule getByBillNumber(String tenant, String billNumber, String... parts) {
    List<String> fetchParts = parts == null ? Collections.emptyList() : Arrays.asList(parts);
    SelectStatement select = new SelectBuilder()
            .select(PGPasPromRule.COLUMNS)
            .from(PGPasPromRule.TABLE_NAME)
            .where(Predicates.equals(PGPasPromRule.TENANT, tenant))
            .where(Predicates.equals(PGPasPromRule.BILL_NUMBER, billNumber))
            .build();
    if (fetchParts.contains(GPasPromRule.PARTS_PROMOTION)) {
      select.select(PGPasPromRule.PROMOTION);
    }

    List<GPasPromRule> list = jdbcTemplate.query(select, new PGPasPromRule.RowMapper(fetchParts));
    fetchParts(tenant, list, fetchParts);
    return list.isEmpty() ? null : list.get(0);
  }

  public QueryResult<GPasPromRule> query(String tenant, QueryDefinition qd, List<String> fetchParts) {
    SelectStatement select = QUERY_PROCESSOR.process(qd);
    JdbcPagingQueryExecutor<GPasPromRule> queryExecutor = new JdbcPagingQueryExecutor<>(jdbcTemplate, new PGPasPromRule.RowMapper(fetchParts));
    QueryResult<GPasPromRule> queryResult = queryExecutor.query(select, qd.getPage(), qd.getPageSize());
    fetchParts(tenant, queryResult.getRecords(), fetchParts);
    return queryResult;
  }

  private void fetchParts(String tenant, List<GPasPromRule> records, List<String> fetchParts) {
    if (fetchParts == null || fetchParts.isEmpty())
      return;
    Map<String, GPasPromRule> pasPromRuleMap = records.stream().collect(Collectors.toMap(GPasPromRule::getUuid, o -> o));
    if (CollectionUtils.isEmpty(pasPromRuleMap.keySet())) {
      return;
    }
    if (fetchParts.contains(GPasPromRule.FAVOR_SHARINGS)) {
      fetchPartsOfFavorSharings(pasPromRuleMap);
    }
    if (fetchParts.contains(GPasPromRule.PARTS_JOIN_UNITS)) {
      fetchPartsOfJoinUnits(pasPromRuleMap);
    }
  }

  private void fetchPartsOfFavorSharings(Map<String, GPasPromRule> pasPromRuleMap) {
    SelectStatement select = new SelectBuilder()
            .select(PGPasPromRuleFavorSharing.COLUMNS)
            .from(PGPasPromRuleFavorSharing.TABLE_NAME)
            .where(Predicates.in2(PGPasPromRuleFavorSharing.RULE_UUID, pasPromRuleMap.keySet().toArray()))
            .orderBy(PGPasPromRuleFavorSharing.RATE, false)
            .build();

    jdbcTemplate.query(select, (resultSet, i) -> {
      FavorSharing favorSharing = PGPasPromRuleFavorSharing.mapRow(resultSet, i);
      GPasPromRule GPasPromRule = pasPromRuleMap.get(resultSet.getString(PGPasPromRuleFavorSharing.RULE_UUID));
      if (GPasPromRule != null) {
        if (CollectionUtils.isEmpty(GPasPromRule.getFavorSharings())) {
          GPasPromRule.setFavorSharings(new ArrayList<>());
        }
        GPasPromRule.getFavorSharings().add(favorSharing);
      }
      return favorSharing;
    });
  }

  /**
   * 查询门店信息
   */
  public void fetchPartsOfJoinUnits(Map<String, GPasPromRule> pasPromRuleMap) {
    SelectStatement select = new SelectBuilder()
            .select(PGPasPromRuleJoinUnits.COLUMNS)
            .from(PGPasPromRuleJoinUnits.TABLE_NAME)
            .where(Predicates.in2(PGPasPromRuleJoinUnits.RULE_UUID, pasPromRuleMap.keySet().toArray()))
            .orderBy(PGPasPromRuleJoinUnits.JOIN_UNIT_CODE)
            .build();

    jdbcTemplate.query(select, (resultSet, i) -> {
      PromotionJoinUnits.JoinUnit joinUnit = PGPasPromRuleJoinUnits.mapRow(resultSet, i);
      GPasPromRule activity = pasPromRuleMap.get(resultSet.getString(PGPasPromRuleJoinUnits.RULE_UUID));
      if (activity != null && activity.getJoinUnits().getAllUnit() == false) {
        if (activity.getJoinUnits().getStores() == null) {
          activity.getJoinUnits().setStores(new ArrayList<>());
        }
        activity.getJoinUnits().getStores().add(joinUnit);
      }
      return joinUnit;
    });
  }

  public static Date toDate(Object o) {
    if (o == null) {
      return null;
    } else if (o instanceof Date) {
      return (Date) o;
    } else if (o instanceof String) {
      try {
        return new DateTimeFormat().parse((String) o);
      } catch (ParseException e) {
        throw new IllegalArgumentException("value cast to date fail!");
      }
    }
    throw new IllegalArgumentException("value cast to date fail!");
  }

  public static DateRange convert2DateRange(Object value) {
    if (value instanceof List == false) {
      throw new IllegalArgumentException("filter (" + GPasPromRule.FILTER_DATE_RANGE_BETWEEN + ") must be a list");
    }
    List params = (List) value;
    if (CollectionUtils.isEmpty(params)) {
      return new DateRange();
    }
    if (params.size() != 2) {
      throw new IllegalArgumentException("filter (" + GPasPromRule.FILTER_DATE_RANGE_BETWEEN + ") must be 2 value");
    }
    return new DateRange(toDate(params.get(0)), toDate(params.get(1)));
  }

}
