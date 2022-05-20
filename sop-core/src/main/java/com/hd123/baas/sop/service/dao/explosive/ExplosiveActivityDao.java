package com.hd123.baas.sop.service.dao.explosive;

import com.hd123.baas.sop.annotation.PmsTx;
import com.hd123.baas.sop.service.api.pms.explosive.ExplosiveActivity;
import com.hd123.baas.sop.service.api.pms.explosive.ExplosiveActivityDetail;
import com.hd123.baas.sop.service.api.pms.explosive.ExplosiveActivityDetailJoin;
import com.hd123.baas.sop.service.api.pms.explosive.ExplosiveActivityLine;
import com.hd123.baas.sop.service.api.pms.explosive.ExplosiveActivitySignJoin;
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
import com.hd123.rumba.commons.jdbc.sql.Expr;
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
import com.hd123.spms.commons.calendar.DateRange;
import com.hd123.spms.commons.format.DateTimeFormat;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author wushuaijun
 * @since 1.0
 */
@Repository
@Slf4j
public class ExplosiveActivityDao {

  private static final QueryProcessor QUERY_PROCESSOR = new QueryProcessorBuilder(ExplosiveActivity.class,
      PExplosiveActivity.class).addConditionProcessor((condition, context) -> {
    if (ExplosiveActivity.Queries.JOIN_UNIT_UUID.equals(condition.getField())) {
      if (Cop.IN.equals(condition.getOperator())) {
        if (condition.getParameter() instanceof List == false) {
          throw new IllegalArgumentException(
              "filter (" + ExplosiveActivity.Queries.JOIN_UNIT_UUID + Cop.IN + ") must be a list");
        }
        List params = (List) condition.getParameter();
        return Predicates.or(Predicates.equals(PExplosiveActivity.JOIN_UNITS_ALL_UNIT, true),
            Predicates.exists(new SelectBuilder().select(PExplosiveActivityJoinUnits.ACTIVITY_UUID)
                .from(PExplosiveActivityJoinUnits.TABLE_NAME, "_j")
                .where(Predicates.equals("_j", PExplosiveActivityJoinUnits.ACTIVITY_UUID, context.getPerzAlias(),
                    PExplosiveActivity.UUID))
                .where(Predicates.in("_j", PExplosiveActivityJoinUnits.JOIN_UNIT_UUID, params.toArray()))
                .build()));
      } else if (Cop.EQUALS.equals(condition.getOperator())) {
        return Predicates.or(Predicates.equals(PExplosiveActivity.JOIN_UNITS_ALL_UNIT, true),
            Predicates.exists(new SelectBuilder().select(PExplosiveActivityJoinUnits.ACTIVITY_UUID)
                .from(PExplosiveActivityJoinUnits.TABLE_NAME, "_j")
                .where(Predicates.equals("_j", PExplosiveActivityJoinUnits.ACTIVITY_UUID, context.getPerzAlias(),
                    PExplosiveActivity.UUID))
                .where(
                    Predicates.equals("_j", PExplosiveActivityJoinUnits.JOIN_UNIT_UUID, condition.getParameter()))
                .build()));
      }
    }
    if (ExplosiveActivity.Queries.SIGN_STORE_UUID.equals(condition.getField())) {
      if (Cop.EQUALS.equals(condition.getOperator())) {
        SelectStatement subSelect = new SelectBuilder().select(PExplosiveActivitySignJoinLine.ACTIVITY_UUID)
            .from(PExplosiveActivitySignJoinLine.TABLE_NAME, "_s")
            .where(Predicates.equals("_s", PExplosiveActivitySignJoinLine.ACTIVITY_UUID, context.getPerzAlias(),
                PExplosiveActivity.UUID))
            .where(Predicates.equals("_s", PExplosiveActivitySignJoinLine.JOIN_UNIT_UUID, condition.getParameter()))
            .build();
        return condition.isNot() ? Predicates.notExists(subSelect)
            : Predicates.in(context.getPerzAlias(), PExplosiveActivityJoinUnits.UUID, subSelect);
      }
    }
    return null;
  }).build();
  private static final QueryProcessor QUERY_PROCESSOR_DETAIL = new QueryProcessorBuilder(ExplosiveActivityDetail.class,
      PExplosiveActivityDetail.class).addConditionProcessor(((condition, context) -> {
    if (ExplosiveActivityDetail.Queries.JOIN_UNIT_UUID.equals(condition.getField())) {
      SelectStatement statement = new SelectBuilder().select(PExplosiveActivityDetailJoin.DETAIL_UUID)
          .from(PExplosiveActivityDetailJoin.TABLE_NAME, "_s")
          .where(Predicates.equals("_s", PExplosiveActivityDetailJoin.DETAIL_UUID, context.getPerzAlias(),
              PExplosiveActivityDetail.UUID))
          .where(Predicates.equals("_s", PExplosiveActivityDetailJoin.STORE_UUID, condition.getParameter()))
          .build();
      return Predicates.exists(statement);
    }
    if (ExplosiveActivityDetail.Queries.STORE_KEYWORD.equals(condition.getField())) {
      SelectStatement statement = new SelectBuilder().select(PExplosiveActivityDetailJoin.DETAIL_UUID)
          .from(PExplosiveActivityDetailJoin.TABLE_NAME, "_s")
          .where(Predicates.or(Predicates.like(PExplosiveActivityDetailJoin.STORE_CODE, condition.getParameter()),
              Predicates.like(PExplosiveActivityDetailJoin.STORE_NAME, condition.getParameter())))
          .build();
      return Predicates.in(context.getPerzAlias(), PExplosiveActivityDetail.UUID, statement);
    }
    return null;
  })).build();
  private static final QueryProcessor QUERY_PROCESSOR_DETAIL_JOIN = new QueryProcessorBuilder(
      ExplosiveActivityDetailJoin.class, PExplosiveActivityDetailJoin.class).build();

