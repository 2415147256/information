package com.hd123.baas.sop.service.api.todo;

import com.hd123.rumba.commons.biz.entity.OperateInfo;
import com.hd123.rumba.commons.biz.query.QueryDefinition;
import com.hd123.rumba.commons.biz.query.QueryResult;
import com.qianfan123.baas.common.BaasException;

import java.util.List;

/**
 * @author mibo
 */
public interface TodoSettingService {
  /**
   * 新增设置
   *
   * @param tenant 租户
   * @param orgId 组织
   * @param todoSetting 待办配置/消息配置
   * @param operateInfo 操作人信息
   * @return
   */
  String saveNew(String tenant, String orgId, TodoSetting todoSetting, OperateInfo operateInfo) throws BaasException;

  /**
   * 修改设置
   *
   * @param tenant 租户
   * @param orgId 组织
   * @param todoSetting 待办配置/消息配置
   * @param operateInfo 操作人信息
   */
  void saveModify(String tenant, String orgId, TodoSetting todoSetting, OperateInfo operateInfo) throws BaasException;

  /**
   * 获取设置
   *
   * @param tenant 租户
   * @param uuid ID
   */
  TodoSetting get(String tenant, String uuid);

  /**
   * 查询设置
   *
   * @param tenant 租户
   * @param orgId 组织
   * @param qd 查询条件
   * @return
   */
  QueryResult<TodoSetting> query(String tenant, String orgId, QueryDefinition qd);

  /**
   * 获取待办场景配置
   *
   * @param tenant 租户
   * @param orgId 组织
   * @param sceneId 待办场景ID
   * @return
   */
  List<TodoSetting> listBySceneId(String tenant, String orgId, String sceneId);
}
