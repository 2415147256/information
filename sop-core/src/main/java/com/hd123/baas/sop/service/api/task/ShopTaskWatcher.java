package com.hd123.baas.sop.service.api.task;

import com.hd123.baas.sop.service.api.TenantEntity;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

/**
 * @author guyahui on 2021/8/25.
 */
@Getter
@Setter
public class ShopTaskWatcher extends TenantEntity {

  private static final long serialVersionUID = -846267442983249410L;
  /**
   * 任务id，巡检中指shopTaskLogId
   */
  private String shopTaskId;
  /**
   * 关注人ID
   */
  private String watcher;
  /**
   * 关注人名称
   */
  private String watcherName;
  /**
   * 关注时间
   */
  private Date created;
}