  @Autowired
  private JdbcTemplate jdbcTemplate;

  public ExplosiveActivity get(String tenant, String uuid, String... parts) {
    List<String> fetchParts = parts == null ? Collections.emptyList() : Arrays.asList(parts);
    SelectStatement select = new SelectBuilder().select(PExplosiveActivity.COLUMNS)
        .from(PExplosiveActivity.TABLE_NAME)
        .where(Predicates.equals(PExplosiveActivity.TENANT, tenant))
        .where(Predicates.or(Predicates.equals(PExplosiveActivity.UUID, uuid),
            Predicates.equals(PExplosiveActivity.BILL_NUMBER, uuid)))
        .build();
    List<ExplosiveActivity> list = jdbcTemplate.query(select, new PExplosiveActivity.RowMapper());
    fetchParts(tenant, list, fetchParts);
    return list.isEmpty() ? null : list.get(0);
  }

  public QueryResult<ExplosiveActivity> query(String tenant, QueryDefinition qd, List<String> fetchParts) {
    SelectStatement select = QUERY_PROCESSOR.process(qd);
    JdbcPagingQueryExecutor<ExplosiveActivity> queryExecutor = new JdbcPagingQueryExecutor<>(jdbcTemplate,
        new PExplosiveActivity.RowMapper());
    QueryResult<ExplosiveActivity> queryResult = queryExecutor.query(select, qd.getPage(), qd.getPageSize());
    fetchParts(tenant, queryResult.getRecords(), fetchParts);
    return queryResult;
  }

