package com.hd123.baas.sop.service.api.screen;

import java.util.Date;

import com.hd123.rumba.commons.biz.entity.OperateInfo;
import com.hd123.rumba.commons.biz.query.QueryDefinition;
import com.hd123.rumba.commons.biz.query.QueryResult;
import com.qianfan123.baas.common.BaasException;

/**
 * (PriceScreen)表服务接口
 *
 * @author liuhaoxin
 * @since 2021-08-09 11:39:26
 */
public interface PriceScreenService {

  /**
   * 新增
   *
   * @param tenant
   *          租户
   * @param priceScreen
   *          价格屏
   * @return uuid
   * @throws BaasException
   */
  String saveNew(String tenant, PriceScreen priceScreen, OperateInfo operateInfo) throws BaasException;

  /**
   * 删除（逻辑）
   *
   * @param tenant
   *          租户
   * @param uuid
   * @return 对象列表
   */
  void deleted(String tenant, String uuid, OperateInfo operateInfo) throws BaasException;

  /**
   * 编辑
   *
   * @param tenant
   *          租户
   * @param priceScreen
   *          实例对象
   * @return 实例对象
   */
  void saveModify(String tenant, PriceScreen priceScreen, OperateInfo operateInfo) throws BaasException;

  /**
   * 终止
   * 
   * @param tenant
   *          租户
   * @param uuid
   *          价格屏id
   * @param operateInfo
   *          操作时间
   */
  void terminate(String tenant, String uuid, OperateInfo operateInfo) throws BaasException;

  /**
   * 详情
   *
   * @param tenant
   *          租户
   * @param uuid
   *          对象id
   * @return 实例对象
   */
  PriceScreen get(String tenant, String uuid);

  /**
   * 查询
   *
   * @param tenant
   *          租户
   * @param qd
   *          自定义查询
   * @return 查询结果
   */
  QueryResult<PriceScreen> query(String tenant, QueryDefinition qd);

  /**
   * 价格屏状态更改成生效中
   * 
   * @param tenant
   *          租户
   * @param date
   *          时间
   */
  void effect(String tenant, Date date, OperateInfo operateInfo);

  /**
   * 价格屏状态更改成已失效
   * 
   * @param tenant
   *          租户
   * @param date
   *          时间
   */
  void expire(String tenant, Date date, OperateInfo operateInfo);
}
