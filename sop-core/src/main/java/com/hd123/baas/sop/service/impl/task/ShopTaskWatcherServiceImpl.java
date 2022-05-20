package com.hd123.baas.sop.service.impl.task;

import com.hd123.baas.sop.annotation.Tx;
import com.hd123.baas.sop.service.api.task.*;
import com.hd123.baas.sop.service.dao.task.ShopTaskWatcherDaoBof;
import com.hd123.rumba.commons.lang.Assert;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @Author guyahui
 * @Since
 */
@Service
@Slf4j
public class ShopTaskWatcherServiceImpl implements ShopTaskWatcherService {

  @Autowired
  private ShopTaskWatcherDaoBof shopTaskWatcherDao;
  @Autowired
  private ShopTaskService shopTaskService;

  @Override
  public List<ShopTask> query(String tenant, String operatorId, String keyword, List<String> shopTaskStateList, Integer pageStart, Integer pageSize) {
    Assert.hasText(tenant, "tenant");
    Assert.hasText(operatorId, "operatorId");
    Assert.notEmpty(shopTaskStateList, "shopTaskStateList");
    Assert.notNull(pageStart, "pageStart");
    Assert.notNull(pageSize, "pageSize");

    return shopTaskWatcherDao.query(tenant, operatorId,keyword, shopTaskStateList, pageStart, pageSize);
  }

  @Override
  public long count(String tenant, String operatorId, String keyword, List<String> shopTaskStateList) {
    Assert.hasText(tenant, "tenant");
    Assert.hasText(operatorId, "operatorId");
    Assert.notEmpty(shopTaskStateList, "shopTaskStateList");

    return shopTaskWatcherDao.count(tenant, operatorId,keyword, shopTaskStateList);
  }

  @Override
  @Tx
  public void batchSave(String tenant, List<ShopTaskWatcher> shopTaskWatcherList) {
    Assert.hasText(tenant, "tenant");
    Assert.notEmpty(shopTaskWatcherList, "shopTaskWatcherList");

    shopTaskWatcherDao.batchSave(tenant, shopTaskWatcherList);
  }

  @Override
  @Tx
  public void saveNew(String tenant, ShopTaskWatcher shopTaskWatcher) {
    Assert.hasText(tenant, "tenant");
    Assert.notNull(shopTaskWatcher, "shopTaskWatcher");

    shopTaskWatcherDao.saveNew(tenant, shopTaskWatcher);
  }

  @Override
  public List<ShopTaskLog> listShopTaskLogByShopTaskId(String tenant, String shopTaskId, String operatorId) {
    Assert.hasText(tenant, "tenant");
    Assert.hasText(shopTaskId, "shopTaskId");
    Assert.hasText(operatorId, "operatorId");

    List<ShopTaskLog> shopTaskLogList = new ArrayList<>();

    List<String> shopTaskLogIdList = shopTaskWatcherDao.listShopTaskIdByWatcher(tenant, operatorId);
    if (CollectionUtils.isEmpty(shopTaskLogIdList)) {
      log.warn("该操作人还没有关注任何任务");
      return shopTaskLogList;
    }
    List<ShopTaskLog> shopTaskLogs = shopTaskService.listByOwners(tenant, Collections.singletonList(shopTaskId));
    if (CollectionUtils.isEmpty(shopTaskLogs)) {
      log.warn("该shopTaskId：{}没有对应的shopTaskLog列表", shopTaskId);
      return shopTaskLogList;
    }
    // 只返回我关注的shopTaskLog详情
    List<ShopTaskLog> result = shopTaskLogs.stream().filter(shopTaskLog ->
        shopTaskLogIdList.stream().anyMatch(shopTaskLogId ->
            StringUtils.isNotEmpty(shopTaskLogId) && shopTaskLogId.equals(shopTaskLog.getUuid()))).collect(Collectors.toList());
    return result;
  }

  @Override
  public ShopTaskWatcher getByWatcherAndShopTaskLogId(String tenant, String watcher, String shopTaskId) {
    Assert.hasText(tenant, "tenant");
    Assert.hasText(watcher, "watcher");
    Assert.hasText(shopTaskId, "shopTaskId");

    return shopTaskWatcherDao.getByWatcherAndShopTaskId(tenant, watcher, shopTaskId);
  }

  @Override
  public List<ShopTaskWatcher> listByWatcherAndShopTaskLogIdList(String tenant, String watcher, List<String> shopTaskIdList) {
    Assert.hasText(tenant, "tenant");
    Assert.hasText(watcher, "watcher");
    Assert.notEmpty(shopTaskIdList, "shopTaskIdList");

    return shopTaskWatcherDao.listByWatcherAndShopTaskIdList(tenant, watcher, shopTaskIdList);
  }

}
