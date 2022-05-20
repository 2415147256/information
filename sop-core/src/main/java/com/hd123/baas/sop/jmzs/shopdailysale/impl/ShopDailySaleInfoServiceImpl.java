package com.hd123.baas.sop.jmzs.shopdailysale.impl;

import com.hd123.baas.sop.annotation.Tx;
import com.hd123.baas.sop.jmzs.shopdailysale.api.ShopDailySaleInfo;
import com.hd123.baas.sop.jmzs.shopdailysale.api.ShopDailySaleInfoLine;
import com.hd123.baas.sop.jmzs.shopdailysale.api.ShopDailySaleInfoService;
import com.hd123.baas.sop.jmzs.shopdailysale.api.ShopDailySaleInfoState;
import com.hd123.baas.sop.jmzs.shopdailysale.dao.ShopDailySaleInfoDao;
import com.hd123.rumba.commons.biz.entity.OperateInfo;
import com.hd123.rumba.commons.biz.entity.StandardEntity;
import com.hd123.rumba.commons.biz.query.QueryDefinition;
import com.hd123.rumba.commons.biz.query.QueryResult;
import com.hd123.rumba.commons.lang.Assert;
import com.qianfan123.baas.common.BaasException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class ShopDailySaleInfoServiceImpl  implements ShopDailySaleInfoService {
  @Autowired
  private ShopDailySaleInfoDao dao;


  @Override
  public QueryResult<ShopDailySaleInfo> query(String tenant, QueryDefinition qd) {
    Assert.notNull(tenant);
    Assert.notNull(qd);

    return dao.query(tenant, qd);
  }

  @Override
  public ShopDailySaleInfo get(String tenant, String uuid) {
    Assert.hasText(tenant);
    Assert.hasText(uuid);

    return dao.get(tenant, uuid);
  }

  @Override
  @Tx
  public String saveNew(String tenant, ShopDailySaleInfo saleInfo, OperateInfo operateInfo) throws Exception {
    Assert.hasText(tenant);
    Assert.notNull(saleInfo);
    Assert.notEmpty(saleInfo.getLines());
    Assert.notNull(operateInfo);

    saleInfo.setTenant(tenant);
    saleInfo.setState(ShopDailySaleInfoState.INIT);
    buildForSaveNew(saleInfo, operateInfo);
    setAmount(saleInfo);
    dao.saveNew(saleInfo);

    return saleInfo.getUuid();
  }



  @Override
  @Tx
  public void submit(String tenant, String uuid, OperateInfo operateInfo) throws Exception {
    Assert.hasText(tenant);
    Assert.hasText(uuid);
    Assert.notNull(operateInfo);

    ShopDailySaleInfo shopDailySaleInfo = dao.get(tenant, uuid, true);
    if (shopDailySaleInfo == null) {
      throw new BaasException("提交门店盈亏帐失败,找不到源数据{}", uuid);
    }

    if (shopDailySaleInfo.getState() != ShopDailySaleInfoState.INIT) {
      throw new BaasException("提交门店盈亏帐错误,状态{}", shopDailySaleInfo.getState());
    }

    shopDailySaleInfo.setState(ShopDailySaleInfoState.SUBMITTED);
    buildForUpdate(shopDailySaleInfo, operateInfo);
    dao.update(shopDailySaleInfo);

  }

  @Override
  @Tx
  public void saveModify(String tenant, ShopDailySaleInfo shopDailySaleInfo, OperateInfo operateInfo) throws Exception {
    Assert.hasText(tenant);
    Assert.notNull(shopDailySaleInfo);
    Assert.hasText(shopDailySaleInfo.getUuid());
    Assert.notNull(operateInfo);

    ShopDailySaleInfo record = dao.get(tenant, shopDailySaleInfo.getUuid(), true);
    if (record == null) {
      throw new BaasException("编辑失败,找不到源数据{}", shopDailySaleInfo.getUuid());
    }

    if (record.getState() != ShopDailySaleInfoState.INIT) {
      throw new BaasException("当前状态不允许编辑");
    }

    convert(record, shopDailySaleInfo);
    buildForUpdate(record, operateInfo);
    dao.update(record);

  }

  private void convert(ShopDailySaleInfo record, ShopDailySaleInfo shopDailySaleInfo) {
    record.setHolder(shopDailySaleInfo.getHolder());
    record.setHolderId(shopDailySaleInfo.getHolderId());
    record.setHolderCode(shopDailySaleInfo.getHolderCode());
    record.setHolderName(shopDailySaleInfo.getHolderName());
    record.setShopId(shopDailySaleInfo.getShopId());
    record.setShopCode(shopDailySaleInfo.getShopCode());
    record.setShopName(shopDailySaleInfo.getShopName());
    record.setLines(shopDailySaleInfo.getLines());
    setAmount(record);

  }

  private void setAmount(ShopDailySaleInfo saleInfo) {
    saleInfo.setAmount(saleInfo.getLines().stream()
        .map(i -> i.getDetails().stream()
            .map(ShopDailySaleInfoLine.ShopDailySaleInfoLineDetail::getAmount)
            .reduce(BigDecimal::add).orElse(BigDecimal.ZERO))
        .reduce(BigDecimal::add)
        .orElse(BigDecimal.ZERO));
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
