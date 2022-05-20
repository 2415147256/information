package com.hd123.baas.sop.service.api.task;

import java.util.List;

/**
 * @Author guyahui
 * @Since
 */
public interface TaskUnfinishedService {
  /**
   * 分页获取未完成的计划列表
   *
   * @param tenant     租户
   * @param operatorId 操作者ID
   * @param pageStart  开始位置
   * @param pageSize   页码大小
   * @return 未完成的计划列表
   */
  List<TaskUnfinished> query(String tenant, String operatorId, Integer pageStart, Integer pageSize);

  /**
   * 获取指定操作人未完成的计划总数量
   *
   * @param tenant     租户
   * @param operatorId 操作者ID
   * @param type       计划类型
   * @return 总数量
   */
  long count(String tenant, String operatorId, String type);
}
