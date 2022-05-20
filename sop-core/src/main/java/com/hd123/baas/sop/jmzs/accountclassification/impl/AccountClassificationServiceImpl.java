package com.hd123.baas.sop.jmzs.accountclassification.impl;

import com.hd123.baas.sop.annotation.Tx;
import com.hd123.baas.sop.jmzs.accountclassification.api.AccountClassification;
import com.hd123.baas.sop.jmzs.accountclassification.api.AccountClassificationService;
import com.hd123.baas.sop.jmzs.accountclassification.api.AccountClassificationState;
import com.hd123.baas.sop.jmzs.accountclassification.dao.AccountClassificationDao;
import com.hd123.rumba.commons.biz.entity.OperateInfo;
import com.hd123.rumba.commons.biz.entity.StandardEntity;
import com.hd123.rumba.commons.biz.query.QueryDefinition;
import com.hd123.rumba.commons.biz.query.QueryResult;
import com.hd123.rumba.commons.lang.Assert;
import com.qianfan123.baas.common.BaasException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AccountClassificationServiceImpl implements AccountClassificationService {

  @Autowired
  private AccountClassificationDao dao;

  @Override
  public QueryResult<AccountClassification> query(String tenant, QueryDefinition qd) {
    Assert.notNull(tenant);
    Assert.notNull(qd);

    return dao.query(tenant, qd);
  }

  @Override
  public AccountClassification get(String tenant, String uuid) {
    Assert.hasText(tenant);
    Assert.hasText(uuid);

    return dao.get(tenant, uuid);
  }

  @Override
  @Tx
  public String saveNew(String tenant, AccountClassification account, OperateInfo operateInfo) throws Exception {
    Assert.hasText(tenant);
    Assert.notNull(account);
    Assert.notNull(operateInfo);

    AccountClassification record = dao.getByName(tenant, account.getOrgId(), account.getName());
    if (record != null) {
      throw new BaasException("该名称已存在,请修改后重试");
    }

    account.setTenant(tenant);
    account.setState(AccountClassificationState.DISABLE);
    buildForSaveNew(account, operateInfo);

    dao.saveNew(account);

    return account.getUuid();
  }

  @Override
  @Tx
  public void saveModify(String tenant, AccountClassification account, OperateInfo operateInfo) throws Exception {
    Assert.hasText(tenant);
    Assert.notNull(account);
    Assert.notNull(operateInfo);
    Assert.hasText(account.getUuid());

    AccountClassification accountByName = dao.getByName(tenant, account.getOrgId(), account.getName());
    if (accountByName != null) {
      throw new BaasException("该名称已存在,请修改后重试");
    }

    AccountClassification record = dao.get(tenant, account.getUuid(), true);
    if (record == null) {
      throw new BaasException("找不到待修改的数据");
    }

    if (record.getState() != AccountClassificationState.DISABLE) {
      throw new BaasException("该收支科目已被启用不允许编辑");
    }

    record.setName(account.getName());
    record.setType(account.getType());
    buildForUpdate(record, operateInfo);

    dao.update(record);

  }

  @Override
  @Tx
  public void deleteByUuid(String tenant, String uuid) throws BaasException {
    Assert.hasText(tenant);
    Assert.hasText(uuid);

    AccountClassification record = dao.get(tenant, uuid, true);
    if (record == null) {
      throw new BaasException("数据错误");
    }

    if (record.getState() != AccountClassificationState.DISABLE) {
      throw new BaasException("该收支科目已被启用不允许删除");
    }

    dao.delete(tenant, uuid);

  }

  @Override
  @Tx
  public void enable(String tenant, String uuid, OperateInfo operateInfo) throws Exception {
    Assert.hasText(tenant);
    Assert.hasText(uuid);
    Assert.notNull(operateInfo);

    AccountClassification record = dao.get(tenant, uuid, true);

    if (record == null) {
      throw new BaasException("数据错误");
    }

    record.setState(AccountClassificationState.ENABLE);
    buildForUpdate(record, operateInfo);
    dao.update(record);
  }

  @Override
  @Tx
  public void disable(String tenant, String uuid, OperateInfo operateInfo) throws Exception {
    Assert.hasText(tenant);
    Assert.hasText(uuid);
    Assert.notNull(operateInfo);

    AccountClassification record = dao.get(tenant, uuid, true);

    if (record == null) {
      throw new BaasException("数据错误");
    }

    record.setState(AccountClassificationState.DISABLE);
    buildForUpdate(record, operateInfo);
    dao.update(record);

  }
  private void buildForUpdate(StandardEntity entity, OperateInfo operateInfo) {
    entity.setVersion(entity.getVersion() + 1);
    entity.setLastModifyInfo(operateInfo);
  }

  private void buildForSaveNew(StandardEntity entity, OperateInfo operateInfo) {
    entity.setVersion(0);
    entity.setCreateInfo(operateInfo);
    entity.setLastModifyInfo(operateInfo);
  }
}
