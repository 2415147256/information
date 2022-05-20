package com.hd123.baas.sop.service.dao.activity;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.hd123.baas.sop.annotation.PmsTx;
import com.hd123.baas.sop.service.api.pms.activity.PromActivity;
import com.hd123.baas.sop.service.dao.BaseDao;
import com.hd123.baas.sop.service.api.promotion.FavorSharing;
import com.hd123.baas.sop.service.api.promotion.PromotionJoinUnits;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

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
import com.hd123.spms.commons.calendar.DateRange;
import com.hd123.spms.commons.format.DateTimeFormat;

@Repository
public class PromActivityDao extends BaseDao {

  private static final QueryProcessor QUERY_PROCESSOR = new QueryProcessorBuilder(PromActivity.class,
      PPromActivity.class).addConditionProcessor((condition, context) -> {
        if (PromActivity.Queries.JOIN_UNIT_UUID.equals(condition.getField())) {
          if (Cop.IN.equals(condition.getOperator())) {
            if (condition.getParameter() instanceof List == false) {
              throw new IllegalArgumentException(
                  "filter (" + PromActivity.Queries.JOIN_UNIT_UUID + Cop.IN + ") must be a list");
            }
            List params = (List) condition.getParameter();
            return Predicates.or(Predicates.equals(PPromActivity.JOIN_UNITS_ALL_UNIT, true),
                Predicates.exists(new SelectBuilder().select(PPromActivityJoinUnits.ACTIVITY_UUID)
                    .from(PPromActivityJoinUnits.TABLE_NAME)
                    .where(Predicates.equals(PPromActivityJoinUnits.TABLE_NAME, PPromActivityJoinUnits.ACTIVITY_UUID,
                        context.getPerzAlias(), PPromActivity.UUID))
                    .where(Predicates.in(PPromActivityJoinUnits.TABLE_NAME, PPromActivityJoinUnits.JOIN_UNIT_UUID,
                        params.toArray()))
                    .build()));
          }
          if (Cop.EQUALS.equals(condition.getOperator())) {
            return Predicates.or(Predicates.equals(PPromActivity.JOIN_UNITS_ALL_UNIT, true),
                Predicates.exists(new SelectBuilder().select(PPromActivityJoinUnits.ACTIVITY_UUID)
                    .from(PPromActivityJoinUnits.TABLE_NAME)
                    .where(Predicates.equals(PPromActivityJoinUnits.TABLE_NAME, PPromActivityJoinUnits.ACTIVITY_UUID,
                        context.getPerzAlias(), PPromActivity.UUID))
                    .where(Predicates.equals(PPromActivityJoinUnits.TABLE_NAME, PPromActivityJoinUnits.JOIN_UNIT_UUID,
                        condition.getParameter()))
                    .build()));
          }
        }
        if (PromActivity.Queries.CREATOR_KEYWORD.equals(condition.getField())) {
          String parameter = (String) condition.getParameter();
          return Predicates.or(Predicates.like(PPromActivity.CREATOR_ID, parameter),
              Predicates.like(PPromActivity.CREATOR_NAME, parameter));
        }
        return null;
      }).build();

  @Autowired
  private JdbcTemplate jdbcTemplate;

  @PmsTx
  public void create(PromActivity promActivity) {
    Map<String, Object> activityMap = PPromActivity.toFieldValues(promActivity);
    InsertStatement insert = new InsertBuilder().table(PPromActivity.TABLE_NAME).addValues(activityMap).build();
    jdbcTemplate.update(insert);

    if (CollectionUtils.isEmpty(promActivity.getFavorSharings()) == false) {
      MultilineInsertStatement multilineInsertStatement = new MultilineInsertBuilder()
          .table(PPromActivityFavorSharing.TABLE_NAME)
          .build();
      promActivity.getFavorSharings().forEach(favorSharing -> {
        multilineInsertStatement.addValuesLine(
            PPromActivityFavorSharing.toFieldValues((String) activityMap.get(PPromActivity.UUID), favorSharing));
      });
      jdbcTemplate.update(multilineInsertStatement);
    }

    if (promActivity.getJoinUnits() != null
        && CollectionUtils.isEmpty(promActivity.getJoinUnits().getStores()) == false) {
      MultilineInsertStatement multilineInsertStatement = new MultilineInsertBuilder()
          .table(PPromActivityJoinUnits.TABLE_NAME)
          .build();
      promActivity.getJoinUnits().getStores().forEach(ucn -> {
        multilineInsertStatement
            .addValuesLine(PPromActivityJoinUnits.toFieldValues((String) activityMap.get(PPromActivity.UUID), ucn));
      });
      jdbcTemplate.update(multilineInsertStatement);
    }
  }

