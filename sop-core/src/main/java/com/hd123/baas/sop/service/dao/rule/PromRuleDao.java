package com.hd123.baas.sop.service.dao.rule;

import com.hd123.baas.sop.annotation.PmsTx;
import com.hd123.baas.sop.service.api.pms.rule.PromRule;
import com.hd123.baas.sop.service.api.promotion.FavorSharing;
import com.hd123.baas.sop.service.api.promotion.PromotionJoinUnits;
import com.hd123.rumba.commons.biz.entity.UCN;
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
import com.hd123.rumba.commons.lang.Assert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author Fanluhao
 * @since 1.0
 */
@Repository
public class PromRuleDao {
  private static QueryProcessor QUERY_PROCESSOR = new QueryProcessorBuilder(PromRule.class, PPromRule.class)
          .addConditionProcessor((condition, context) -> {
            if (PromRule.Queries.TIME_PERIOD_CONDITION.equals(condition.getField())) {
              if (Cop.IS_NULL.equals(condition.getOperator())) {
                return Predicates.isNull(PPromRule.TIME_PERIOD_CONDITION);
              }
            }
            if (PromRule.Queries.JOIN_UNIT_UUID.equals(condition.getField())) {
              if (Cop.IN.equals(condition.getOperator())) {
                if (condition.getParameter() instanceof List == false) {
                  throw new IllegalArgumentException("filter (" + PromRule.Queries.JOIN_UNIT_UUID + Cop.IN + ") must be a list");
                }
                List params = (List) condition.getParameter();
                return Predicates.or(
                        Predicates.equals(PPromRule.ALL_UNIT, true),
                        Predicates.exists(new SelectBuilder()
                                .select(PPromRuleJoinUnits.RULE_UUID)
                                .from(PPromRuleJoinUnits.TABLE_NAME)
                                .where(Predicates.equals(PPromRuleJoinUnits.TABLE_NAME, PPromRuleJoinUnits.RULE_UUID, context.getPerzAlias(), PPromRule.UUID))
                                .where(Predicates.in(PPromRuleJoinUnits.TABLE_NAME, PPromRuleJoinUnits.JOIN_UNIT_UUID, params.toArray()))
                                .build()));
              }
              if (Cop.EQUALS.equals(condition.getOperator())) {
                return Predicates.or(
                        Predicates.equals(PPromRule.ALL_UNIT, true),
                        Predicates.exists(new SelectBuilder()
                                .select(PPromRuleJoinUnits.RULE_UUID)
                                .from(PPromRuleJoinUnits.TABLE_NAME)
                                .where(Predicates.equals(PPromRuleJoinUnits.TABLE_NAME, PPromRuleJoinUnits.RULE_UUID, context.getPerzAlias(), PPromRule.UUID))
                                .where(Predicates.equals(PPromRuleJoinUnits.TABLE_NAME, PPromRuleJoinUnits.JOIN_UNIT_UUID, condition.getParameter()))
                                .build()));
              }
            }
            return null;
          })
          .build();
  @Autowired
  private JdbcTemplate jdbcTemplate;

  @PmsTx
  public PromRule create(PromRule target) {
    InsertStatement insert = new InsertBuilder()
            .table(PPromRule.TABLE_NAME)
            .addValues(PPromRule.toFieldValues(target))
            .build();
    jdbcTemplate.update(insert);
    // 保存关联关系
    saveFavorSharing(target);
    saveJoinUnits(target);
    return target;
  }

  @PmsTx
  public void delete(String uuid) {
    DeleteStatement delete = new DeleteBuilder()
            .table(PPromRule.TABLE_NAME)
            .where(Predicates.equals(PPromRule.UUID, uuid))
            .build();
    jdbcTemplate.update(delete);
    // 删除关联关系
    deleteFavorSharing(uuid);
    deleteJoinUnits(uuid);
  }

  @PmsTx
  public PromRule update(PromRule target, String... parts) {
    UpdateStatement update = new UpdateBuilder()
            .table(PPromRule.TABLE_NAME)
            .setValues(PPromRule.toFieldValues(target))
            .where(Predicates.equals(PPromRule.UUID, target.getUuid()))
            .build();
    jdbcTemplate.update(update);

    if (parts != null) {
      List<String> updateParts = Arrays.asList(parts);
      if (updateParts.contains(PromRule.PART_FAVOR_SHARINGS_PARTS)) {     // 更新费用承担方关联
        deleteFavorSharing(target.getUuid());
        saveFavorSharing(target);
      }
      if (updateParts.contains(PromRule.PART_JOIN_UNITS)) {    // 更新适用门店关联
        deleteJoinUnits(target.getUuid());
        saveJoinUnits(target);
      }
    }
    return target;
  }

