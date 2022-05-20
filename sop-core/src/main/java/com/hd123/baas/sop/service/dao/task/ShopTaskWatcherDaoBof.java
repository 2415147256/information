package com.hd123.baas.sop.service.dao.task;

import com.hd123.baas.sop.service.dao.BofBaseDao;
import com.hd123.baas.sop.service.api.task.ShopTask;
import com.hd123.baas.sop.service.api.task.ShopTaskState;
import com.hd123.baas.sop.service.api.task.ShopTaskWatcher;
import com.hd123.baas.sop.service.api.taskplan.TaskPlanType;
import com.hd123.rumba.commons.jdbc.sql.*;
import com.hd123.rumba.commons.jdbc.temapper.TEMapper;
import com.hd123.rumba.commons.jdbc.temapper.TEMapperBuilder;
import com.hd123.rumba.commons.lang.Assert;
import com.hd123.rumba.commons.util.converter.EnumConverters;
import org.apache.commons.lang.StringUtils;
import org.springframework.jdbc.core.SingleColumnRowMapper;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author guyahui
 * @Since
 */
@Repository
public class ShopTaskWatcherDaoBof extends BofBaseDao {

  public static final TEMapper<ShopTask> MAPPER = TEMapperBuilder.of(ShopTask.class, PShopTask.class)
      .primaryKey(PShopTask.TENANT, PShopTask.UUID)
      .map("state", PShopTask.STATE, EnumConverters.toString(ShopTaskState.class),
          EnumConverters.toEnum(ShopTaskState.class))
      .build();
  public static final TEMapper<ShopTaskWatcher> WATCHER_TE_MAPPER = TEMapperBuilder.of(ShopTaskWatcher.class, PShopTaskWatcher.class)
      .primaryKey(PShopTaskWatcher.UUID, PShopTaskWatcher.TENANT)
      .build();

  public List<ShopTask> query(String tenant, String operatorId, String keyword, List<String> shopTaskStateList, Integer pageStart, Integer pageSize) {
    Assert.hasText(tenant, "tenant");
    Assert.hasText(operatorId, "operatorId");
    Assert.notEmpty(shopTaskStateList, "shopTaskStateList");
    Assert.notNull(pageStart, "pageStart");
    Assert.notNull(pageSize, "pageSize");

    /*
 		SELECT a.*FROM shop_task a WHERE a.tenant='mkhtest' AND a.uuid IN (
        SELECT b.OWNER FROM shop_task_log b WHERE b.tenant='mkhtest' AND b.uuid IN (
        SELECT c.shop_task_id FROM shop_task_watcher c WHERE b.tenant='mkhtest' AND c.watcher='hdposadmin'))
        AND a.plan_type='INSPECTION' AND (plan_name LIKE '%%' OR shop_name LIKE '%%')
        ORDER BY CASE WHEN state='UNFINISHED' THEN 1 WHEN state='FINISHED' THEN 2 WHEN state='EXPIRED' THEN 3 WHEN state='TERMINATE' THEN 4 END,a.plan_end_time DESC LIMIT 1000
     */
    SelectStatement selectShopTaskWatcher = new SelectBuilder().select("c.shop_task_id")
        .from(PShopTaskWatcher.TABLE_NAME, "c")
        .where(Predicates.equals("c", PShopTaskWatcher.TENANT, tenant))
        .where(Predicates.equals("c", PShopTaskWatcher.WATCHER, operatorId))
        .build();
    SelectStatement selectShopTaskLog = new SelectBuilder().select("b.owner")
        .from(PShopTaskLog.TABLE_NAME, "b")
        .where(Predicates.equals("b", PShopTaskLog.TENANT, tenant))
        .where(Predicates.in("b", PShopTaskLog.UUID, selectShopTaskWatcher))
        .build();
    SelectStatement select = new SelectBuilder()
        .from(PShopTask.TABLE_NAME, "a")
        .where(Predicates.equals("a", PShopTask.TENANT, tenant))
        .where(Predicates.equals("a", PShopTask.PLAN_TYPE, TaskPlanType.INSPECTION.name()))
        .where(Predicates.in("a", PShopTask.UUID, selectShopTaskLog))
        .where(Predicates.in2("a." + PShopTask.STATE, shopTaskStateList.toArray()))
        .where(Predicates.when(StringUtils.isNotEmpty(keyword),
            Predicates.or(Predicates.like(PShopTask.PLAN_NAME, keyword), Predicates.like(PShopTask.SHOP_NAME, keyword))))
        .orderBy("case " +
            "when state='" + ShopTaskState.UNFINISHED.name() + "' then 1 " +
            "when state='" + ShopTaskState.FINISHED.name() + "' then 2 " +
            "when state='" + ShopTaskState.EXPIRED.name() + "' then 3 " +
            "when state='" + ShopTaskState.TERMINATE.name() + "' then 4 " +
            "end")
        .orderBy(PShopTask.PLAN_END_TIME)
        .limit(pageStart, pageSize).build();
    return jdbcTemplate.query(select, MAPPER);
  }