  public List<ExplosiveActivity> validListByDate(String tenant, String orgId, String state, Date beginDate, Date endDate,
      String... parts) {
    Assert.notNull(tenant, "tenant");
    Assert.notNull(orgId, "orgId");
    Assert.notNull(state, "state");
    Assert.notNull(beginDate, "beginDate");
    Assert.notNull(endDate, "endDate");
    List<String> fetchParts = parts == null ? Collections.emptyList() : Arrays.asList(parts);
    SelectStatement select = new SelectBuilder().select(PExplosiveActivity.COLUMNS)
        .from(PExplosiveActivity.TABLE_NAME)
        .where(Predicates.equals(PExplosiveActivity.TENANT, tenant))
        .where(Predicates.equals(PExplosiveActivity.ORG_ID, orgId))
        .where(Predicates.equals(PExplosiveActivity.STATE, state))
        .where(Predicates.greater(PExplosiveActivity.DATE_RANGE_CONDITION_DATE_RANGE_END_DATE, new Date()))
        .where(
            Predicates.or(
                Predicates.and(
                    Predicates.lessOrEquals(PExplosiveActivity.DATE_RANGE_CONDITION_DATE_RANGE_BEGIN_DATE, endDate),
                    Predicates.greaterOrEquals(PExplosiveActivity.DATE_RANGE_CONDITION_DATE_RANGE_BEGIN_DATE,
                        beginDate)),
                Predicates.and(
                    Predicates.lessOrEquals(PExplosiveActivity.DATE_RANGE_CONDITION_DATE_RANGE_BEGIN_DATE, beginDate),
                    Predicates.greaterOrEquals(PExplosiveActivity.DATE_RANGE_CONDITION_DATE_RANGE_END_DATE, endDate)),
                Predicates.and(
                    Predicates.greaterOrEquals(PExplosiveActivity.DATE_RANGE_CONDITION_DATE_RANGE_END_DATE, beginDate),
                    Predicates.lessOrEquals(PExplosiveActivity.DATE_RANGE_CONDITION_DATE_RANGE_END_DATE, endDate))))
        .build();
    log.info("ExplosiveActivity list sql:{}", select.getSql());
    List<ExplosiveActivity> list = jdbcTemplate.query(select, new PExplosiveActivity.RowMapper());
    fetchParts(tenant, list, fetchParts);
    return list;
  }

  public List<ExplosiveActivityDetail> getDetails(String activityUuid, List<String> entityUuid) {
    SelectStatement select = new SelectBuilder().select(PExplosiveActivityDetail.COLUMNS)
        .from(PExplosiveActivityDetail.TABLE_NAME)
        .where(Predicates.equals(PExplosiveActivityDetail.ACTIVITY_UUID, activityUuid))
        .where(Predicates.in2(PExplosiveActivityDetail.ENTITY_UUID, entityUuid.toArray()))
        .build();
    return jdbcTemplate.query(select, new PExplosiveActivityDetail.RowMapper());
  }

  public QueryResult<ExplosiveActivityDetail> queryDetails(QueryDefinition qd) {
    SelectStatement select = QUERY_PROCESSOR_DETAIL.process(qd);
    JdbcPagingQueryExecutor<ExplosiveActivityDetail> queryExecutor = new JdbcPagingQueryExecutor<>(jdbcTemplate,
        new PExplosiveActivityDetail.RowMapper());
    return queryExecutor.query(select, qd.getPage(), qd.getPageSize());
  }

  public QueryResult<ExplosiveActivityDetailJoin> queryDetailJoins(QueryDefinition qd) {
    SelectStatement select = QUERY_PROCESSOR_DETAIL_JOIN.process(qd);
    JdbcPagingQueryExecutor<ExplosiveActivityDetailJoin> queryExecutor = new JdbcPagingQueryExecutor<>(jdbcTemplate,
        PExplosiveActivityDetailJoin::mapRow);
    return queryExecutor.query(select, qd.getPage(), qd.getPageSize());
  }

  public List<ExplosiveActivityDetailJoin> queryDetailJoins(List<String> detailUuids) {
    SelectStatement select = new SelectBuilder().from(PExplosiveActivityDetailJoin.TABLE_NAME)
        .where(Predicates.in2(PExplosiveActivityDetailJoin.DETAIL_UUID, detailUuids.toArray()))
        .build();
    List<ExplosiveActivityDetailJoin> query = jdbcTemplate.query(select, PExplosiveActivityDetailJoin::mapRow);
    return query;
  }

