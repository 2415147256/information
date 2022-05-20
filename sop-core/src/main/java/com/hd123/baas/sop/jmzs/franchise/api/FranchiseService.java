package com.hd123.baas.sop.jmzs.franchise.api;

import com.hd123.rumba.commons.biz.entity.OperateInfo;
import com.hd123.rumba.commons.biz.query.QueryDefinition;
import com.hd123.rumba.commons.biz.query.QueryResult;

import java.util.List;

public interface FranchiseService {
  String FETCH_SHOP = "shop";
  String[] ALL_PARTS = new String[]{
      FETCH_SHOP
  };


  /**
   * 查询
   *
   * @param tenant
   *     租户
   * @param qd
   *     查询条件
   */
  QueryResult<Franchise> query(String tenant, QueryDefinition qd, String... fetchParts);

  /**
   * 获取有效数据 ,即deleted=false,默认级联获取全部
   *
   * @param tenant
   *        租户
   * @param uuid
   *        标识
   * @return
   */
  Franchise get(String tenant, String uuid);
  /**
   * 获取有效数据 ,即deleted=false,默认级联获取全部
   *
   * @param tenant
   *        租户
   * @param orgId
   *       组织ID
   * @param id
   *        标识
   * @return
   */
  Franchise getById(String tenant, String orgId,String id);

  /**
   * 新建
   *
   * @param tenant
   *        租户
   * @param franchise
   *        加盟商数据
   * @param operateInfo
   *        操作上下文
   * @return
   * @throws Exception
   */
  String saveNew(String tenant, Franchise franchise, OperateInfo operateInfo) throws Exception;

  /**
   * 新建
   *
   * @param tenant
   *        租户
   * @param franchises
   *        加盟商数据
   * @param operateInfo
   *        操作上下文
   * @return
   * @throws Exception
   */
  void batchSave(String tenant, List<Franchise> franchises, OperateInfo operateInfo) throws Exception;

  /**
   * 保存修改
   *
   * @param tenant
   *        租户
   * @param franchise
   *        加盟商数据
   * @param operateInfo
   *        修改上下文
   * @return
   * @throws Exception
   */
  void saveModify(String tenant, Franchise franchise, OperateInfo operateInfo) throws Exception;

}