  public long count(String tenant, String operatorId, String keyword, List<String> shopTaskStateList) {
    Assert.hasText(tenant, "tenant");
    Assert.hasText(operatorId, "operatorId");
    Assert.notEmpty(shopTaskStateList, "shopTaskStateList");

    /*
  	   SELECT count(1) as cnt from
	   (SELECT a.*FROM shop_task a WHERE a.tenant='mkhtest' AND a.uuid IN (
        SELECT b.OWNER FROM shop_task_log b WHERE b.tenant='mkhtest' AND b.uuid IN (
        SELECT c.shop_task_id FROM shop_task_watcher c WHERE b.tenant='mkhtest' AND c.watcher='hdposadmin'))
        AND a.plan_type='INSPECTION' AND (plan_name LIKE '%%' OR shop_name LIKE '%%')
        ORDER BY CASE WHEN state='UNFINISHED' THEN 1 WHEN state='FINISHED' THEN 2 WHEN state='EXPIRED' THEN 3 WHEN state='TERMINATE' THEN 4 END,a.plan_end_time DESC) d;
     */

    SelectStatement selectShopTaskWatcher = new SelectBuilder().select("c.shop_task_id")
        .from(PShopTaskWatcher.TABLE_NAME, "c")
        .where(Predicates.equals("c", PShopTaskWatcher.TENANT, tenant))
        .where(Predicates.equals("c", PShopTaskWatcher.WATCHER, operatorId))
        .build();
    SelectStatement selectShopTaskLog = new SelectBuilder().select("b.owner")
        .from(PShopTaskLog.TABLE_NAME, "b")
        .where(Predicates.equals("b", PShopTaskLog.TENANT, tenant))
        .where(Predicates.in("b", PShopTaskLog.UUID, selectShopTaskWatcher))
        .build();
    SelectStatement select = new SelectBuilder()
        .from(PShopTask.TABLE_NAME, "a")
        .where(Predicates.equals("a", PShopTask.TENANT, tenant))
        .where(Predicates.equals("a", PShopTask.PLAN_TYPE, TaskPlanType.INSPECTION.name()))
        .where(Predicates.in("a", PShopTask.UUID, selectShopTaskLog))
        .where(Predicates.in2("a." + PShopTask.STATE, shopTaskStateList.toArray()))
        .where(Predicates.when(StringUtils.isNotEmpty(keyword),
            Predicates.or(Predicates.like(PShopTask.PLAN_NAME, keyword), Predicates.like(PShopTask.SHOP_NAME, keyword))))
        .build();
    SelectStatement selectCount = new SelectBuilder().select("count(1) as cnt")
        .from(select, "d").build();
    List<Long> count = jdbcTemplate.query(selectCount, new SingleColumnRowMapper<>());
    return count.get(0);
  }

  public List<String> listShopTaskIdByWatcher(String tenant, String operatorId) {
    Assert.hasText(tenant, "tenant");
    Assert.hasText(operatorId, "operatorId");

    SelectStatement selectStatement = new SelectBuilder()
        .select(PShopTaskWatcher.SHOP_TASK_ID)
        .distinct()
        .from(PShopTaskWatcher.TABLE_NAME)
        .where(Predicates.equals(PShopTaskWatcher.TENANT, tenant))
        .where(Predicates.equals(PShopTaskWatcher.WATCHER, operatorId))
        .build();
    return jdbcTemplate.query(selectStatement, new SingleColumnRowMapper<>());
  }

  public void saveNew(String tenant, ShopTaskWatcher shopTaskWatcher) {
    Assert.hasText(tenant, "tenant");
    Assert.notNull(shopTaskWatcher, "shopTaskWatcher");

    InsertBuilder insert = new InsertBuilder().table(PShopTaskWatcher.TABLE_NAME).addValues(WATCHER_TE_MAPPER.forInsert(shopTaskWatcher));
    jdbcTemplate.update(insert.build());
  }

  public void batchSave(String tenant, List<ShopTaskWatcher> shopTaskWatcherList) {
    Assert.hasText(tenant, "tenant");
    Assert.notEmpty(shopTaskWatcherList, "shopTaskWatcherList");

    List<InsertStatement> list = new ArrayList<>();
    for (ShopTaskWatcher shopTaskWatcher : shopTaskWatcherList) {
      shopTaskWatcher.setTenant(tenant);
      InsertBuilder insert = new InsertBuilder().table(PShopTaskWatcher.TABLE_NAME).addValues(WATCHER_TE_MAPPER.forInsert(shopTaskWatcher));
      list.add(insert.build());
    }
    batchUpdate(list);
  }

  public ShopTaskWatcher getByWatcherAndShopTaskId(String tenant, String watcher, String shopTaskId) {
    Assert.hasText(tenant, "tenant");
    Assert.hasText(watcher, "watcher");
    Assert.hasText(shopTaskId, "shopTaskId");

    SelectStatement selectStatement = new SelectBuilder()
        .from(PShopTaskWatcher.TABLE_NAME)
        .where(Predicates.equals(PShopTaskWatcher.TENANT, tenant))
        .where(Predicates.equals(PShopTaskWatcher.WATCHER, watcher))
        .where(Predicates.equals(PShopTaskWatcher.SHOP_TASK_ID, shopTaskId))
        .build();
    return getFirst(jdbcTemplate.query(selectStatement, WATCHER_TE_MAPPER));
  }

  public List<ShopTaskWatcher> listByWatcherAndShopTaskIdList(String tenant, String watcher, List<String> shopTaskIdList) {
    Assert.hasText(tenant, "tenant");
    Assert.hasText(watcher, "watcher");
    Assert.notEmpty(shopTaskIdList, "shopTaskIdList");

    SelectStatement selectStatement = new SelectBuilder()
        .from(PShopTaskWatcher.TABLE_NAME)
        .where(Predicates.equals(PShopTaskWatcher.TENANT, tenant))
        .where(Predicates.equals(PShopTaskWatcher.WATCHER, watcher))
        .where(Predicates.in2(PShopTaskWatcher.SHOP_TASK_ID, shopTaskIdList.toArray()))
        .build();
    return jdbcTemplate.query(selectStatement, WATCHER_TE_MAPPER);
  }
}
