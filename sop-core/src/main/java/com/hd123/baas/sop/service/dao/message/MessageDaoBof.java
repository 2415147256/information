package com.hd123.baas.sop.service.dao.message;

import com.hd123.baas.sop.service.dao.BofBaseDao;
import com.hd123.baas.sop.service.api.message.Message;
import com.hd123.baas.sop.service.api.message.MessageType;
import com.hd123.baas.sop.utils.BaasJSONUtil;
import com.hd123.rumba.commons.biz.entity.OperateInfo;
import com.hd123.rumba.commons.biz.query.Cop;
import com.hd123.rumba.commons.biz.query.QueryDefinition;
import com.hd123.rumba.commons.biz.query.QueryResult;
import com.hd123.rumba.commons.jdbc.entity.PStandardEntity;
import com.hd123.rumba.commons.jdbc.qd.QueryProcessor;
import com.hd123.rumba.commons.jdbc.qd.QueryProcessorBuilder;
import com.hd123.rumba.commons.jdbc.sql.*;
import com.hd123.rumba.commons.lang.Assert;
import com.qianfan123.baas.common.BaasException;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.jdbc.core.SingleColumnRowMapper;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author zhengzewang on 2020/11/17.
 */
@Repository
public class MessageDaoBof extends BofBaseDao {

  private QueryProcessor QUERY_PROCESSOR = new QueryProcessorBuilder(Message.class, PMessage.class)
      .addConditionProcessor((condition, context) -> {
        String alias = context.getPerzAlias();
        if (StringUtils.equalsIgnoreCase(Message.Queries.SHOP_KEYWORD, condition.getOperation())) {
          String value = (String) condition.getParameter();
          return Predicates.or(Predicates.like(alias, PMessage.SHOP_CODE, value),
              Predicates.like(alias, PMessage.SHOP_NAME, value));
        }
        if (StringUtils.equalsIgnoreCase(Message.Queries.OWNERSHIP_IN, condition.getOperation())) {
          List<Object> parameters = condition.getParameters();
          Object shop = parameters.get(0);
          Object userId = parameters.get(1);
          Object appId = parameters.get(2);
          if (shop == null || "".equals(shop) || "-".equals(shop)) {
            return Predicates.or(
                Predicates.and(
                    Predicates.isNull(PMessage.APP_ID),
                    Predicates.equals(alias, PMessage.USER_ID, parameters.get(1))
                ),
                Predicates.and(
                    Predicates.isNotNull(PMessage.APP_ID),
                    Predicates.equalsWhenPresent(alias, PMessage.APP_ID, appId),
                    Predicates.equals(alias, PMessage.USER_ID, parameters.get(1))
                )
            );
          } else {
            return Predicates.and(Predicates.equals(alias, PMessage.SHOP, parameters.get(0)),
                Predicates.or(Predicates.equals(alias, PMessage.USER_ID, userId), Predicates.isNull(alias, PMessage.USER_ID)));
          }
        }
        return null;
      })
      .build();

  public QueryResult<Message> query(String tenant, QueryDefinition qd) {
    qd.addByField(Message.Queries.TENANT, Cop.EQUALS, tenant);
    SelectStatement select = QUERY_PROCESSOR.process(qd);
    return executor.query(select, new MessageMapper());
  }

  public long queryCount(String tenant, QueryDefinition qd) {
    qd.addByField(Message.Queries.TENANT, Cop.EQUALS, tenant);
    SelectStatement selectStatement = QUERY_PROCESSOR.process(qd);
    selectStatement.getSelectClause().getFields().clear();
    selectStatement.select("count(1)");
    QueryResult<Long> result = executor.query(selectStatement, new SingleColumnRowMapper<>(Long.class));
    if (result.getRecordCount() == 0) {
      return 0;
    }
    return result.getRecords().get(0);
  }

  public Message get(String tenant, String uuid) {
    SelectStatement select = new SelectBuilder().from(PMessage.TABLE_NAME)
        .select(PMessage.allColumns())
        .where(Predicates.equals(PMessage.TENANT, tenant))
        .where(Predicates.equals(PMessage.UUID, uuid))
        .build();
    return getFirst(jdbcTemplate.query(select, new MessageMapper()));
  }

