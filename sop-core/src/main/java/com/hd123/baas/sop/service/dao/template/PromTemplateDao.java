package com.hd123.baas.sop.service.dao.template;

import com.hd123.baas.sop.annotation.PmsTx;
import com.hd123.baas.sop.service.api.pms.template.PromTemplate;
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
import com.hd123.rumba.commons.jdbc.sql.UpsertBuilder;
import com.hd123.rumba.commons.jdbc.sql.UpsertStatement;
import com.hd123.spms.commons.calendar.DateRange;
import com.hd123.spms.commons.format.DateTimeFormat;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.util.CollectionUtils;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author zhuangwenting
 * @since 1.0
 */
@Repository
public class PromTemplateDao {

  private static final QueryProcessor QUERY_PROCESSOR = new QueryProcessorBuilder(PromTemplate.class, PPromTemplate.class)
      .addConditionProcessor((condition, context) -> {
        if (PromTemplate.Queries.GRANT_UNIT_UUID.equals(condition.getField())) {
          if (Cop.IN.equals(condition.getOperator())) {
            if (condition.getParameter() instanceof List == false) {
              throw new IllegalArgumentException("filter (" + PromTemplate.Queries.GRANT_UNIT_UUID + Cop.IN + ") must be a list");
            }
            List params = (List) condition.getParameter();
            return Predicates.or(
                Predicates.equals(PPromTemplate.ALL_UNIT, true),
                Predicates.exists(new SelectBuilder()
                    .select(PPromTemplateGrantUnits.TEMPLATE_UUID)
                    .from(PPromTemplateGrantUnits.TABLE_NAME)
                    .where(Predicates.equals(PPromTemplateGrantUnits.TABLE_NAME, PPromTemplateGrantUnits.TEMPLATE_UUID, context.getPerzAlias(), PPromTemplate.UUID))
                    .where(Predicates.in(PPromTemplateGrantUnits.TABLE_NAME, PPromTemplateGrantUnits.GRANT_UNIT_UUID, params.toArray()))
                    .build()));
          }
          if (Cop.EQUALS.equals(condition.getOperator())) {
            return Predicates.or(
                Predicates.equals(PPromTemplate.ALL_UNIT, true),
                Predicates.exists(new SelectBuilder()
                    .select(PPromTemplateGrantUnits.TEMPLATE_UUID)
                    .from(PPromTemplateGrantUnits.TABLE_NAME)
                    .where(Predicates.equals(PPromTemplateGrantUnits.TABLE_NAME, PPromTemplateGrantUnits.TEMPLATE_UUID, context.getPerzAlias(), PPromTemplate.UUID))
                    .where(Predicates.equals(PPromTemplateGrantUnits.TABLE_NAME, PPromTemplateGrantUnits.GRANT_UNIT_UUID, condition.getParameter()))
                    .build()));
          }
        }
        return null;
      })
      .build();

  @Autowired
  private JdbcTemplate jdbcTemplate;

  @PmsTx
  public PromTemplate create(PromTemplate target) {
    InsertStatement insert = new InsertBuilder()
        .table(PPromTemplate.TABLE_NAME)
        .addValues(PPromTemplate.toFieldValues(target))
        .build();
    jdbcTemplate.update(insert);
    saveGrantUnits(target.getUuid(), target.getGrantUnits(), true);
    saveFavorSharing(target.getUuid(), target.getFavorSharings());
    return target;
  }

  @PmsTx
  public void createPredefine(PromTemplate target) {
    UpsertStatement upsert = new UpsertBuilder()
        .table(PPromTemplate.TABLE_NAME)
        .addValues(PPromTemplate.toFieldValues(target))
        .build();
    jdbcTemplate.update(upsert);
  }

  public void delete(String tenant, String uuid) {
    DeleteStatement delete = new DeleteBuilder()
        .table(PPromTemplate.TABLE_NAME)
        .where(Predicates.equals(PPromTemplate.TENANT, tenant))
        .where(Predicates.equals(PPromTemplate.UUID, uuid))
        .build();
    saveGrantUnits(uuid, null, true);
    saveFavorSharing(uuid, null);
    jdbcTemplate.update(delete);
  }