  @PmsTx
  public void updateByActivity(String tenant, String activityUuid, PromRule.State targetState) {
    UpdateStatement update = new UpdateBuilder()
            .table(PPromRule.TABLE_NAME)
            .setValue(PPromRule.STATE, targetState.name())
            .setValue(PPromRule.LAST_MODIFIER_ID, "System")
            .setValue(PPromRule.LAST_MODIFIER_NAME, "系统自动终止")
            .setValue(PPromRule.LAST_MODIFY_INFO_TIME, new Date())
            .where(Predicates.equals(PPromRule.TENANT, tenant))
            .where(Predicates.equals(PPromRule.ACTIVITY_UUID, activityUuid))
            .build();
    jdbcTemplate.update(update);
  }

  public PromRule get(String tenantId, String uuid, String... parts) {
    List<String> fetchParts = parts == null ? Collections.emptyList() : Arrays.asList(parts);
    SelectStatement select = new SelectBuilder()
            .select(PPromRule.COLUMNS)
            .from(PPromRule.TABLE_NAME)
            .where(Predicates.equals(PPromRule.TENANT, tenantId))
            .where(Predicates.equals(PPromRule.UUID, uuid))
            .build();
    if (fetchParts.contains(PromRule.PART_PROMOTION)) {
      select.select(PPromRule.PROMOTION);
    }
    List<PromRule> list = jdbcTemplate.query(select, new PPromRule.RowMapper(fetchParts));
    fetchParts(list, fetchParts);
    return CollectionUtils.isEmpty(list) ? null : list.get(0);
  }

  public List<PromRule> gets(String tenantId, Collection<String> uuids, String... parts) {
    if (uuids == null || uuids.isEmpty())
      return new ArrayList<>();

    List<String> fetchParts = parts == null ? Collections.emptyList() : Arrays.asList(parts);
    SelectStatement select = new SelectBuilder()
            .select(PPromRule.COLUMNS)
            .from(PPromRule.TABLE_NAME)
            .where(Predicates.equals(PPromRule.TENANT, tenantId))
            .where(Predicates.in2(PPromRule.UUID, uuids.toArray()))
            .build();
    if (fetchParts.contains(PromRule.PART_PROMOTION)) {
      select.select(PPromRule.PROMOTION);
    }
    List<PromRule> list = jdbcTemplate.query(select, new PPromRule.RowMapper(fetchParts));
    fetchParts(list, fetchParts);
    return list;
  }

  public PromRule getByBillNumber(String tenantId, String billNumber, String... parts) {
    List<String> fetchParts = parts == null ? Collections.emptyList() : Arrays.asList(parts);
    SelectStatement select = new SelectBuilder()
            .select(PPromRule.COLUMNS)
            .from(PPromRule.TABLE_NAME)
            .where(Predicates.equals(PPromRule.TENANT, tenantId))
            .where(Predicates.equals(PPromRule.BILL_NUMBER, billNumber))
            .build();
    if (fetchParts.contains(PromRule.PART_PROMOTION)) {
      select.select(PPromRule.PROMOTION);
    }
    List<PromRule> list = jdbcTemplate.query(select, new PPromRule.RowMapper(fetchParts));
    fetchParts(list, fetchParts);
    return CollectionUtils.isEmpty(list) ? null : list.get(0);
  }

  public QueryResult<PromRule> query(String tenant, QueryDefinition qd, List<String> fetchParts) {
    Assert.hasLength(tenant, "tenant");
    Assert.notNull(qd, "qd");
    qd.addByField(PromRule.Queries.TENANT, Cop.EQUALS, tenant);
    SelectStatement select = QUERY_PROCESSOR.process(qd);
    if (fetchParts != null && fetchParts.contains(PromRule.PART_PROMOTION)) {
      select.select(PPromRule.PROMOTION);
    }

    JdbcPagingQueryExecutor<PromRule> queryExecutor =
            new JdbcPagingQueryExecutor<>(jdbcTemplate, new PPromRule.RowMapper(fetchParts));
    QueryResult<PromRule> queryResult = queryExecutor.query(select, qd.getPage(), qd.getPageSize());
    fetchParts(queryResult.getRecords(), fetchParts);
    return queryResult;

  }

  public Map<String, Long> queryCountByTemplate(String tenant, QueryDefinition qd, List<String> fetchParts) {
    qd.addByField(PromRule.Queries.TENANT, Cop.EQUALS, tenant);
    SelectStatement select = QUERY_PROCESSOR.process(qd);
    select.getSelectClause().getFields().clear();
    select.select(PPromRule.TEMPLATE_UUID);
    select.select("count(1) as count");
    select.where(Predicates.equals(PPromRule.STATE, PromRule.State.effect.name()));
    select.groupBy(PPromRule.TEMPLATE_UUID);

    Map<String, Long> result = new HashMap<>();
    jdbcTemplate.query(select, (resultSet, i) ->
    {
      if (resultSet.getString(PPromRule.TEMPLATE_UUID) != null) {
        result.put(resultSet.getString(PPromRule.TEMPLATE_UUID), resultSet.getLong("count"));
      }
      return null;
    });
    return result;
  }

