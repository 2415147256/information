package com.hd123.baas.sop.service.dao.task;

import com.hd123.baas.sop.service.dao.BofBaseDao;
import com.hd123.baas.sop.service.api.task.ShopTaskGroup;
import com.hd123.baas.sop.service.api.task.ShopTaskGroupState;
import com.hd123.baas.sop.service.api.taskgroup.TaskGroupType;
import com.hd123.rumba.commons.biz.entity.OperateInfo;
import com.hd123.rumba.commons.biz.query.Cop;
import com.hd123.rumba.commons.biz.query.QueryCondition;
import com.hd123.rumba.commons.biz.query.QueryDefinition;
import com.hd123.rumba.commons.biz.query.QueryResult;
import com.hd123.rumba.commons.jdbc.entity.PStandardEntity;
import com.hd123.rumba.commons.jdbc.qd.*;
import com.hd123.rumba.commons.jdbc.sql.*;
import com.hd123.rumba.commons.lang.Assert;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

import static com.hd123.rumba.commons.jdbc.sql.Predicates.like;

/**
 * @author zhengzewang on 2020/11/3.
 */
@Repository
public class ShopTaskGroupDaoBof extends BofBaseDao {

  private static final ShopTaskGroupMapper SHOP_TASK_GROUP_MAPPER = new ShopTaskGroupMapper();

  private QueryProcessor QUERY_PROCESSOR = new QueryProcessorBuilder(ShopTaskGroup.class, PShopTaskGroup.class)
      .addConditionProcessor(new QueryConditionProcessor() {
        @Override
        public Predicate process(QueryCondition condition, QueryProcessContext queryProcessContext)
            throws IllegalArgumentException, QueryProcessException {
          if (condition == null) {
            return null;
          }
          String operation = condition.getOperation();
          String shopKeyWord = ShopTaskGroup.Queries.SHOP_KEYWORD_LIKE;
          if (StringUtils.equals(operation, shopKeyWord)) {
            return Predicates.or(like(PShopTaskGroup.SHOP_CODE, condition.getParameter()),
                like(PShopTaskGroup.SHOP_NAME, condition.getParameter()));
          }
          return null;
        }
      })
      .build();

  public ShopTaskGroup get(String tenant, String uuid) {
    Assert.notNull(tenant, "租户");
    Assert.notNull(uuid, "uuid");
    SelectBuilder select = new SelectBuilder().from(PShopTaskGroup.TABLE_NAME)
        .where(Predicates.equals(PShopTaskGroup.TENANT, tenant))
        .where(Predicates.equals(PShopTaskGroup.UUID, uuid));
    List<ShopTaskGroup> query = jdbcTemplate.query(select.build(), SHOP_TASK_GROUP_MAPPER);
    if (CollectionUtils.isEmpty(query)) {
      return null;
    }
    return query.get(0);
  }

  public ShopTaskGroup getByShop(String tenant, String shopId, TaskGroupType type, Date date) {
    Assert.notNull(tenant, "租户");
    Assert.notNull(shopId, "shopId");
    Assert.notNull(type, "type");
    Assert.notNull(date, "date");
    SelectBuilder select = new SelectBuilder().from(PShopTaskGroup.TABLE_NAME)
        .where(Predicates.equals(PShopTaskGroup.TENANT, tenant))
        .where(Predicates.equals(PShopTaskGroup.TYPE, type.name()))
        .where(Predicates.equals(PShopTaskGroup.SHOP, shopId))
        .where(Predicates.greater(PShopTaskGroup.PLAN_TIME, date))
        .orderBy(PShopTaskGroup.PLAN_TIME, false);
    List<ShopTaskGroup> query = jdbcTemplate.query(select.build(), SHOP_TASK_GROUP_MAPPER);
    if (CollectionUtils.isEmpty(query)) {
      return null;
    }
    return query.get(0);
  }