  @PmsTx
  public PromTemplate update(PromTemplate target) {
    UpdateStatement update = new UpdateBuilder()
        .table(PPromTemplate.TABLE_NAME)
        .setValues(PPromTemplate.toFieldValues(target))
        .where(Predicates.equals(PPromTemplate.UUID, target.getUuid()))
        .where(Predicates.equals(PPromTemplate.TENANT, target.getTenant()))
        .build();
    jdbcTemplate.update(update);
    if (target.getGrantUnits() != null) {
      saveGrantUnits(target.getUuid(), target.getGrantUnits(), true);
    }
    saveFavorSharing(target.getUuid(), target.getFavorSharings());
    return target;
  }

  public PromTemplate get(String tenant, String uuid, String... parts) {
    List<String> fetchParts = parts == null ? Collections.emptyList() : Arrays.asList(parts);
    SelectStatement select = new SelectBuilder()
        .select(PPromTemplate.COLUMNS)
        .from(PPromTemplate.TABLE_NAME)
        .where(Predicates.equals(PPromTemplate.TENANT, tenant))
        .where(Predicates.equals(PPromTemplate.UUID, uuid))
        .build();
    if (fetchParts.contains(PromTemplate.PARTS_PROMOTION)) {
      select.select(PPromTemplate.PROMOTION);
    }
    List<PromTemplate> result = jdbcTemplate.query(select, new PPromTemplate.RowMapper(fetchParts));
    PromTemplate target = CollectionUtils.isEmpty(result) ? null : result.get(0);
    if (target != null) {
      fetchParts(tenant, Collections.singletonList(target), fetchParts);
    }
    return target;
  }

  public QueryResult<PromTemplate> query(String tenant, QueryDefinition qd) {
    return query(tenant, qd, Collections.emptyList());
  }

  public QueryResult<PromTemplate> query(String tenant, QueryDefinition qd, List<String> fetchParts) {
    SelectStatement select = QUERY_PROCESSOR.process(qd);
    JdbcPagingQueryExecutor<PromTemplate> queryExecutor = new JdbcPagingQueryExecutor<>(jdbcTemplate, new PPromTemplate.RowMapper(fetchParts));
    QueryResult<PromTemplate> queryResult = queryExecutor.query(select, qd.getPage(), qd.getPageSize());
    fetchParts(tenant, queryResult.getRecords(), fetchParts);
    return queryResult;
  }

  private void fetchParts(String tenant, List<PromTemplate> list, List<String> parts) {
    if (list == null || list.isEmpty() || parts == null) {
      return;
    }
    if (parts.contains(PromTemplate.PARTS_GRANT_UNITS)) {
      fetchPartsOfJoinUnits(list);
    }
    if (parts.contains(PromTemplate.PARTS_FAVOR_SHARING)) {
      fetchPartsOfFavorSharing(list);
    }
  }

  private void fetchPartsOfJoinUnits(List<PromTemplate> list) {
    if (CollectionUtils.isEmpty(list)) {
      return;
    }
    SelectStatement select = new SelectBuilder()
        .select(PPromTemplateGrantUnits.COLUMNS)
        .from(PPromTemplateGrantUnits.TABLE_NAME)
        .where(Predicates.in2(PPromTemplateGrantUnits.TEMPLATE_UUID, list.stream().map(PromTemplate::getUuid).toArray()))
        .orderBy(PPromTemplateGrantUnits.GRANT_UNIT_CODE)
        .build();
    Map<String, List<PromotionJoinUnits.JoinUnit>> map = new HashMap<>();
    jdbcTemplate.query(select, (rs, i) -> {
      PromotionJoinUnits.JoinUnit store = PPromTemplateGrantUnits.mapRow(rs, i);
      map.computeIfAbsent(rs.getString(PPromTemplateGrantUnits.TEMPLATE_UUID), k -> new ArrayList<>()).add(store);
      return store;
    });

    list.forEach(value -> {
      if (value.getGrantUnits() == null) {
        value.setGrantUnits(new PromotionJoinUnits());
      }
      value.getGrantUnits().setStores((ObjectUtils.defaultIfNull(map.get(value.getUuid()), null)));
    });
  }