  @PmsTx
  public void batchSaveNew(List<PromActivity> promActivities) {
    Assert.notEmpty(promActivities);
    List<InsertStatement> activitiesInsert = new ArrayList<>();
    List<InsertStatement> favorSharingsInsert = new ArrayList<>();
    List<InsertStatement> joinUnitsInsert = new ArrayList<>();
    for (PromActivity promActivity : promActivities) {
      Map<String, Object> activityMap = PPromActivity.toFieldValues(promActivity);
      InsertStatement insert = new InsertBuilder().table(PPromActivity.TABLE_NAME).addValues(activityMap).build();
      activitiesInsert.add(insert);

      if (CollectionUtils.isNotEmpty(promActivity.getFavorSharings())) {
        promActivity.getFavorSharings().forEach(favorSharing -> {
          InsertStatement sharingInsert = new InsertBuilder().table(PPromActivityFavorSharing.TABLE_NAME)
              .addValues(PPromActivityFavorSharing.toFieldValues(promActivity.getUuid(), favorSharing))
              .build();
          favorSharingsInsert.add(sharingInsert);
        });
      }
      if (promActivity.getJoinUnits() != null && CollectionUtils.isNotEmpty(promActivity.getJoinUnits().getStores())) {
        promActivity.getJoinUnits().getStores().forEach(store -> {
          InsertStatement sharingInsert = new InsertBuilder().table(PPromActivityJoinUnits.TABLE_NAME)
              .addValues(PPromActivityJoinUnits.toFieldValues(promActivity.getUuid(), store))
              .build();
          joinUnitsInsert.add(sharingInsert);
        });
      }
    }
    batchUpdate(activitiesInsert);
    batchUpdate(favorSharingsInsert);
    batchUpdate(joinUnitsInsert);
  }

  @PmsTx
  public void delete(String tenant, String uuid) {
    DeleteStatement delete = new DeleteBuilder().table(PPromActivity.TABLE_NAME)
        .where(Predicates.and(Predicates.equals(PPromActivity.UUID, uuid),
            Predicates.equals(PPromActivity.TENANT, tenant)))
        .build();
    jdbcTemplate.update(delete);

    DeleteStatement deleteFavorSharing = new DeleteBuilder().table(PPromActivityFavorSharing.TABLE_NAME)
        .where(Predicates.equals(PPromActivityFavorSharing.ACTIVITY_UUID, uuid))
        .build();
    jdbcTemplate.update(deleteFavorSharing);

    DeleteStatement deleteStores = new DeleteBuilder().table(PPromActivityJoinUnits.TABLE_NAME)
        .where(Predicates.equals(PPromActivityJoinUnits.ACTIVITY_UUID, uuid))
        .build();
    jdbcTemplate.update(deleteStores);
  }

  @PmsTx
  public void update(PromActivity promActivity) {
    UpdateStatement update = new UpdateBuilder().table(PPromActivity.TABLE_NAME)
        .setValues(PPromActivity.toFieldValues(promActivity))
        .where(Predicates.equals(PPromActivity.UUID, promActivity.getUuid()))
        .build();
    jdbcTemplate.update(update);

    if (CollectionUtils.isEmpty(promActivity.getFavorSharings()) == false) {
      DeleteStatement deleteFavorSharing = new DeleteBuilder().table(PPromActivityFavorSharing.TABLE_NAME)
          .where(Predicates.equals(PPromActivityFavorSharing.ACTIVITY_UUID, promActivity.getUuid()))
          .build();
      jdbcTemplate.update(deleteFavorSharing);
      MultilineInsertStatement multilineInsertStatement = new MultilineInsertBuilder()
          .table(PPromActivityFavorSharing.TABLE_NAME)
          .build();
      promActivity.getFavorSharings().forEach(favorSharing -> {
        multilineInsertStatement
            .addValuesLine(PPromActivityFavorSharing.toFieldValues(promActivity.getUuid(), favorSharing));
      });
      jdbcTemplate.update(multilineInsertStatement);
    }

    if (promActivity.getJoinUnits() != null
        && CollectionUtils.isEmpty(promActivity.getJoinUnits().getStores()) == false) {
      DeleteStatement deleteStores = new DeleteBuilder().table(PPromActivityJoinUnits.TABLE_NAME)
          .where(Predicates.equals(PPromActivityJoinUnits.ACTIVITY_UUID, promActivity.getUuid()))
          .build();
      jdbcTemplate.update(deleteStores);
      MultilineInsertStatement multilineInsertStatement = new MultilineInsertBuilder()
          .table(PPromActivityJoinUnits.TABLE_NAME)
          .build();
      promActivity.getJoinUnits().getStores().forEach(ucn -> {
        multilineInsertStatement.addValuesLine(PPromActivityJoinUnits.toFieldValues(promActivity.getUuid(), ucn));
      });
      jdbcTemplate.update(multilineInsertStatement);
    }
  }