  @PmsTx
  public void create(ExplosiveActivity explosiveActivity) {
    Map<String, Object> activityMap = PExplosiveActivity.toFieldValues(explosiveActivity);
    InsertStatement insert = new InsertBuilder().table(PExplosiveActivity.TABLE_NAME).addValues(activityMap).build();
    jdbcTemplate.update(insert);
    String activityUuid = (String) activityMap.get(PExplosiveActivity.UUID);

    if (CollectionUtils.isNotEmpty(explosiveActivity.getFavorSharings())) {
      MultilineInsertStatement multilineInsertStatement = new MultilineInsertBuilder()
          .table(PExplosiveActivityFavorSharing.TABLE_NAME)
          .build();
      explosiveActivity.getFavorSharings().forEach(favorSharing -> {
        multilineInsertStatement
            .addValuesLine(PExplosiveActivityFavorSharing.toFieldValues(activityUuid, favorSharing));
      });
      jdbcTemplate.update(multilineInsertStatement);
    }

    if (explosiveActivity.getJoinUnits() != null
        && CollectionUtils.isNotEmpty(explosiveActivity.getJoinUnits().getStores())) {
      MultilineInsertStatement multilineInsertStatement = new MultilineInsertBuilder()
          .table(PExplosiveActivityJoinUnits.TABLE_NAME)
          .build();
      explosiveActivity.getJoinUnits().getStores().forEach(ucn -> {
        multilineInsertStatement.addValuesLine(PExplosiveActivityJoinUnits.toFieldValues(activityUuid, ucn));
      });
      jdbcTemplate.update(multilineInsertStatement);
    }

    if (CollectionUtils.isNotEmpty(explosiveActivity.getLines())) {
      MultilineInsertStatement multilineInsertStatement = new MultilineInsertBuilder()
          .table(PExplosiveActivityLine.TABLE_NAME)
          .build();
      explosiveActivity.getLines().forEach(explosiveActivityLine -> {
        multilineInsertStatement
            .addValuesLine(PExplosiveActivityLine.toFieldValues(activityUuid, explosiveActivityLine));
      });
      jdbcTemplate.update(multilineInsertStatement);
    }

  }

  @PmsTx
  public void saveDetails(List<ExplosiveActivityDetail> details) {
    MultilineInsertStatement multilineInsertStatement = new MultilineInsertBuilder()
        .table(PExplosiveActivityDetail.TABLE_NAME)
        .build();
    details.forEach(detail -> {
      multilineInsertStatement.addValuesLine(PExplosiveActivityDetail.toFieldValues(detail));
    });
    jdbcTemplate.update(multilineInsertStatement);
  }

  @PmsTx
  public void delete(String tenant, String uuid) {
    DeleteStatement delete = new DeleteBuilder().table(PExplosiveActivity.TABLE_NAME)
        .where(Predicates.and(Predicates.equals(PExplosiveActivity.UUID, uuid),
            Predicates.equals(PExplosiveActivity.TENANT, tenant)))
        .build();
    jdbcTemplate.update(delete);

    delete = new DeleteBuilder().table(PExplosiveActivityLine.TABLE_NAME)
        .where(Predicates.equals(PExplosiveActivityLine.ACTIVITY_UUID, uuid))
        .build();
    jdbcTemplate.update(delete);

    delete = new DeleteBuilder().table(PExplosiveActivityFavorSharing.TABLE_NAME)
        .where(Predicates.equals(PExplosiveActivityFavorSharing.ACTIVITY_UUID, uuid))
        .build();
    jdbcTemplate.update(delete);

    delete = new DeleteBuilder().table(PExplosiveActivityJoinUnits.TABLE_NAME)
        .where(Predicates.equals(PExplosiveActivityJoinUnits.ACTIVITY_UUID, uuid))
        .build();
    jdbcTemplate.update(delete);

    delete = new DeleteBuilder().table(PExplosiveActivitySignJoinLine.TABLE_NAME)
        .where(Predicates.equals(PExplosiveActivitySignJoinLine.ACTIVITY_UUID, uuid))
        .build();
    jdbcTemplate.update(delete);
  }

