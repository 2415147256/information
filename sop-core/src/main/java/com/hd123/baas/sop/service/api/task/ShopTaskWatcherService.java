package com.hd123.baas.sop.service.api.task;

import java.util.List;

/**
 * @Author guyahui
 * @Since
 */
public interface ShopTaskWatcherService {
  /**
   * 分页获取未完成的计划列表
   *
   * @param tenant            租户
   * @param operatorId        操作者ID
   * @param shopTaskStateList 状态范围
   * @param pageStart         开始位置
   * @param pageSize          页码大小
   * @return 未完成的计划列表
   */
  List<ShopTask> query(String tenant, String operatorId, String keyword, List<String> shopTaskStateList, Integer pageStart, Integer pageSize);

  /**
   * 获取指定操作人未完成的计划总数量
   *
   * @param tenant            租户
   * @param operatorId        操作者ID
   * @param shopTaskStateList 状态范围
   * @return 总数量
   */
  long count(String tenant, String operatorId, String keyword, List<String> shopTaskStateList);

  /**
   * 批量保存关注信息列表
   *
   * @param tenant              租户
   * @param shopTaskWatcherList 关注信息列表
   */
  void batchSave(String tenant, List<ShopTaskWatcher> shopTaskWatcherList);

  /**
   * 保存任务关注信息
   *
   * @param tenant          租户
   * @param shopTaskWatcher 关注信息
   */
  void saveNew(String tenant, ShopTaskWatcher shopTaskWatcher);

  /**
   * 获取关注人关注的任务小项列表
   *
   * @param tenant     租户
   * @param shopTaskId shopTaskId
   * @param operatorId 当前登录人
   * @return ShopTaskLog列表
   */
  List<ShopTaskLog> listShopTaskLogByShopTaskId(String tenant, String shopTaskId, String operatorId);

  /**
   * 根据tenant、watcher、shopTaskId获取我的关注
   *
   * @param tenant     租户
   * @param watcher    关注人
   * @param shopTaskId 在巡检中指shopTaskLogId
   * @return 关注信息
   */
  ShopTaskWatcher getByWatcherAndShopTaskLogId(String tenant, String watcher, String shopTaskId);

  /**
   * 根据tenant、watcher、shopTaskId获取我的关注列表
   *
   * @param tenant         租户
   * @param watcher        关注人
   * @param shopTaskIdList 在巡检中指shopTaskLogId列表
   * @return 关注信息列表
   */
  List<ShopTaskWatcher> listByWatcherAndShopTaskLogIdList(String tenant, String watcher, List<String> shopTaskIdList);
}