  public void insert(String tenant, Message message, OperateInfo operateInfo) throws BaasException {
    Assert.hasText(tenant, "tenant");
    Assert.notNull(message, "message");

    if (StringUtils.isBlank(message.getUuid())) {
      message.setUuid(UUID.randomUUID().toString());
    }
    InsertStatement insert = buildInsertStatement(tenant, message, operateInfo);
    jdbcTemplate.update(insert);
  }

  public void deleteRead(String tenant, Date beforeDate) {
    DeleteStatement delete = new DeleteBuilder().table(PMessage.TABLE_NAME)
        .where(Predicates.equals(PMessage.TENANT, tenant))
        .where(Predicates.equals(PMessage.READ, false))
        .where(Predicates.less(PMessage.CREATE_INFO_TIME, beforeDate))
        .build();
    jdbcTemplate.update(delete);
  }

  public void batchDelete(String tenant, Collection<String> uuids) {
    DeleteStatement delete = new DeleteBuilder().table(PMessage.TABLE_NAME)
        .where(Predicates.equals(PMessage.TENANT, tenant))
        .where(Predicates.in2(PMessage.UUID, uuids.toArray()))
        .build();
    jdbcTemplate.update(delete);
  }

  public List<Message> list(String tenant, Collection<String> uuids) {
    if (CollectionUtils.isEmpty(uuids)) {
      return new ArrayList<>();
    }
    SelectStatement select = new SelectBuilder().from(PMessage.TABLE_NAME)
        .select(PMessage.allColumns())
        .where(Predicates.equals(PMessage.TENANT, tenant))
        .where(Predicates.in2(PMessage.UUID, uuids.toArray()))
        .build();
    return jdbcTemplate.query(select, new MessageMapper());
  }

  public void read(String tenant, String uuid, String appId, OperateInfo operateInfo) {
    Assert.hasText(tenant, "tenant");
    Assert.hasText(uuid, "uuid");
    Assert.hasText(appId, "appId");
    Assert.notNull(operateInfo, "operateInfo");
    UpdateStatement update = new UpdateBuilder().table(PMessage.TABLE_NAME)
        .addValues(PStandardEntity.toLastModifyInfoFieldValues(operateInfo))
        .addValue(PMessage.READ, true)
        .addValue(PMessage.READ_APP_ID, appId)
        .addValue(PMessage.READ_INFO_OPERATOR_FULL_NAME, operateInfo.getOperator().getFullName())
        .addValue(PMessage.READ_INFO_OPERATOR_ID, operateInfo.getOperator().getId())
        .addValue(PMessage.READ_INFO_OPERATOR_NAMESPACE, operateInfo.getOperator().getNamespace())
        .addValue(PMessage.READ_INFO_TIME, operateInfo.getTime())
        .where(Predicates.equals(PMessage.TENANT, tenant))
        .where(Predicates.equals(PMessage.UUID, uuid))
        .build();
    jdbcTemplate.update(update);
  }

