package com.hd123.baas.sop.service.api.skutag;

import java.util.List;

import com.qianfan123.baas.common.BaasException;

/**
 * @Author maodapeng
 * @Since
 */
public interface TagService {

  /**
   * 新增
   * 
   * @param tenant
   * @param tag
   * @throws BaasException
   */
  Integer saveNew(String tenant, Tag tag) throws BaasException;

  /**
   * 修改
   * 
   * @param tenant
   * @param tag
   * @throws BaasException
   */
  void saveModify(String tenant, Tag tag) throws BaasException;

  /**
   * 批量保存
   *
   */
  void batchSave(String tenant, List<Tag> tags) throws BaasException;

  /**
   * 删除
   * 
   * @param tenant
   * @param uuid
   */
  void delete(String tenant, Integer uuid) throws BaasException;

  /**
   * 新增
   * 
   * @param tenant
   * @throws BaasException
   */
  Tag get(String tenant, Integer uuid) throws BaasException;

  /**
   * 列表
   * 
   * @param tenant
   * @param orgIds
   * @return
   */
  List<Tag> list(String tenant,String source, List<String> orgIds);
  /**
   * 所有标签
   */
  List<Tag> list(String tenant);

  /**
   * 列表
   * @param tenant 租户
   * @param uuids 标签id
   * @return
   */
  List<Tag> listByUuids(String tenant, List<Integer> uuids);
}
