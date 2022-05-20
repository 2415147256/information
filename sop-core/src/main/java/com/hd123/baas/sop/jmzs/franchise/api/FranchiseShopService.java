package com.hd123.baas.sop.jmzs.franchise.api;

import com.hd123.rumba.commons.biz.entity.OperateInfo;

import java.util.List;

public interface FranchiseShopService {
  /**
   * 保存加盟商-门店关系
   *
   * @param tenant
   *        租户
   * @param franchiseShops
   *        加盟商-门店
   * @param operateInfo
   *        操作上下文
   */
  void save(String tenant, List<FranchiseShopAssignment> franchiseShops, OperateInfo operateInfo) throws Exception;


  /**
   * 根据加盟商获取关系
   *
   * @param tenant
   *        租户
   * @param franchiseUuid
   *        加盟商uuid
   * @return
   */
  List<FranchiseShopAssignment> listByUuid(String tenant, String franchiseUuid);

}
