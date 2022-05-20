package com.hd123.baas.sop.service.impl.application;

import com.hd123.baas.sop.annotation.Tx;
import com.hd123.baas.sop.service.api.appmanage.Module;
import com.hd123.baas.sop.service.api.appmanage.ModuleService;
import com.hd123.baas.sop.service.api.appmanage.UserModule;
import com.hd123.baas.sop.service.dao.application.ApplicationDaoBof;
import com.hd123.baas.sop.service.dao.application.UserApplicationDaoBof;
import com.hd123.rumba.commons.biz.entity.OperateInfo;
import com.hd123.rumba.commons.lang.Assert;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class ModuleServiceImpl implements ModuleService {

  @Autowired
  private ApplicationDaoBof applicationDao;

  @Autowired
  private UserApplicationDaoBof userApplicationDao;

  @Override
  public List<Module> listByUserId(String tenant, String userId) {
    Assert.notNull(tenant, "租户");
    Assert.notNull(userId, "userid");
    List<UserModule> list = userApplicationDao.queryApplicationIdsByUserId(tenant, userId);
    if (CollectionUtils.isEmpty(list)) {
      log.info("该用户应用为空userId:{}",userId);
      return null;
    }
    List<String> modules = list.stream().map(UserModule::getApplication).collect(Collectors.toList());

    return applicationDao.queryByIds(tenant,modules);
  }

  @Override
  public List<Module> all(String tenant) {
    Assert.notNull(tenant);
    return applicationDao.all(tenant);
  }

  @Override
  @Tx
  public void saveModify(String tenant, String userId, List<UserModule> applications, OperateInfo operateInfo) {
    Assert.notNull(tenant,"租户");
    Assert.notNull(userId,"用户");
    userApplicationDao.deleteByUserId(tenant,userId);
    if(CollectionUtils.isEmpty(applications)){
      log.info("该用户添加应该为空");
      return;
    }
    userApplicationDao.batchSave(tenant,userId,applications,operateInfo);
  }

  @Override
  public List<Module> queryByIds(String tenant, List<String> list) {
    Assert.notNull(tenant,"租户");
    Assert.notEmpty(list,"ids");

    return applicationDao.queryByIds(tenant,list);
  }
}