  public PromActivity get(String tenant, String uuid, String... parts) {
    List<String> fetchParts = parts == null ? Collections.emptyList() : Arrays.asList(parts);
    SelectStatement select = new SelectBuilder().select(PPromActivity.COLUMNS)
        .from(PPromActivity.TABLE_NAME)
        .where(Predicates.equals(PPromActivity.TENANT, tenant))
        .where(Predicates.equals(PPromActivity.UUID, uuid))
        .build();
    if (fetchParts.contains(PromActivity.PARTS_PROMOTION)) {
      select.select(PPromActivity.PROMOTIONS);
    }

    List<PromActivity> list = jdbcTemplate.query(select, new PPromActivity.RowMapper(fetchParts));
    fetchParts(tenant, list, fetchParts);
    return list.isEmpty() ? null : list.get(0);
  }

  public List<PromActivity> list(String tenant, Collection<String> uuids, String... parts) {
    List<String> fetchParts = parts == null ? Collections.emptyList() : Arrays.asList(parts);
    SelectStatement select = new SelectBuilder().select(PPromActivity.COLUMNS)
        .from(PPromActivity.TABLE_NAME)
        .where(Predicates.equals(PPromActivity.TENANT, tenant))
        .where(Predicates.in2(PPromActivity.UUID, uuids.toArray()))
        .build();
    if (fetchParts.contains(PromActivity.PARTS_PROMOTION)) {
      select.select(PPromActivity.PROMOTIONS);
    }

    List<PromActivity> list = jdbcTemplate.query(select, new PPromActivity.RowMapper(fetchParts));
    fetchParts(tenant, list, fetchParts);
    return list;
  }

  public PromActivity getByBillNumber(String tenant, String billNumber, String... parts) {
    List<String> fetchParts = parts == null ? Collections.emptyList() : Arrays.asList(parts);
    SelectStatement select = new SelectBuilder().select(PPromActivity.COLUMNS)
        .from(PPromActivity.TABLE_NAME)
        .where(Predicates.equals(PPromActivity.TENANT, tenant))
        .where(Predicates.equals(PPromActivity.BILL_NUMBER, billNumber))
        .build();
    if (fetchParts.contains(PromActivity.PARTS_PROMOTION)) {
      select.select(PPromActivity.PROMOTIONS);
    }

    List<PromActivity> list = jdbcTemplate.query(select, new PPromActivity.RowMapper(fetchParts));
    fetchParts(tenant, list, fetchParts);
    return list.isEmpty() ? null : list.get(0);
  }

  public QueryResult<PromActivity> query(String tenant, QueryDefinition qd, List<String> fetchParts) {
    SelectStatement select = QUERY_PROCESSOR.process(qd);
    JdbcPagingQueryExecutor<PromActivity> queryExecutor = new JdbcPagingQueryExecutor<>(jdbcTemplate,
        new PPromActivity.RowMapper(fetchParts));
    QueryResult<PromActivity> queryResult = queryExecutor.query(select, qd.getPage(), qd.getPageSize());
    fetchParts(tenant, queryResult.getRecords(), fetchParts);
    return queryResult;
  }

  private void fetchParts(String tenant, List<PromActivity> records, List<String> fetchParts) {
    if (fetchParts == null || fetchParts.isEmpty())
      return;
    Map<String, PromActivity> activityMap = records.stream().collect(Collectors.toMap(PromActivity::getUuid, o -> o));
    if (CollectionUtils.isEmpty(activityMap.keySet())) {
      return;
    }
    if (fetchParts.contains(PromActivity.FAVOR_SHARINGS)) {
      fetchPartsOfFavorSharings(activityMap);
    }
    if (fetchParts.contains(PromActivity.PARTS_JOIN_UNITS)) {
      fetchPartsOfJoinUnits(activityMap);
    }
  }

