package com.hd123.baas.sop.service.api.taskplan.template;

import com.qianfan123.baas.common.BaasException;

/**
 * @author zhengzewang on 2020/11/3.
 */
public interface TemplateClsTaskPlan {

  String getName();

  String getDescription();

  boolean wordNeeded();

  boolean imageNeeded();

  /**
   *
   * @param tenant
   *            租户
   * @param checkInfo
   *            需要检验的对象
   * @throws BaasException
   */
  void check(String tenant, Object checkInfo) throws Exception;

}
