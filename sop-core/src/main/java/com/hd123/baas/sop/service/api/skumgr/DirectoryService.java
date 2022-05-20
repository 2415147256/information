package com.hd123.baas.sop.service.api.skumgr;

import com.hd123.rumba.commons.biz.entity.OperateInfo;
import com.hd123.rumba.commons.biz.query.QueryResult;
import com.qianfan123.baas.common.BaasException;

import java.util.List;

/**
 * @Author maodapeng
 * @Since
 */
public interface DirectoryService {
  /**
   * 创建目录
   *
   * @param tenant
   * @param name
   */
  String saveNew(String tenant, String orgId, String name, OperateInfo operateInfo) throws BaasException;

  /**
   * 目录列表
   *
   * @param tenant 租户
   * @param orgId 组织id
   * @return
   */
  List<Directory> list(String tenant, String orgId);

  /**
   * 通过组织id查询列表
   * @param tenant
   * @param orgIds
   * @return
   */
  List<Directory> listByOrgIds(String tenant, List<String> orgIds);

  /**
   * 获取目录详情
   * @param tenant
   * @param uuid
   * @return
   */
  Directory get(String tenant,String uuid);

  /**
   * 删除目录
   *
   * @param tenant 租户
   * @param orgId 组织id
   * @param uuid 目录id
   * @param operateInfo 操作人
   * @throws BaasException
   */
  void delete(String tenant, String orgId, String uuid, OperateInfo operateInfo) throws BaasException;

  /**
   * 目录商品
   *
   * @param tenant
   * @param uuid
   * @param filter
   * @return
   * @throws BaasException
   */
  QueryResult<DirectorySku> queryDirectorySku(String tenant, String uuid, DirectorySkuFilter filter)
      throws BaasException;

  /**
   * 关联门店
   *
   * @param tenant
   * @param uuid
   * @param orgId
   * @param shops
   * @param operateInfo
   * @throws BaasException
   */
  void relateShops(String tenant, String orgId, String uuid, List<String> shops, OperateInfo operateInfo) throws BaasException;

  /**
   * 删除关联门店
   *
   * @param tenant
   * @param uuid
   * @param shops
   * @param operateInfo
   */
  void deleteShops(String tenant, String orgId, String uuid, List<String> shops, OperateInfo operateInfo) throws BaasException;

  /**
   * 查询目录门店门店
   *
   * @param tenant 租户
   * @param orgId  组织id
   * @param uuid   目录id
   * @return
   * @throws BaasException
   */
  List<DirectoryShop> queryShops(String tenant, String orgId, String uuid) throws BaasException;

  /**
   * 新增目录商品
   *
   * @param tenant
   * @param uuid
   * @param skuIds
   * @param operateInfo
   * @throws BaasException
   */
  void addSku(String tenant, String orgId, String uuid, List<String> skuIds, OperateInfo operateInfo) throws BaasException;

  /**
   * 删除目录商品
   *
   * @param tenant
   * @param uuid
   * @param skuIds
   * @param operateInfo
   * @throws BaasException
   */
  void deleteSku(String tenant, String orgId, String uuid, List<String> skuIds, OperateInfo operateInfo) throws BaasException;

  /**
   * 设置渠道必定属性
   *
   * @param tenant
   * @param operateInfo
   * @throws BaasException
   */
  void selectDirectoryRequired(String tenant, String orgId, String uuid, List<SkuProperty> skuProperties, OperateInfo operateInfo)
      throws BaasException;

}