  private void fetchPartsOfFavorSharings(Map<String, PromActivity> activityMap) {
    SelectStatement select = new SelectBuilder().select(PPromActivityFavorSharing.COLUMNS)
        .from(PPromActivityFavorSharing.TABLE_NAME)
        .where(Predicates.in2(PPromActivityFavorSharing.ACTIVITY_UUID, activityMap.keySet().toArray()))
        .orderBy(PPromActivityFavorSharing.RATE, false)
        .build();

    jdbcTemplate.query(select, (resultSet, i) -> {
      FavorSharing favorSharing = PPromActivityFavorSharing.mapRow(resultSet, i);
      PromActivity promActivity = activityMap.get(resultSet.getString(PPromActivityFavorSharing.ACTIVITY_UUID));
      if (promActivity != null) {
        if (CollectionUtils.isEmpty(promActivity.getFavorSharings())) {
          promActivity.setFavorSharings(new ArrayList<>());
        }
        promActivity.getFavorSharings().add(favorSharing);
      }
      return favorSharing;
    });
  }

  /**
   * 查询门店信息
   */
  public void fetchPartsOfJoinUnits(Map<String, PromActivity> activityMap) {
    SelectStatement select = new SelectBuilder().select(PPromActivityJoinUnits.COLUMNS)
        .from(PPromActivityJoinUnits.TABLE_NAME)
        .where(Predicates.in2(PPromActivityJoinUnits.ACTIVITY_UUID, activityMap.keySet().toArray()))
        .orderBy(PPromActivityJoinUnits.JOIN_UNIT_CODE)
        .build();

    jdbcTemplate.query(select, (resultSet, i) -> {
      PromotionJoinUnits.JoinUnit ucn = PPromActivityJoinUnits.mapRow(resultSet, i);
      PromActivity activity = activityMap.get(resultSet.getString(PPromActivityJoinUnits.ACTIVITY_UUID));
      if (activity != null) {
        if (CollectionUtils.isEmpty(activity.getJoinUnits().getStores())) {
          activity.getJoinUnits().setStores(new ArrayList<>());
        }
        if(activity.getJoinUnits().getAllUnit() == false){
          activity.getJoinUnits().getStores().add(ucn);
        }
      }
      return ucn;
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
      throw new IllegalArgumentException("filter (" + PromActivity.FILTER_DATE_RANGE_BETWEEN + ") must be a list");
    }
    List params = (List) value;
    if (CollectionUtils.isEmpty(params)) {
      return new DateRange();
    }
    if (params.size() != 2) {
      throw new IllegalArgumentException("filter (" + PromActivity.FILTER_DATE_RANGE_BETWEEN + ") must be 2 value");
    }
    return new DateRange(toDate(params.get(0)), toDate(params.get(1)));
  }

  public List<PromActivity> listByShopCodeAndEffectDate(String tenant, String shopCode, Date effectDate,
      String... parts) {
    List<String> fetchParts = parts == null ? Collections.emptyList() : Arrays.asList(parts);
    String alias = "_p";

    SelectStatement sub = new SelectBuilder().select("1")
        .from(PPromActivityJoinUnits.TABLE_NAME, "_s")
        .where(Predicates.equals("_s", PPromActivityJoinUnits.ACTIVITY_UUID, alias, PPromActivity.UUID))
        .where(Predicates.equals("_s", PPromActivityJoinUnits.JOIN_UNIT_CODE, shopCode))
        .build();

    SelectStatement select = new SelectBuilder().select(PPromActivity.COLUMNS)
        .from(PPromActivity.TABLE_NAME, alias)
        .where(Predicates.equals(alias, PPromActivity.TENANT, tenant))
        .where(Predicates.equals(alias, PPromActivity.STATE, PromActivity.State.audited.name()))
        .where(Predicates.lessOrEquals(alias, PPromActivity.DATE_RANGE_CONDITION_DATE_RANGE_BEGIN_DATE, effectDate))
        .where(Predicates.greaterOrEquals(alias, PPromActivity.DATE_RANGE_CONDITION_DATE_RANGE_END_DATE, effectDate))
        .where(Predicates.or(Predicates.equals(alias, PPromActivity.JOIN_UNITS_ALL_UNIT, "1"), Predicates.exists(sub)))
        .build();

      if (fetchParts.contains(PromActivity.PARTS_PROMOTION)) {
          select.select(PPromActivity.PROMOTIONS);
      }

    List<PromActivity> list = jdbcTemplate.query(select, new PPromActivity.RowMapper(fetchParts));
    fetchParts(tenant, list, fetchParts);
    return list;
  }
}