  public void readAll(String tenant, String shop, MessageType type, String appId, OperateInfo operateInfo) {
    Assert.hasText(tenant, "tenant");
    Assert.hasText(shop, "shop");
    Assert.notNull(type, "type");
    Assert.hasText(appId, "appId");
    Assert.notNull(operateInfo, "operateInfo");
    /*
    UPDATE message
    SET lastModifierNS = ?, lastModifierId = ?, lastModifierName = ?, lastModified = ?, `read` = ?, read_app_id = ?, reader_name = ?, reader_id = ?, reader_ns = ?, read_time = ?
    WHERE (tenant = ?)
      AND (type = ?)
      AND (((app_id IS NULL) AND ((shop = ?) OR (user_id = ?))) OR
           ((app_id IS NOT NULL) AND (app_id = ?) AND (user_id = ?)))
     */
    UpdateStatement update = new UpdateBuilder()
        .table(PMessage.TABLE_NAME)
        .addValues(PStandardEntity.toLastModifyInfoFieldValues(operateInfo))
        .addValue(PMessage.READ, true)
        .addValue(PMessage.READ_APP_ID, appId)
        .addValue(PMessage.READ_INFO_OPERATOR_FULL_NAME, operateInfo.getOperator().getFullName())
        .addValue(PMessage.READ_INFO_OPERATOR_ID, operateInfo.getOperator().getId())
        .addValue(PMessage.READ_INFO_OPERATOR_NAMESPACE, operateInfo.getOperator().getNamespace())
        .addValue(PMessage.READ_INFO_TIME, operateInfo.getTime())
        .where(Predicates.equals(PMessage.TENANT, tenant))
        .where(Predicates.equals(PMessage.TYPE, type.name()))
        .where(
            Predicates.or(
                Predicates.and(
                    Predicates.isNull(PMessage.APP_ID),
                    Predicates.or(
                        Predicates.equals(PMessage.SHOP, shop),
                        Predicates.equals(PMessage.USER_ID, operateInfo.getOperator().getId())
                    )
                ),
                Predicates.and(
                    Predicates.isNotNull(PMessage.APP_ID),
                    Predicates.equals(PMessage.APP_ID, appId),
                    Predicates.equals(PMessage.USER_ID, operateInfo.getOperator().getId())
                )
            )
        )
        .build();
    System.out.println(update.getSql());
    jdbcTemplate.update(update);
  }

  public Map<MessageType, Integer> summary(String tenant, String appId, String shop, String loginId, QueryDefinition qd) {
    qd.addByField(Message.Queries.TENANT, Cop.EQUALS, tenant);
    SelectStatement selectStatement = QUERY_PROCESSOR.process(qd);
    selectStatement.getSelectClause().getFields().clear();
    selectStatement.select(PMessage.TYPE, "count(1) AS total");
    String defaultShop = "-1";
    if (StringUtils.isBlank(shop) || defaultShop.equals(shop) || "-".equals(shop)) {
      selectStatement.where(
          Predicates.or(
              Predicates.and(
                  Predicates.isNotNull(PMessage.APP_ID),
                  Predicates.equals(PMessage.APP_ID, appId),
                  Predicates.equals(PMessage.USER_ID, loginId)
              ),
              Predicates.and(
                  Predicates.isNull(PMessage.APP_ID),
                  Predicates.equals(PMessage.USER_ID, loginId)
              )
          )
      );
    } else {
      selectStatement.where(Predicates.and(Predicates.equals(PMessage.SHOP, shop),
          Predicates.or(Predicates.equals(PMessage.USER_ID, loginId), Predicates.isNull(PMessage.USER_ID))));
    }
    selectStatement.where(Predicates.equals(PMessage.READ, false));
    selectStatement.groupBy(selectStatement.getFromClause().getAlias() + "." + PMessage.TYPE);
    List<MessageSummary> summaries = jdbcTemplate.query(selectStatement, (rs, rowNum) -> {
      MessageSummary summary = new MessageSummary();
      summary.setType(MessageType.valueOf(rs.getString(PMessage.TYPE)));
      summary.setTotal(rs.getInt("total"));
      return summary;
    });
    return summaries.stream().collect(Collectors.toMap(MessageSummary::getType, MessageSummary::getTotal));
  }

  @Getter
  @Setter
  private static class MessageSummary {

    private MessageType type;
    private Integer total;

  }

