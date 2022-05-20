package com.hd123.baas.sop.service.impl.sysconfig;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.hd123.baas.sop.annotation.LogRequestPraras;
import com.hd123.baas.sop.annotation.Tx;
import com.hd123.baas.sop.service.api.sysconfig.SysConfig;
import com.hd123.baas.sop.service.api.sysconfig.SysConfigService;
import com.hd123.baas.sop.service.dao.sysconfig.SysConfigDaoBof;
import com.hd123.baas.sop.evcall.EvCallEventPublisher;
import com.hd123.baas.sop.evcall.exector.sysConfig.SysConfigChangeEvCallExecutor;
import com.hd123.baas.sop.evcall.exector.sysConfig.SysConfigChangeEvCallMsg;
import com.hd123.rumba.commons.lang.Assert;
import com.qianfan123.baas.common.BaasException;

import lombok.extern.slf4j.Slf4j;

/**
 * @author W.J.H.7
 * @since 1.0.0
 **/
@Slf4j
@Service
public class SysConfigServiceImpl implements SysConfigService {

  @Autowired
  private SysConfigDaoBof configDao;
  @Autowired
  private EvCallEventPublisher publisher;

  @Override
  @LogRequestPraras(note = "系统设置")
  @Tx
  public void save(String tenant, SysConfig config) throws BaasException {
    Assert.notNull(tenant, "租户id");
    Assert.notNull(config, "config");
    Assert.notNull(config.getCfKey(), "config.cfKey");

    // 判断是否存在
    SysConfig his = get(tenant, config.getSpec(), config.getCfKey());
    if (his == null) {
      configDao.insert(tenant, config);
    } else {
      configDao.update(tenant, config);
    }

    // 发送异步事件
    SysConfigChangeEvCallMsg msg = new SysConfigChangeEvCallMsg();
    msg.setTenant(tenant);
    msg.setCfgKey(config.getCfKey());
    msg.setSpec(config.getSpec());
    publisher.publishForNormal(SysConfigChangeEvCallExecutor.SYSCONFIG_CHANGE_EXECUTOR_ID, msg);
  }

  @Override
  public SysConfig get(String tenant, String cfKey) {
    Assert.notNull(tenant, "租户id");
    Assert.notNull(cfKey, "cfKey");

    return configDao.get(tenant, "def", cfKey);
  }

  @Override
  public SysConfig get(String tenant, String spec, String cfKey) {
    Assert.notNull(tenant, "租户id");
    Assert.notNull(cfKey, "cfKey");

    return configDao.get(tenant, spec, cfKey);
  }

  @Override
  public List<SysConfig> list(String tenant) {
    Assert.notNull(tenant, "租户id");

    return configDao.list(tenant, "def");
  }

  @Override
  public List<SysConfig> listBySpec(String tenant, String spec) {
    Assert.notNull(tenant, "租户id");
    Assert.notNull(spec, "spec");
    return configDao.list(tenant, spec);
  }

  @Override
  public List<SysConfig> list(String tenant, String cfKey) {
    return configDao.listByKey(tenant, cfKey);
  }

  @Tx
  @Override
  public void batchSave(String tenant, List<SysConfig> configs) {
    Assert.notNull(configs, "店务配置数据");
    if (configs.size() > 0) {
      for (SysConfig config : configs) {
        Assert.notNull(tenant, "租户id");
        Assert.notNull(config, "config");
        Assert.notNull(config.getCfKey(), "config.cfKey");
        // 判断是否存在
        SysConfig his = get(tenant, config.getSpec(), config.getCfKey());
        if (his == null) {
          configDao.insert(tenant, config);
        } else {
          configDao.update(tenant, config);
        }
      }
    }
  }

}
