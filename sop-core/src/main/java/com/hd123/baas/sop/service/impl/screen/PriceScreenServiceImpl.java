package com.hd123.baas.sop.service.impl.screen;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.hd123.baas.sop.annotation.Tx;
import com.hd123.baas.sop.service.api.screen.PriceScreen;
import com.hd123.baas.sop.service.api.screen.PriceScreenService;
import com.hd123.baas.sop.service.api.screen.PriceScreenShop;
import com.hd123.baas.sop.service.api.screen.PriceScreenState;
import com.hd123.baas.sop.service.dao.screen.PriceScreenDaoBof;
import com.hd123.baas.sop.service.dao.screen.PriceScreenShopDaoBof;
import com.hd123.baas.sop.evcall.EvCallEventPublisher;
import com.hd123.baas.sop.evcall.exector.pricescreen.PriceScreenTerminateEvCallExecutor;
import com.hd123.baas.sop.evcall.exector.pricescreen.PriceScreenTerminateMsg;
import com.hd123.rumba.commons.biz.entity.OperateInfo;
import com.hd123.rumba.commons.biz.query.QueryDefinition;
import com.hd123.rumba.commons.biz.query.QueryResult;
import com.hd123.rumba.commons.lang.Assert;
import com.qianfan123.baas.common.BaasException;

/**
 * (PriceScreen)表服务实现类
 *
 * @author liuhaoxin
 * @since 2021-08-09 11:39:30
 */
@Service
public class PriceScreenServiceImpl implements PriceScreenService {
  @Autowired
  private PriceScreenDaoBof priceScreenDao;

  @Autowired
  private PriceScreenShopDaoBof priceScreenShopDao;

  @Autowired
  private EvCallEventPublisher publisher;

  @Override
  @Tx
  public String saveNew(String tenant, PriceScreen priceScreen, OperateInfo operateInfo) throws BaasException {
    Assert.hasText(tenant, "tenant");
    Assert.notNull(priceScreen, "priceScreen");

    if (priceScreen.getEffectiveStartTime().after(priceScreen.getEffectiveEndTime())) {
      throw new BaasException("开始时间不能小于结束时间!");
    }
    // 开始时间大于当前时间
    if (!new Date().before(priceScreen.getEffectiveStartTime())) {
      throw new BaasException("开始时间不是未来时间！");
    }
    if (!priceScreen.getAllShops() && CollectionUtils.isEmpty(priceScreen.getShops())){
      throw new BaasException("门店不能为空");
    }
    String uuid = UUID.randomUUID().toString();
    priceScreen.setUuid(uuid);
    priceScreenDao.saveNew(tenant, priceScreen, operateInfo);

    // 增加加个屏和门店关联关系
    relationShops(tenant, uuid, priceScreen.getAllShops(), priceScreen.getShops());
    return uuid;
  }

  private void relationShops(String tenant, String uuid, Boolean allShops, List<PriceScreenShop> shops) {
    if (!allShops) {
      List<PriceScreenShop> priceScreenShops = new ArrayList<>(shops.size());
      for (PriceScreenShop shop : shops) {
        shop.setUuid(UUID.randomUUID().toString());
        shop.setOwner(uuid);
        priceScreenShops.add(shop);
      }
      priceScreenShopDao.batchSave(tenant, priceScreenShops);
    }
  }

  @Override
  @Tx
  public void deleted(String tenant, String uuid, OperateInfo operateInfo) throws BaasException {
    Assert.hasText(tenant, "tenant");
    Assert.hasText(uuid, "uuid");

    PriceScreen priceScreen = priceScreenDao.get(tenant, uuid);
    if (Objects.isNull(priceScreen)) {
      return;
    }
    if (!PriceScreenState.CONFIRMED.equals(priceScreen.getState())) {
      throw new BaasException("价格屏内容状态不是待生效状态，无法删除，请重新刷新！");
    }

    priceScreenDao.deleted(tenant, uuid, operateInfo);
  }