  public void update(String tenant, Message message, OperateInfo operateInfo) throws BaasException {
    Assert.hasText(tenant, "tenant");
    Assert.notNull(message, "message");
    message.setLastModifyInfo(operateInfo);
    UpdateBuilder updateBuilder = new UpdateBuilder().table(PMessage.TABLE_NAME)
        .addValues(PMessage.forSaveModify(message))
        .addValue(PMessage.TENANT, message.getTenant())
        .addValue(PMessage.SHOP, message.getShop())
        .addValue(PMessage.SHOP_CODE, message.getShopCode())
        .addValue(PMessage.SHOP_NAME, message.getShopName())
        .addValue(PMessage.TYPE, message.getType().name())
        .addValue(PMessage.ACTION, message.getAction())
        .addValue(PMessage.ACTION_INFO, message.getActionInfo())
        .addValue(PMessage.TITLE, message.getTitle())
        .addValue(PMessage.CONTENT, BaasJSONUtil.safeToJson(message.getContent()))
        .addValue(PMessage.SEND_POS, message.isSendPos())
        .addValue(PMessage.TAG, message.getTag())
        .addValue(PMessage.READ, message.isRead())
        .addValue(PMessage.SOURCE, message.getSource())
        .addValue(PMessage.READ_APP_ID, message.getReadAppId())
        .where(Predicates.equals(PMessage.TENANT, tenant))
        .where(Predicates.equals(PMessage.UUID, message.getUuid()));
    if (message.getReadInfo() != null) {
      OperateInfo readInfo = message.getReadInfo();
      updateBuilder.addValue(PMessage.READ_INFO_OPERATOR_FULL_NAME, readInfo.getOperator().getFullName())
          .addValue(PMessage.READ_INFO_OPERATOR_ID, readInfo.getOperator().getId())
          .addValue(PMessage.READ_INFO_OPERATOR_NAMESPACE, readInfo.getOperator().getNamespace())
          .addValue(PMessage.READ_INFO_TIME, readInfo.getTime());
    }
    UpdateStatement update = updateBuilder.build();
    jdbcTemplate.update(update);
  }

  public void batchInsert(String tenant, Collection<Message> messages, OperateInfo operateInfo) throws BaasException {
    Assert.hasText(tenant, "tenant");
    if (CollectionUtils.isEmpty(messages)) {
      return;
    }
    List<InsertStatement> statements = new ArrayList<>();
    for (Message message : messages) {
      if (StringUtils.isBlank(message.getUuid())) {
        message.setUuid(UUID.randomUUID().toString());
      }
      statements.add(buildInsertStatement(tenant, message, operateInfo));
    }
    batchUpdate(statements);
  }

  private InsertStatement buildInsertStatement(String tenant, Message message, OperateInfo operateInfo)
      throws BaasException {
    message.setCreateInfo(operateInfo);
    message.setLastModifyInfo(operateInfo);
    if (StringUtils.isBlank(message.getUuid())) {
      message.setUuid(UUID.randomUUID().toString());
    }
    InsertBuilder builder = new InsertBuilder().table(PMessage.TABLE_NAME)
        .addValues(PMessage.forSaveNew(message))
        .addValue(PMessage.TENANT, tenant)
        .addValue(PMessage.ORG_ID, message.getOrgId())
        .addValue(PMessage.SHOP, message.getShop())
        .addValue(PMessage.SHOP_CODE, message.getShopCode())
        .addValue(PMessage.SHOP_NAME, message.getShopName())
        .addValue(PMessage.TYPE, message.getType().name())
        .addValue(PMessage.ACTION, message.getAction().name())
        .addValue(PMessage.ACTION_INFO, message.getActionInfo())
        .addValue(PMessage.TITLE, message.getTitle())
        .addValue(PMessage.CONTENT, BaasJSONUtil.safeToJson(message.getContent()))
        .addValue(PMessage.SEND_POS, message.isSendPos())
        .addValue(PMessage.TAG, message.getTag())
        .addValue(PMessage.READ, message.isRead())
        .addValue(PMessage.SOURCE, message.getSource())
        .addValue(PMessage.APP_ID, message.getAppId())
        .addValue(PMessage.READ_APP_ID, message.getReadAppId())
        .addValue(PMessage.USER_ID, message.getUserId());
    if (message.getReadInfo() != null) {
      OperateInfo readInfo = message.getReadInfo();
      builder.addValue(PMessage.READ_INFO_OPERATOR_FULL_NAME, readInfo.getOperator().getFullName())
          .addValue(PMessage.READ_INFO_OPERATOR_ID, readInfo.getOperator().getId())
          .addValue(PMessage.READ_INFO_OPERATOR_NAMESPACE, readInfo.getOperator().getNamespace())
          .addValue(PMessage.READ_INFO_TIME, readInfo.getTime());
    }
    return builder.build();
  }

}