  public QueryResult<ShopTaskGroup> query(String tenant, QueryDefinition qd) {
    Assert.notNull(qd);
    qd.addByField(ShopTaskGroup.Queries.TENANT, Cop.EQUALS, tenant);
    SelectStatement select = QUERY_PROCESSOR.process(qd);
    return executor.query(select, SHOP_TASK_GROUP_MAPPER);
  }

  public void finish(String tenant, String uuid, String appId, OperateInfo operateInfo) {
    Assert.notNull(tenant, "租户");
    Assert.notNull(uuid, "uuid");

    UpdateBuilder update = new UpdateBuilder().table(PShopTaskGroup.TABLE_NAME)
        .setValue(PShopTaskGroup.STATE, ShopTaskGroupState.FINISHED.name());
    if (operateInfo != null) {
      update.setValue(PShopTaskGroup.FINISH_INFO_OPERATOR_FULL_NAME, operateInfo.getOperator().getFullName())
          .setValue(PShopTaskGroup.FINISH_INFO_OPERATOR_ID, operateInfo.getOperator().getId())
          .setValue(PShopTaskGroup.FINISH_INFO_OPERATOR_NAMESPACE, operateInfo.getOperator().getNamespace())
          .setValue(PShopTaskGroup.FINISH_INFO_TIME, operateInfo.getTime())
          .setValue(PShopTaskGroup.FINISH_APPID, appId);
    }
    update.where(Predicates.equals(PShopTaskGroup.TENANT, tenant))
        .where(Predicates.equals(PShopTaskGroup.UUID, uuid))
        .where(Predicates.equals(PShopTask.STATE, ShopTaskGroupState.UNFINISHED.name()));
    jdbcTemplate.update(update.build());
  }

  public void insert(String tenant, ShopTaskGroup shopTaskGroup) {
    Assert.notNull(tenant, "租户");
    Assert.notNull(shopTaskGroup, "门店任务组");
    OperateInfo createInfo = shopTaskGroup.getCreateInfo();
    OperateInfo lastModifyInfo = shopTaskGroup.getLastModifyInfo();
    InsertBuilder insert = new InsertBuilder().table(PShopTaskGroup.TABLE_NAME)
        .addValue(PShopTaskGroup.TENANT, tenant)
        .addValue(PShopTaskGroup.ORG_ID, shopTaskGroup.getOrgId())
        .addValue(PShopTaskGroup.STATE, shopTaskGroup.getState().name())
        .addValue(PShopTaskGroup.TASK_GROUP, shopTaskGroup.getTaskGroup())
        .addValue(PShopTaskGroup.GROUP_NAME, shopTaskGroup.getGroupName())
        .addValue(PShopTaskGroup.PLAN_TIME, shopTaskGroup.getPlanTime())
        .addValue(PShopTaskGroup.REMIND_TIME, shopTaskGroup.getRemindTime())
        .addValue(PShopTaskGroup.SHOP, shopTaskGroup.getShop())
        .addValue(PShopTaskGroup.SHOP_CODE, shopTaskGroup.getShopCode())
        .addValue(PShopTaskGroup.SHOP_NAME, shopTaskGroup.getShopName())
        .addValue(PShopTaskGroup.TYPE, shopTaskGroup.getType().name())
        .addValue(PShopTaskGroup.UUID, shopTaskGroup.getUuid())
        .addValue(PShopTaskGroup.VERSION, shopTaskGroup.getVersion())
        .addValue(PShopTaskGroup.EARLIEST_FINISH_TIME, shopTaskGroup.getEarliestFinishTime());
    if (createInfo != null) {
      insert.addValues(PStandardEntity.toCreateInfoFieldValues(createInfo));
    }
    if (lastModifyInfo != null) {
      insert.addValues(PStandardEntity.toLastModifyInfoFieldValues(lastModifyInfo));
    }
    jdbcTemplate.update(insert.build());
  }

