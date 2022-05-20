package com.hd123.baas.sop.service.api.announcement;

import com.hd123.rumba.commons.biz.entity.OperateInfo;
import com.hd123.rumba.commons.biz.query.QueryDefinition;
import com.hd123.rumba.commons.biz.query.QueryResult;
import com.qianfan123.baas.common.BaasException;

import java.util.Collection;
import java.util.List;

/**
 * @author zhengzewang on 2020/11/20.
 */
public interface AnnouncementService {

  /**
   * 分页查询
   *
   * @param tenant
   * @param qd
   * @return
   */
  QueryResult<Announcement> query(String tenant, QueryDefinition qd);

  /**
   * 详情查询
   *
   * @param tenant 租户
   * @param uuid   公告id
   * @return 公告信息
   */
  Announcement get(String tenant, String uuid);

  /**
   * 新增
   *
   * @param tenant       租户
   * @param announcement 公告
   * @param operateInfo  操作人信息
   */
  String saveNew(String tenant, Announcement announcement, OperateInfo operateInfo) throws BaasException;

  /**
   * 编辑
   *
   * @param tenant       租户
   * @param announcement 公告
   * @param operateInfo  操作人信息
   */
  void saveModify(String tenant, Announcement announcement, OperateInfo operateInfo) throws BaasException;

  /**
   * 发布。同时发送异步生成message
   *
   * @param tenant      租户
   * @param uuid        公告id
   * @param operateInfo 操作人信息
   */
  void publish(String tenant, String uuid, OperateInfo operateInfo) throws BaasException;

  /**
   * 发布 同时发送异步生成message
   *
   * @param tenant      租户
   * @param uuids       公告id
   * @param operateInfo 操作人详细
   * @throws BaasException
   */
  void batchPublish(String tenant, Collection<String> uuids, OperateInfo operateInfo) throws BaasException;

  /**
   * 删除
   *
   * @param tenant 租户
   * @param uuid   公告id
   */
  void delete(String tenant, String uuid) throws BaasException;

  /**
   * 批量删除
   *
   * @param tenant 租户
   * @param uuids  公告id集合
   */
  void batchDelete(String tenant, Collection<String> uuids) throws BaasException;

  /**
   * 关联门店
   *
   * @param tenant  租户
   * @param uuid    单据id
   * @param shopIds 门店id集合
   * @throws BaasException 业务异常
   */
  void relateShops(String tenant, String uuid, List<String> shopIds) throws BaasException;

  /**
   * 查询关联门店
   *
   * @param tenant 租户
   * @param uuid   单据id
   * @return 关联门店列表
   */
  List<AnnouncementShop> listShops(String tenant, String uuid);

  /**
   * 通知被读取了
   *
   * @param tenant 租户
   * @param uuid   公告id
   */
  void noticeRead(String tenant, String orgId, String uuid, OperateInfo operateInfo) throws BaasException;

  /**
   * 通过uuid查询
   * @param tenant
   * @param uuids
   * @return
   */
  List<Announcement> listByIds(String tenant, List<String> uuids);

}