  public void fetchParts(List<PromRule> list, List<String> parts) {
    if (CollectionUtils.isEmpty(list) || CollectionUtils.isEmpty(parts)) {
      return;
    }
    Map<String, PromRule> promRuleMap = list.stream().collect(Collectors.toMap(PromRule::getUuid, promRule -> promRule));
    if (parts.contains(PromRule.PART_JOIN_UNITS)) {
      fetchJoinUnits(promRuleMap);
    }
    if (parts.contains(PromRule.PART_FAVOR_SHARINGS_PARTS)) {
      fetchFavorSharings(promRuleMap);
    }
  }

  private void fetchFavorSharings(Map<String, PromRule> promRuleMap) {
    SelectStatement select = new SelectBuilder()
            .select(PPromRuleFavorSharing.COLUMNS)
            .from(PPromRuleFavorSharing.TABLE_NAME)
            .where(Predicates.in2(PPromRuleFavorSharing.RULE_UUID, promRuleMap.keySet().toArray()))
            .orderBy(PPromRuleFavorSharing.RATE, false)
            .build();
    jdbcTemplate.query(select, (rs, i) -> {
      PromRule rule = promRuleMap.get(rs.getString(PPromRuleFavorSharing.RULE_UUID));
      if (rule != null) {
        FavorSharing favorSharing = new FavorSharing();
        favorSharing.setRate(rs.getBigDecimal(PPromRuleFavorSharing.RATE));
        favorSharing.setTargetUnit(new UCN(rs.getString(PPromRuleFavorSharing.TARGET_UNIT_UUID), rs.getString(PPromRuleFavorSharing.TARGET_UNIT_CODE), rs.getString(PPromRuleFavorSharing.TARGET_UNIT_NAME)));
        if (rule.getFavorSharings() == null) {
          rule.setFavorSharings(new ArrayList<>());
        }
        rule.getFavorSharings().add(favorSharing);
      }
      return rule;
    });
  }

  private void fetchJoinUnits(Map<String, PromRule> promRuleMap) {
    SelectStatement select = new SelectBuilder()
            .select(PPromRuleJoinUnits.COLUMNS)
            .from(PPromRuleJoinUnits.TABLE_NAME)
            .where(Predicates.in2(PPromRuleJoinUnits.RULE_UUID, promRuleMap.keySet().toArray()))
            .orderBy(PPromRuleJoinUnits.JOIN_UNIT_CODE)
            .build();
    jdbcTemplate.query(select, (rs, i) -> {
      PromRule rule = promRuleMap.get(rs.getString(PPromRuleJoinUnits.RULE_UUID));
      if (rule != null) {
        if (rule.getJoinUnits().getStores() == null) {
          rule.getJoinUnits().setStores(new ArrayList<>());
        }
        rule.getJoinUnits().getStores().add(new PromotionJoinUnits.JoinUnit(rs.getString(PPromRuleJoinUnits.JOIN_UNIT_UUID), rs.getString(PPromRuleJoinUnits.JOIN_UNIT_CODE), rs.getString(PPromRuleJoinUnits.JOIN_UNIT_NAME)));
      }
      return rule;
    });
  }

  private void saveFavorSharing(PromRule target) {
    // 保存费用承担方
    if (CollectionUtils.isEmpty(target.getFavorSharings())) {
      return;
    }
    MultilineInsertStatement insert = new MultilineInsertBuilder()
            .table(PPromRuleFavorSharing.TABLE_NAME)
            .build();
    target.getFavorSharings().forEach(item -> {
      insert.addValuesLine(PPromRuleFavorSharing.toFieldValues(target.getUuid(), item));
    });
    jdbcTemplate.update(insert);
  }

  private void deleteFavorSharing(String ruleUuid) {
    DeleteStatement delete = new DeleteBuilder()
            .table(PPromRuleFavorSharing.TABLE_NAME)
            .where(Predicates.equals(PPromRuleFavorSharing.RULE_UUID, ruleUuid))
            .build();
    jdbcTemplate.update(delete);
  }

  private void saveJoinUnits(PromRule target) {
    // 保存适用门店
    if (target.getJoinUnits() == null || target.getJoinUnits().getAllUnit() || CollectionUtils.isEmpty(target.getJoinUnits().getStores())) {
      return;
    }
    MultilineInsertStatement insert = new MultilineInsertBuilder()
            .table(PPromRuleJoinUnits.TABLE_NAME)
            .build();
    target.getJoinUnits().getStores().forEach(item -> {
      insert.addValuesLine(PPromRuleJoinUnits.toFieldValues(target.getUuid(), item));
    });
    jdbcTemplate.update(insert);
  }

  private void deleteJoinUnits(String ruleUuid) {
    DeleteStatement delete = new DeleteBuilder()
            .table(PPromRuleJoinUnits.TABLE_NAME)
            .where(Predicates.equals(PPromRuleJoinUnits.RULE_UUID, ruleUuid))
            .build();
    jdbcTemplate.update(delete);
  }

}