  @PmsTx
  public void updateActivityDetailByActivityUuid(String activityUuid) {
    UpdateStatement update = new UpdateBuilder().table(PExplosiveActivityDetail.TABLE_NAME)
        .setValue(PExplosiveActivityDetail.ACTIVITY_STATE, ExplosiveActivity.State.canceled.name())
        .where(Predicates.equals(PExplosiveActivityDetail.ACTIVITY_UUID, activityUuid))
        .build();
    jdbcTemplate.update(update);
  }

  @PmsTx
  public void update(ExplosiveActivity target, String... parts) {
    UpdateStatement update = new UpdateBuilder().table(PExplosiveActivity.TABLE_NAME)
        .setValues(PExplosiveActivity.toFieldValues(target))
        .where(Predicates.and(Predicates.equals(PExplosiveActivity.UUID, target.getUuid()),
            Predicates.equals(PExplosiveActivity.TENANT, target.getTenant())))
        .build();
    jdbcTemplate.update(update);

    List<String> updateParts = Arrays.asList(parts);

    if (updateParts.contains(ExplosiveActivity.PARTS_FAVOR_SHARINGS)) {
      DeleteStatement deleteFavorSharing = new DeleteBuilder().table(PExplosiveActivityFavorSharing.TABLE_NAME)
          .where(Predicates.equals(PExplosiveActivityFavorSharing.ACTIVITY_UUID, target.getUuid()))
          .build();
      jdbcTemplate.update(deleteFavorSharing);
      MultilineInsertStatement multilineInsertStatement = new MultilineInsertBuilder()
          .table(PExplosiveActivityFavorSharing.TABLE_NAME)
          .build();
      target.getFavorSharings().forEach(favorSharing -> {
        multilineInsertStatement
            .addValuesLine(PExplosiveActivityFavorSharing.toFieldValues(target.getUuid(), favorSharing));
      });
      jdbcTemplate.update(multilineInsertStatement);
    }

    if (updateParts.contains(ExplosiveActivity.PARTS_JOIN_UNITS)) {
      DeleteStatement deleteStores = new DeleteBuilder().table(PExplosiveActivityJoinUnits.TABLE_NAME)
          .where(Predicates.equals(PExplosiveActivityJoinUnits.ACTIVITY_UUID, target.getUuid()))
          .build();
      jdbcTemplate.update(deleteStores);
      if (target.getJoinUnits() != null && CollectionUtils.isNotEmpty(target.getJoinUnits().getStores())) {
        MultilineInsertStatement multilineInsertStatement = new MultilineInsertBuilder()
            .table(PExplosiveActivityJoinUnits.TABLE_NAME)
            .build();
        target.getJoinUnits().getStores().forEach(ucn -> {
          multilineInsertStatement.addValuesLine(PExplosiveActivityJoinUnits.toFieldValues(target.getUuid(), ucn));
        });
        jdbcTemplate.update(multilineInsertStatement);
      }
    }

    if (updateParts.contains(ExplosiveActivity.PARTS_LINES)) {
      DeleteStatement deleteStores = new DeleteBuilder().table(PExplosiveActivityLine.TABLE_NAME)
          .where(Predicates.equals(PExplosiveActivityLine.ACTIVITY_UUID, target.getUuid()))
          .build();
      jdbcTemplate.update(deleteStores);
      MultilineInsertStatement multilineInsertStatement = new MultilineInsertBuilder()
          .table(PExplosiveActivityLine.TABLE_NAME)
          .build();
      target.getLines().forEach(explosiveActivityLine -> {
        multilineInsertStatement
            .addValuesLine(PExplosiveActivityLine.toFieldValues(target.getUuid(), explosiveActivityLine));
      });
      jdbcTemplate.update(multilineInsertStatement);
    }
    if (updateParts.contains(ExplosiveActivity.UPDATE_DETAIL)) {
      update = new UpdateBuilder().table(PExplosiveActivityDetail.TABLE_NAME)
          .setValue(PExplosiveActivityDetail.ACTIVITY_STATE, target.getState().name())
          .where(Predicates.equals(PExplosiveActivityDetail.ACTIVITY_UUID, target.getUuid()))
          .build();
      jdbcTemplate.update(update);
    }
  }

