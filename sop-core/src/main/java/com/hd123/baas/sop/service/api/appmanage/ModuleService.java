package com.hd123.baas.sop.service.api.appmanage;

import com.hd123.rumba.commons.biz.entity.OperateInfo;

import java.util.List;

public interface ModuleService {
  /**
   * 查询用户所选应用
   */
  List<Module> listByUserId(String tenant, String userId);

  /**
   * 查询所有应用
   */
  List<Module> all(String tenant);

  /**
   * 设置我的应用
   */
  void saveModify(String tenant, String userId, List<UserModule> applications, OperateInfo operateInfo);

  /**
   * 更具id查询应用
   */
  List<Module> queryByIds(String tenant, List<String> list);
}
