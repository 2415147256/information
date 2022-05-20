package com.hd123.baas.sop.service.api.menu;

import com.qianfan123.baas.common.BaasException;

import java.util.List;

/**
 * @author W.J.H.7
 * @since 2022-01-23
 */
public interface MenuService {

  /**
   * 新增一个菜单对象
   */
  String saveNew(String tenant, Menu item) throws BaasException;

  /**
   * 修改一个菜单对象
   */
  void saveModify(String tenant, Menu item) throws BaasException;

  /**
   * 移除一个菜单对象
   */
  void remove(String tenant, String uuid) throws BaasException;

  /**
   * 重置菜单
   */
  void reset(String tenant) throws BaasException;

  /**
   * 获取指定菜单对象的详情
   */
  Menu get(String tenant, String uuid);


  /**
   * 获取指定菜单对象的详情
   */
  List<Menu> getByUpperCode(String tenant, String upperCode);

  /**
   * 获取当前租户的对象集合
   */
  List<Menu> listByParent(String tenant);
}
