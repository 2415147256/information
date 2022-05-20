package com.hd123.baas.sop.service.api.explosivev2.sign;

import com.hd123.rumba.commons.biz.entity.OperateInfo;
import com.hd123.rumba.commons.biz.query.QueryDefinition;
import com.hd123.rumba.commons.biz.query.QueryResult;
import com.qianfan123.baas.common.BaasException;

import java.util.List;

/**
 * 爆品活动报名服务
 *
 * @author liuhaoxin
 * @since 2021-12-2
 */
public interface ExplosiveV2SignService {
  /**
   * 创建 爆品报名
   *
   * @param tenant
   *     租户
   * @param explosiveSignV2
   *     爆品报名
   * @param operateInfo
   *     操作时间
   * @throws BaasException
   *     创建报名异常
   */
  void batchSaveNew(String tenant, List<ExplosiveSignV2> explosiveSignV2, OperateInfo operateInfo) throws BaasException;

  /**
   * 更新 爆品报名
   *
   * @param tenant
   *     租户
   * @param explosiveSignV2
   *     爆品报名
   * @param operateInfo
   *     操作时间
   * @return uuid 报名唯一id
   * @throws BaasException
   *     更新报名异常
   */
  String submit(String tenant, ExplosiveSignV2 explosiveSignV2, OperateInfo operateInfo) throws BaasException;

  /**
   * 爆品报名取消
   *
   * @param tenant
   *     租户
   * @param uuids
   *     报名id
   * @param operateInfo
   *     操作时间
   */
  void cancel(String tenant, List<String> uuids, OperateInfo operateInfo);

  /**
   * 更新 爆品报名行
   *
   * @param tenant
   *     租户
   * @param uuid
   *     爆品报名ID
   * @param lines
   *     修改商品信息
   * @param operateInfo
   *     操作信息
   * @throws BaasException
   *     更新报名行异常
   */
  void changeLinesQty(String tenant, String uuid, List<ExplosiveSignV2Line> lines, OperateInfo operateInfo) throws BaasException;

  /**
   * 报名详情
   *
   * @param tenant
   *     租户
   * @param uuid
   *     报名id
   * @param fetchParts
   *     分片
   * @return ExplosiveSignV2 报名信息
   */
  ExplosiveSignV2 get(String tenant, String uuid, String... fetchParts) throws BaasException;

  /**
   * 报名列表查询
   *
   * @param tenant
   *     租户
   * @param qd
   *     查询
   * @param fetchParts
   *     肥牛片
   * @return QueryResult<ExplosiveSignV2> 报名列表信息
   */
  QueryResult<ExplosiveSignV2> query(String tenant, QueryDefinition qd, String... fetchParts);

  /**
   * 报名列表查询
   *
   * @param tenant
   *     租户
   * @param uuids
   *     查询
   * @param fetchParts
   *     肥牛片
   * @return QueryResult<ExplosiveSignV2> 报名列表信息
   */
  List<ExplosiveSignV2> list(String tenant, List<String> uuids, String... fetchParts);

  /**
   * 报名结束
   *
   * @param tenant
   *     租户
   * @param explosiveIds
   *     爆品活动id
   * @throws BaasException
   *     报名异常
   */
  void setFinish(String tenant, List<String> explosiveIds, OperateInfo operateInfo) throws BaasException;
}