  @PmsTx
  public void saveSingJoin(String uuid, ExplosiveActivitySignJoin signJoin) {
    MultilineInsertStatement insert = new MultilineInsertBuilder().table(PExplosiveActivitySignJoinLine.TABLE_NAME)
        .build();
    signJoin.getLines()
        .forEach(line -> insert.addValuesLine(PExplosiveActivitySignJoinLine.toFieldValues(uuid, signJoin, line)));
    jdbcTemplate.update(insert);
  }

  @PmsTx
  public void saveDetailJoin(ExplosiveActivityDetail detail, ExplosiveActivityDetailJoin detailJoin) {
    InsertStatement insert = new InsertBuilder().table(PExplosiveActivityDetailJoin.TABLE_NAME)
        .addValues(PExplosiveActivityDetailJoin.toFieldValues(detail.getUuid(), detailJoin))
        .build();
    jdbcTemplate.update(insert);

    UpdateStatement update = new UpdateBuilder().table(PExplosiveActivityDetail.TABLE_NAME)
        .setValue(PExplosiveActivityDetail.TOTAL_SIGN_QTY,
            Expr.valueOf(
                PExplosiveActivityDetail.TOTAL_SIGN_QTY.concat(" + ").concat(detailJoin.getSignQty().toString())))
        .setValue(PExplosiveActivityDetail.STORE_COUNT,
            Expr.valueOf(PExplosiveActivityDetail.STORE_COUNT.concat(" + 1")))
        .where(Predicates.equals(PExplosiveActivityDetail.UUID, detail.getUuid()))
        .where(Predicates.equals(PExplosiveActivityDetail.TENANT, detail.getTenant()))
        .build();
    if (detail.getStoreCount() == 0) {
      update.setValue(PExplosiveActivityDetail.STORE_EXAMPLE_UUID, detailJoin.getStore().getUuid());
      update.setValue(PExplosiveActivityDetail.STORE_EXAMPLE_CODE, detailJoin.getStore().getCode());
      update.setValue(PExplosiveActivityDetail.STORE_EXAMPLE_NAME, detailJoin.getStore().getName());
    }
    jdbcTemplate.update(update);
  }

  @PmsTx
  public void deleteDetailJoin(ExplosiveActivityDetailJoin detailJoin) {
    DeleteStatement delete = new DeleteBuilder().table(PExplosiveActivityDetailJoin.TABLE_NAME)
        .where(Predicates.equals(PExplosiveActivityDetailJoin.TENANT, detailJoin.getTenant()))
        .where(Predicates.equals(PExplosiveActivityDetailJoin.DETAIL_UUID, detailJoin.getDetailUuid()))
        .where(Predicates.equals(PExplosiveActivityDetailJoin.STORE_UUID, detailJoin.getStore().getUuid()))
        .build();
    jdbcTemplate.update(delete);

    UpdateStatement update = new UpdateBuilder().table(PExplosiveActivityDetail.TABLE_NAME)
        .setValue(PExplosiveActivityDetail.TOTAL_SIGN_QTY,
            Expr.valueOf(
                PExplosiveActivityDetail.TOTAL_SIGN_QTY.concat(" - ").concat(detailJoin.getSignQty().toString())))
        .setValue(PExplosiveActivityDetail.STORE_COUNT,
            Expr.valueOf(PExplosiveActivityDetail.STORE_COUNT.concat(" - 1")))
        .where(Predicates.equals(PExplosiveActivityDetail.UUID, detailJoin.getDetailUuid()))
        .where(Predicates.equals(PExplosiveActivityDetail.TENANT, detailJoin.getTenant()))
        .build();
    jdbcTemplate.update(update);
  }