  private void fetchPartsOfFavorSharing(List<PromTemplate> list) {
    if (CollectionUtils.isEmpty(list)) {
      return;
    }
    SelectStatement select = new SelectBuilder()
        .select(PPromTemplateFavorSharing.COLUMNS)
        .from(PPromTemplateFavorSharing.TABLE_NAME)
        .where(Predicates.in2(PPromTemplateFavorSharing.TEMPLATE_UUID, list.stream().map(PromTemplate::getUuid).toArray()))
        .orderBy(PPromTemplateFavorSharing.RATE, false)
        .build();
    Map<String, List<FavorSharing>> map = new HashMap<>();
    jdbcTemplate.query(select, (rs, i) -> {
      FavorSharing fvorSharing = PPromTemplateFavorSharing.mapRow(rs, i);
      map.computeIfAbsent(rs.getString(PPromTemplateFavorSharing.TEMPLATE_UUID), k -> new ArrayList<>()).add(fvorSharing);
      return fvorSharing;
    });

    list.forEach(value -> {
      value.setFavorSharings(ObjectUtils.defaultIfNull(map.get(value.getUuid()), null));
    });
  }

  @PmsTx
  public void saveGrantUnits(String templateUuid, PromotionJoinUnits joinUnits, boolean deleteOld) {
    if (deleteOld) {
      DeleteStatement delete = new DeleteBuilder()
          .table(PPromTemplateGrantUnits.TABLE_NAME)
          .where(Predicates.equals(PPromTemplateGrantUnits.TEMPLATE_UUID, templateUuid))
          .build();
      jdbcTemplate.update(delete);
    } else {
      boolean allUnit = joinUnits != null && joinUnits.getAllUnit() == Boolean.TRUE;
      UpdateStatement update = new UpdateBuilder()
          .table(PPromTemplate.TABLE_NAME)
          .addValue(PPromTemplate.ALL_UNIT, allUnit)
          .where(Predicates.equals(PPromTemplate.UUID, templateUuid))
          .build();
      jdbcTemplate.update(update);
    }
    if (joinUnits == null || joinUnits.getStores() == null || joinUnits.getStores().isEmpty()) {
      return;
    }

    MultilineInsertStatement insert = new MultilineInsertBuilder()
        .table(PPromTemplateGrantUnits.TABLE_NAME)
        .build();
    joinUnits.getStores().forEach(value -> insert.addValuesLine(PPromTemplateGrantUnits.toFieldValues(templateUuid, value)));
    jdbcTemplate.update(insert);
  }

  @PmsTx
  public void saveFavorSharing(String uuid, List<FavorSharing> favorSharings) {
    DeleteStatement delete = new DeleteBuilder()
        .table(PPromTemplateFavorSharing.TABLE_NAME)
        .where(Predicates.equals(PPromTemplateFavorSharing.TEMPLATE_UUID, uuid))
        .build();
    jdbcTemplate.update(delete);

    if (CollectionUtils.isEmpty(favorSharings)) {
      return;
    }

    MultilineInsertStatement insert = new MultilineInsertBuilder()
        .table(PPromTemplateFavorSharing.TABLE_NAME)
        .build();
    favorSharings.forEach(value -> insert.addValuesLine(PPromTemplateFavorSharing.toFieldValues(uuid, value)));
    jdbcTemplate.update(insert);
  }

  public static DateRange convert2DateRange(Object value) {
    if (value instanceof List == false) {
      throw new IllegalArgumentException("filter (" + PromTemplate.FILTER_DATE_RANGE_BETWEEN + ") must be a list");
    }
    List params = (List) value;
    if (params.size() != 2) {
      throw new IllegalArgumentException("filter (" + PromTemplate.FILTER_DATE_RANGE_BETWEEN + ") must be 2 value");
    }
    return new DateRange(toDate(params.get(0)), toDate(params.get(1)));
  }

  private static Date toDate(Object o) {
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
}
