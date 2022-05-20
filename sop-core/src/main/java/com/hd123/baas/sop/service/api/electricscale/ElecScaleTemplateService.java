package com.hd123.baas.sop.service.api.electricscale;

import com.hd123.rumba.commons.biz.entity.OperateInfo;
import com.hd123.rumba.commons.biz.entity.UCN;
import com.hd123.rumba.commons.biz.query.QueryDefinition;
import com.hd123.rumba.commons.biz.query.QueryResult;
import com.qianfan123.baas.common.BaasException;

import java.util.List;

public interface ElecScaleTemplateService {

  /**
   * 创建电子秤模板
   *
   * @param tenant      租户
   * @param template    模板
   * @param keyboard    电子秤键盘
   * @param operateInfo 操作人
   */
  String create(String tenant, ElecScaleTemplate template, ElecScaleKeyboard keyboard, OperateInfo operateInfo);

  /**
   * 创建电子秤模板
   *
   * @param tenant      租户
   * @param template    模板
   * @param keyboard    电子秤键盘
   * @param operateInfo 操作人
   */
  String modify(String tenant, ElecScaleTemplate template, ElecScaleKeyboard keyboard, OperateInfo operateInfo) throws BaasException;

  /**
   * 电子秤模板绑定门店
   * 
   * @param tenant
   *          租户
   * @param keyBoardTemUuid
   *          模板id
   * @param allShop
   *          是否全部门店
   * @param shops
   *          门店信息
   *
   */
  void bindShop(String tenant, String keyBoardTemUuid, boolean allShop, List<UCN> shops)
      throws BaasException;

  /**
   * 删除模板，包括关联信息
   * 
   * @param tenant
   *          租户
   * @param uuid
   *          uuid
   */
  void delete(String tenant, String uuid) throws BaasException;

  /**
   * 查询模板
   * 
   * @param tenant
   * @param qd
   * @return
   */
  QueryResult<ElecScaleTemplate> query(String tenant, QueryDefinition qd);

  /**
   * 根据id批量获取模板
   *
   * @param tenant
   * @param uuids
   * @return
   */
  List<ElecScaleTemplate> listByUuid(String tenant, List<String> uuids);
}