  @PmsTx
  public void deleteLineByStoreId(String activityUuid, String storeId) {
    DeleteStatement delete = new DeleteBuilder().table(PExplosiveActivitySignJoinLine.TABLE_NAME)
        .where(Predicates.equals(PExplosiveActivitySignJoinLine.ACTIVITY_UUID, activityUuid))
        .where(Predicates.equals(PExplosiveActivitySignJoinLine.JOIN_UNIT_UUID, storeId))
        .build();
    jdbcTemplate.update(delete);
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
    if (!(value instanceof List)) {
      throw new IllegalArgumentException(
          "filter (" + ExplosiveActivityDetail.FILTER_ACTIVITY_RANGE_BETWEEN + ") must be a list");
    }
    List params = (List) value;
    if (CollectionUtils.isEmpty(params)) {
      return new DateRange();
    }
    if (params.size() != 2) {
      throw new IllegalArgumentException(
          "filter (" + ExplosiveActivityDetail.FILTER_ACTIVITY_RANGE_BETWEEN + ") must be 2 value");
    }
    return new DateRange(toDate(params.get(0)), toDate(params.get(1)));
  }

  private void fetchParts(String tenant, List<ExplosiveActivity> activities, List<String> fetchParts) {
    if (CollectionUtils.isEmpty(activities)) {
      return;
    }
    Map<String, ExplosiveActivity> activityMap = activities.stream()
        .collect(Collectors.toMap(ExplosiveActivity::getUuid, o -> o));

    if (CollectionUtils.isEmpty(activityMap.keySet())) {
      return;
    }
    if (fetchParts.contains(ExplosiveActivity.PARTS_FAVOR_SHARINGS)) {
      fetchPartsOfFavorSharings(activityMap);
    }
    if (fetchParts.contains(ExplosiveActivity.PARTS_JOIN_UNITS)) {
      fetchPartsOfJoinUnits(activityMap);
    }
    if (fetchParts.contains(ExplosiveActivity.PARTS_LINES)) {
      fetchPartsOfLine(activityMap);
    }
    if (fetchParts.contains(ExplosiveActivity.PARTS_SIGN_JOINS)) {
      fetchPartsOfSignJoin(activityMap);
    }
  }

  private void fetchPartsOfSignJoin(Map<String, ExplosiveActivity> activityMap) {
    SelectStatement select = new SelectBuilder().select(PExplosiveActivitySignJoinLine.COLUMNS)
        .from(PExplosiveActivitySignJoinLine.TABLE_NAME)
        .where(Predicates.in2(PExplosiveActivitySignJoinLine.ACTIVITY_UUID, activityMap.keySet().toArray()))
        .orderBy(PExplosiveActivitySignJoinLine.JOIN_UNIT_CODE)
        .build();

    Map<String, Map<String, ExplosiveActivitySignJoin>> activitySignJoinMap = new HashMap<>();
    jdbcTemplate.query(select, (resultSet, i) -> {
      ExplosiveActivitySignJoin signJoin = new ExplosiveActivitySignJoin();
      PExplosiveActivitySignJoinLine.inject(resultSet, signJoin);

      ExplosiveActivitySignJoin.ExplosiveActivitySignLine signLine = new ExplosiveActivitySignJoin.ExplosiveActivitySignLine();
      PExplosiveActivitySignJoinLine.inject(resultSet, signLine);

      String activityUuid = resultSet.getString(PExplosiveActivitySignJoinLine.ACTIVITY_UUID);
      ExplosiveActivity activity = activityMap.get(activityUuid);
      if (activity != null) {
        activitySignJoinMap.computeIfAbsent(activityUuid, k -> new HashMap<>())
            .computeIfAbsent(signJoin.getStore().getUuid(), k -> {
              activity.getSignJoins().add(signJoin);
              signJoin.setLines(new ArrayList<>());
              return signJoin;
            })
            .getLines()
            .add(signLine);
      }
      return signJoin;
    });
  }

