package com.hd123.baas.sop.service.dao.task;

import com.hd123.baas.sop.service.api.task.ShopTaskWatcher;
import com.hd123.rumba.commons.jdbc.annotation.MapToEntity;
import com.hd123.rumba.commons.jdbc.annotation.SchemaMeta;
import com.hd123.rumba.commons.jdbc.annotation.TableName;
import com.hd123.rumba.commons.jdbc.entity.PEntity;
import lombok.Getter;
import lombok.Setter;

@SchemaMeta
@MapToEntity(ShopTaskWatcher.class)
@Getter
@Setter
public class PShopTaskWatcher extends PEntity {
  @TableName
  public static final String TABLE_NAME = "shop_task_watcher";
  public static final String TABLE_ALIAS = "_shop_task_watcher";

  public static final String TENANT = "tenant";

  /**
   * 任务id，巡检中指shopTaskLogId
   */
  public static final String SHOP_TASK_ID = "shop_task_id";
  /**
   * 关注人ID
   */
  public static final String WATCHER = "watcher";
  /**
   * 关注人名称
   */
  public static final String WATCHER_NAME = "watcher_name";
  /**
   * 关注时间
   */
  public static final String CREATED = "created";
}