  @Override
  @Tx
  public void saveModify(String tenant, PriceScreen priceScreen, OperateInfo operateInfo) throws BaasException {
    Assert.hasText(tenant, "tenant");
    Assert.notNull(priceScreen, "priceScreen");

    PriceScreen newPriceScreen = priceScreenDao.get(tenant, priceScreen.getUuid());
    if (Objects.isNull(newPriceScreen)) {
      throw new BaasException("价格屏内容不存在！");
    }
    if (!PriceScreenState.CONFIRMED.equals(newPriceScreen.getState())) {
      throw new BaasException("价格屏状态不是待生效，无法编辑！");
    }
    if (!priceScreen.getAllShops() && CollectionUtils.isEmpty(priceScreen.getShops())) {
      throw new BaasException("门店不能为空");
    }
    if (priceScreen.getEffectiveStartTime().after(priceScreen.getEffectiveEndTime())) {
      throw new BaasException("开始时间不能大于结束时间!");
    }
    priceScreen.setOrgId(newPriceScreen.getOrgId());
    priceScreen.setCreateInfo(newPriceScreen.getCreateInfo());
    priceScreenDao.update(tenant, priceScreen, operateInfo);
    priceScreenShopDao.removeByOwner(tenant, priceScreen.getUuid());
    relationShops(tenant, priceScreen.getUuid(), priceScreen.getAllShops(), priceScreen.getShops());
  }

  @Override
  @Tx
  public void terminate(String tenant, String uuid, OperateInfo operateInfo) throws BaasException {
    Assert.hasText(tenant, "tenant");
    Assert.hasText(uuid, "uuid");

    PriceScreen priceScreen = priceScreenDao.get(tenant, uuid);
    if (Objects.isNull(priceScreen)) {
      throw new BaasException("不存在价格屏信息");
    }
    if (PriceScreenState.TERMINATED.equals(priceScreen.getState())) {
      return;
    }
    if (!PriceScreenState.PUBLISHED.equals(priceScreen.getState())) {
      throw new BaasException("该状态无法终止,请重新刷新");
    }
    priceScreenDao.terminate(tenant, uuid, operateInfo);
    PriceScreenTerminateMsg msg = new PriceScreenTerminateMsg();
    msg.setTenant(tenant);
    msg.setUuid(uuid);
    publisher.publishForNormal(PriceScreenTerminateEvCallExecutor.PRICE_SCREEN_TERMINATE_EXECUTOR_ID, msg);

  }

  @Override
  @Tx
  public void effect(String tenant, Date date, OperateInfo operateInfo) {
    Assert.hasText(tenant, "tenant");
    Assert.notNull(date, "date");

    priceScreenDao.effectByDate(tenant, date, operateInfo);
  }

  @Override
  @Tx
  public void expire(String tenant, Date date, OperateInfo operateInfo) {
    Assert.hasText(tenant, "tenant");
    Assert.notNull(date, "date");

    priceScreenDao.expireByDate(tenant, date, operateInfo);
  }

  @Override
  public PriceScreen get(String tenant, String uuid) {
    Assert.hasText(tenant, "tenant");
    Assert.notNull(uuid, "uuid");

    PriceScreen priceScreen = priceScreenDao.get(tenant, uuid);
    if (!priceScreen.getAllShops()) {
      List<PriceScreenShop> priceScreenShops = priceScreenShopDao.listByOwner(tenant, uuid);
      priceScreen.setShops(priceScreenShops);
    }
    return priceScreen;
  }

  @Override
  public QueryResult<PriceScreen> query(String tenant, QueryDefinition qd) {
    Assert.hasText(tenant, "tenant");
    Assert.notNull(qd, "qd");

    QueryResult<PriceScreen> result = priceScreenDao.query(tenant, qd);
    List<PriceScreen> priceScreens = result.getRecords();

    fetchParts(tenant, priceScreens);

    result.setRecords(priceScreens);
    return result;
  }

  private void fetchParts(String tenant, List<PriceScreen> priceScreens) {
    if (CollectionUtils.isEmpty(priceScreens)) {
      return;
    }
    List<String> uuids = priceScreens.stream().map(PriceScreen::getUuid).collect(Collectors.toList());
    List<PriceScreenShop> priceScreenShops = priceScreenShopDao.listByOwners(tenant, uuids);
    if (CollectionUtils.isEmpty(priceScreenShops)) {
      return;
    }
    Map<String, List<PriceScreenShop>> priceScreenShopMap = priceScreenShops.stream()
        .collect(Collectors.groupingBy(PriceScreenShop::getOwner));

    for (PriceScreen priceScreen : priceScreens) {
      List<PriceScreenShop> shops = priceScreenShopMap.get(priceScreen.getUuid());
      priceScreen.setShops(shops);
    }
  }

}
