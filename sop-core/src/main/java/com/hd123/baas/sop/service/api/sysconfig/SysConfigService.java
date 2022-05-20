package com.hd123.baas.sop.service.api.sysconfig;

import java.util.List;

import com.qianfan123.baas.common.BaasException;

/**
 * @author W.J.H.7
 */
public interface SysConfigService {

  /**
   * 创建消息
   * 
   * @param tenant
   *          租户
   * @param config
   *          配置对象
   */
  void save(String tenant, SysConfig config) throws BaasException;

  /**
   * 获取当前租户的指定key的详情
   *
   */
  SysConfig get(String tenant, String cfKey);

  /**
   * 获取指定配置
   * @param tenant
   * @param spec
   * @param cfKey
   * @return
   */
  SysConfig get(String tenant, String spec, String cfKey);

  /**
   * 获取当前租户的全部配置
   * 
   */
  List<SysConfig> list(String tenant);

  /**
   *
   */
  List<SysConfig> listBySpec(String tenant,String spec);

  /**
   * 获取配置
   *
   */
  List<SysConfig> list(String tenant,String cfKey);

  /**
   * 批量保存数据
   * @param tenant
   * @param configs
   */
  void batchSave(String tenant,List<SysConfig> configs);

}
