package com.hd123.baas.sop.service.api.todo;

import com.hd123.rumba.commons.biz.entity.OperateInfo;
import com.hd123.rumba.commons.biz.query.QueryDefinition;
import com.hd123.rumba.commons.biz.query.QueryResult;
import com.qianfan123.baas.common.BaasException;

import java.io.IOException;
import java.util.List;

/**
 * @author mibo
 */
public interface TodoSceneService {

  /**
   * 新增场景
   *
   * @param tenant
   * @param orgId
   * @param todoScene
   * @param operateInfo
   * @return
   */
  String saveNew(String tenant, String orgId, TodoScene todoScene, OperateInfo operateInfo) throws BaasException;

  /**
   * 修改场景
   *
   * @param tenant
   * @param orgId
   * @param todoScene
   * @param operateInfo
   */
  void saveModify(String tenant, String orgId, TodoScene todoScene, OperateInfo operateInfo) throws BaasException;

  /**
   * 删除场景
   *
   * @param tenant
   * @param orgId
   * @param uuid
   */
  void delete(String tenant, String orgId, String uuid) throws BaasException;

  /**
   * 查询场景
   *
   * @param tenant
   * @param orgId
   * @param qd
   * @return
   */
  QueryResult<TodoScene> query(String tenant, String orgId, QueryDefinition qd);

  /**
   * 详情
   *
   * @param tenant
   * @param uuid
   * @return
   */
  TodoScene get(String tenant, String uuid);

  /**
   * 根据code查询
   *
   * @param tenant
   * @param orgId
   * @param code
   * @return
   */
  TodoScene getByCode(String tenant, String orgId, String code);

  /**
   * 创建生成待办
   *
   * @param tenant
   * @param orgId
   * @param createTodoInfo
   * @return
   */
  List<String> createTodo(String tenant, String orgId, CreateTodoInfo createTodoInfo) throws BaasException;
}