  private void fetchPartsOfLine(Map<String, ExplosiveActivity> activityMap) {
    SelectStatement select = new SelectBuilder().select(PExplosiveActivityLine.COLUMNS)
        .from(PExplosiveActivityLine.TABLE_NAME)
        .where(Predicates.in2(PExplosiveActivityLine.ACTIVITY_UUID, activityMap.keySet().toArray()))
        .orderBy(PExplosiveActivityLine.ENTITY_UNIT_CODE)
        .build();
    jdbcTemplate.query(select, (resultSet, i) -> {
      ExplosiveActivityLine line = PExplosiveActivityLine.mapRow(resultSet, i);
      ExplosiveActivity explosiveActivity = activityMap
          .get(resultSet.getString(PExplosiveActivityJoinUnits.ACTIVITY_UUID));
      if (explosiveActivity != null) {
        if (CollectionUtils.isEmpty(explosiveActivity.getLines())) {
          explosiveActivity.setLines(new ArrayList<>());
        }
        explosiveActivity.getLines().add(line);
      }
      return line;
    });
  }

  private void fetchPartsOfJoinUnits(Map<String, ExplosiveActivity> activityMap) {
    SelectStatement select = new SelectBuilder().select(PExplosiveActivityJoinUnits.COLUMNS)
        .from(PExplosiveActivityJoinUnits.TABLE_NAME)
        .where(Predicates.in2(PExplosiveActivityJoinUnits.ACTIVITY_UUID, activityMap.keySet().toArray()))
        .orderBy(PExplosiveActivityJoinUnits.JOIN_UNIT_CODE)
        .build();

    jdbcTemplate.query(select, (resultSet, i) -> {
      PromotionJoinUnits.JoinUnit joinUnit = PExplosiveActivityJoinUnits.mapRow(resultSet, i);
      ExplosiveActivity activity = activityMap.get(resultSet.getString(PExplosiveActivityJoinUnits.ACTIVITY_UUID));
      if (activity != null) {
        if (CollectionUtils.isEmpty(activity.getJoinUnits().getStores())) {
          activity.getJoinUnits().setStores(new ArrayList<>());
        }
        activity.getJoinUnits().getStores().add(joinUnit);
      }
      return joinUnit;
    });
  }

  private void fetchPartsOfFavorSharings(Map<String, ExplosiveActivity> activityMap) {
    SelectStatement select = new SelectBuilder().select(PExplosiveActivityFavorSharing.COLUMNS)
        .from(PExplosiveActivityFavorSharing.TABLE_NAME)
        .where(Predicates.in2(PExplosiveActivityFavorSharing.ACTIVITY_UUID, activityMap.keySet().toArray()))
        .orderBy(PExplosiveActivityFavorSharing.RATE, false)
        .build();

    jdbcTemplate.query(select, (resultSet, i) -> {
      FavorSharing favorSharing = PExplosiveActivityFavorSharing.mapRow(resultSet, i);
      ExplosiveActivity activity = activityMap.get(resultSet.getString(PExplosiveActivityJoinUnits.ACTIVITY_UUID));
      if (activity != null) {
        if (CollectionUtils.isEmpty(activity.getFavorSharings())) {
          activity.setFavorSharings(new ArrayList<>());
        }
        activity.getFavorSharings().add(favorSharing);
      }
      return favorSharing;
    });
  }
}
