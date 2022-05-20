package com.hd123.baas.sop.service.dao.taskgroup;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.hd123.baas.sop.service.api.taskgroup.TaskGroupType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import com.hd123.baas.sop.service.dao.BofBaseDao;
import com.hd123.baas.sop.service.api.taskgroup.TaskGroupShop;
import com.hd123.rumba.commons.jdbc.sql.*;
import com.hd123.rumba.commons.lang.Assert;

@Service
public class TaskGroupShopDaoBof extends BofBaseDao {
  @Autowired
  JdbcTemplate jdbcTemplate;

  public static final TaskGroupShopMapper TASK_GROUP_SHOP_MAPPER = new TaskGroupShopMapper();

  public void batchInsert(String tenant, String taskGroupId, List<String> shops) {
    Assert.notNull(tenant);
    Assert.notNull(taskGroupId);
    Assert.notNull(shops);
    List<InsertStatement> list = new ArrayList<>();
    for (String shop : shops) {
      InsertBuilder insertBuilder = new InsertBuilder().table(PTaskGroupShop.TABLE_NAME)
          .addValue(PTaskGroupShop.TENANT, tenant)
          .addValue(PTaskGroupShop.TASK_GROUP, taskGroupId)
          .addValue(PTaskGroupShop.SHOP, shop)
          .addValue(PTaskGroupShop.UUID, UUID.randomUUID().toString());
      list.add(insertBuilder.build());
    }
    batchUpdate(list);
  }

  public void insert(String tenant,String taskGroupId,String shop){
    Assert.notNull(tenant);
    Assert.notNull(taskGroupId);
    Assert.notNull(shop);
    InsertBuilder insertBuilder = new InsertBuilder().table(PTaskGroupShop.TABLE_NAME)
            .addValue(PTaskGroupShop.TENANT, tenant)
            .addValue(PTaskGroupShop.TASK_GROUP, taskGroupId)
            .addValue(PTaskGroupShop.SHOP, shop)
            .addValue(PTaskGroupShop.UUID, UUID.randomUUID().toString());
    jdbcTemplate.update(insertBuilder.build());
  }

  public void deleteByGroupId(String tenant, String taskGroup) {
    Assert.notNull(tenant, "租户");
    Assert.notNull(taskGroup, "uuid");
    DeleteBuilder deleteBuilder = new DeleteBuilder().table(PTaskGroupShop.TABLE_NAME)
        .where(Predicates.equals(PTaskGroupShop.TENANT, tenant))
        .where((Predicates.equals(PTaskGroupShop.TASK_GROUP, taskGroup)));
    jdbcTemplate.update(deleteBuilder.build());
  }

  public List<TaskGroupShop> getRelateShops(String tenant, String groupId) {
    Assert.notNull(tenant, "租户");
    Assert.notNull(groupId, "groupId");
    SelectBuilder select = new SelectBuilder().from(PTaskGroupShop.TABLE_NAME)
        .where(Predicates.equals(PTaskGroupShop.TASK_GROUP, groupId))
        .where(Predicates.equals(PTaskGroupShop.TENANT, tenant));
    return jdbcTemplate.query(select.build(), TASK_GROUP_SHOP_MAPPER);
  }

  public List<TaskGroupShop> getByShop(String tenant, String shop, TaskGroupType type) {
    Assert.notNull(tenant, "租户");
    Assert.notNull(shop, "门店");
    SelectBuilder select = new SelectBuilder().from(PTaskGroupShop.TABLE_NAME, PTaskGroupShop.TABLE_ALIAS)
        .leftJoin(PTaskGroup.TABLE_NAME, PTaskGroup.TABLE_ALIAS,
            Predicates.equals(PTaskGroup.TABLE_ALIAS, PTaskGroup.UUID, PTaskGroupShop.TABLE_ALIAS,
                PTaskGroupShop.TASK_GROUP))
        .where(Predicates.equals(PTaskGroupShop.TABLE_ALIAS, PTaskGroupShop.TENANT, tenant))
        .where(Predicates.equals(PTaskGroupShop.TABLE_ALIAS, PTaskGroupShop.SHOP, shop))
        .where(Predicates.equals(PTaskGroup.TYPE, type.name()));
    return jdbcTemplate.query(select.build(), TASK_GROUP_SHOP_MAPPER);
  }

  public List<TaskGroupShop> getRelateShops(String tenant, TaskGroupType type) {
    Assert.notNull(tenant, "租户");
    SelectBuilder select = new SelectBuilder().from(PTaskGroupShop.TABLE_NAME, PTaskGroupShop.TABLE_ALIAS)
        .leftJoin(PTaskGroup.TABLE_NAME, PTaskGroup.TABLE_ALIAS,
            Predicates.equals(PTaskGroup.TABLE_ALIAS, PTaskGroup.UUID, PTaskGroupShop.TABLE_ALIAS,
                PTaskGroupShop.TASK_GROUP))
        .where(Predicates.equals(PTaskGroupShop.TABLE_ALIAS, PTaskGroupShop.TENANT, tenant))
        .where(Predicates.equals(PTaskGroup.TABLE_ALIAS, PTaskGroup.TYPE, type.name()));
    return jdbcTemplate.query(select.build(), TASK_GROUP_SHOP_MAPPER);
  }

  public static void main(String[] args) {
    TaskGroupType type = TaskGroupType.DAILY;
    SelectBuilder select = new SelectBuilder().from(PTaskGroupShop.TABLE_NAME, PTaskGroupShop.TABLE_ALIAS)
        .leftJoin(PTaskGroup.TABLE_NAME, PTaskGroup.TABLE_ALIAS,
            Predicates.equals(PTaskGroup.TABLE_ALIAS, PTaskGroup.UUID, PTaskGroupShop.TABLE_ALIAS,
                PTaskGroupShop.TASK_GROUP))
        .where(Predicates.equals(PTaskGroupShop.TABLE_ALIAS, PTaskGroupShop.TENANT, "tenant"))
        .where(Predicates.equals(PTaskGroup.TABLE_ALIAS, PTaskGroup.TYPE, type.name()));

    System.out.println(select.build().getSql());
  }
}