  public ShopTaskGroup getByShopAndGroupIdAndPlanDate(String tenant, String shop, String groupId, Date planTime) {
    Assert.notNull(tenant, "租户");
    Assert.notNull(groupId, "任务组id");
    Assert.notNull(planTime, "计划日期");
    SelectBuilder select = new SelectBuilder().from(PShopTaskGroup.TABLE_NAME)
        .where(Predicates.equals(PShopTaskGroup.TENANT, tenant))
        .where(Predicates.equals(PShopTaskGroup.TASK_GROUP, groupId))
        .where(Predicates.equals(PShopTaskGroup.SHOP, shop))
        .where(Predicates.equals(PShopTaskGroup.PLAN_TIME, planTime));
    List<ShopTaskGroup> query = jdbcTemplate.query(select.build(), SHOP_TASK_GROUP_MAPPER);
    if (CollectionUtils.isEmpty(query)) {
      return null;
    }
    return query.get(0);
  }

  public void setState(String tenant, String uuid, String state, OperateInfo operateInfo) {
    Assert.notNull(uuid, "uuid");
    Assert.notNull(tenant, "租户");
    UpdateBuilder update = new UpdateBuilder().table(PShopTaskGroup.TABLE_NAME)
        .where(Predicates.equals(PShopTaskGroup.TENANT, tenant))
        .where(Predicates.equals(PShopTaskGroup.UUID, uuid))
        .setValue(PShopTaskGroup.STATE, state)
        .setValues(PShopTaskGroup.toLastModifyInfoFieldValues(operateInfo));
    jdbcTemplate.update(update.build());
  }

  public ShopTaskGroup getLast(String tenant, String shop, String groupId) {
    Assert.notNull(tenant, "租户");
    Assert.notNull(groupId, "任务组id");
    SelectBuilder select = new SelectBuilder().from(PShopTaskGroup.TABLE_NAME)
        .where(Predicates.equals(PShopTaskGroup.TENANT, tenant))
        .where(Predicates.equals(PShopTaskGroup.TASK_GROUP, groupId))
        .where(Predicates.equals(PShopTaskGroup.SHOP, shop))
        .orderBy(PShopTaskGroup.CREATE_INFO_TIME, false)
        .limit(1);
    List<ShopTaskGroup> result = jdbcTemplate.query(select.build(), SHOP_TASK_GROUP_MAPPER);
    if (CollectionUtils.isEmpty(result)) {
      return null;
    }
    return result.get(0);
  }

  public void updateEarliestFinishTime(String tenant, String uuid, Date earliestFinishTime) {
    Assert.notNull(tenant, "租户");
    Assert.notNull(uuid, "uuid");
    Assert.notNull(earliestFinishTime, "最后完成时间");

    UpdateBuilder update = new UpdateBuilder().table(PShopTaskGroup.TABLE_NAME)
        .setValue(PShopTaskGroup.EARLIEST_FINISH_TIME, earliestFinishTime)
        .where(Predicates.equals(PShopTaskGroup.TENANT, tenant))
        .where(Predicates.equals(PShopTaskGroup.UUID, uuid));
    jdbcTemplate.update(update.build());
  }

  public List<ShopTaskGroup> listByShopAndPlanTimeAndState(String tenant, String shop, Date date,
      ShopTaskGroupState state) {
    Assert.notNull(tenant, "租户");
    Assert.notNull(shop, "门店");
    Assert.notNull(date, "日期");
    Assert.notNull(state, "状态");
    SelectBuilder select = new SelectBuilder().from(PShopTaskGroup.TABLE_NAME)
        .where(Predicates.equals(PShopTaskGroup.TENANT, tenant))
        .where(Predicates.equals(PShopTaskGroup.SHOP, shop))
        .where(Predicates.equals(PShopTaskGroup.PLAN_TIME, date))
        .where(Predicates.equals(PShopTaskGroup.STATE, state.name()));
    return jdbcTemplate.query(select.build(), SHOP_TASK_GROUP_MAPPER);
  }
}
