package com.hd123.baas.sop.service.impl.task;

import com.hd123.baas.config.core.BaasConfigClient;
import com.hd123.baas.sop.annotation.Tx;
import com.hd123.baas.sop.config.DailyTaskConfig;
import com.hd123.baas.sop.service.api.sysconfig.SysConfig;
import com.hd123.baas.sop.service.api.sysconfig.SysConfigService;
import com.hd123.baas.sop.service.api.task.*;
import com.hd123.baas.sop.service.api.taskgroup.TaskGroupType;
import com.hd123.baas.sop.service.dao.task.ShopTaskDaoBof;
import com.hd123.baas.sop.service.dao.task.ShopTaskGroupDaoBof;
import com.hd123.baas.sop.configuration.FeignClientMgr;
import com.hd123.baas.sop.remote.rsaccount.RsAccountClient;
import com.hd123.baas.sop.remote.rsaccount.store.RSDailyClear;
import com.hd123.baas.sop.utils.SopUtils;
import com.hd123.rumba.commons.biz.entity.OperateInfo;
import com.hd123.rumba.commons.biz.query.QueryDefinition;
import com.hd123.rumba.commons.biz.query.QueryResult;
import com.hd123.rumba.commons.lang.Assert;
import com.qianfan123.baas.common.BaasException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
@Slf4j
public class ShopTaskGroupServiceImpl implements ShopTaskGroupService {

  @Autowired
  private ShopTaskGroupDaoBof dao;

  @Autowired
  private ShopTaskDaoBof shopTaskDao;

  @Autowired
  private FeignClientMgr feignClientMgr;

  @Autowired
  private BaasConfigClient baasConfigClient;

  @Autowired
  private SysConfigService sysConfigService;

  @Override
  public QueryResult<ShopTaskGroup> query(String tenant, QueryDefinition qd) {
    Assert.notNull(tenant, "租户");
    Assert.notNull(qd, "查询条件");
    return dao.query(tenant, qd);
  }

  @Override
  @Tx
  public void finish(String tenant, String uuid, String appId, OperateInfo operateInfo) throws BaasException {
    Assert.notNull(tenant, "租户");
    Assert.notNull(uuid, "uuid");
    ShopTaskGroup shopTaskGroup = dao.get(tenant, uuid);
    if (shopTaskGroup == null) {
      throw new BaasException("门店任务组不存在");
    }
    if (shopTaskGroup.getState() != ShopTaskGroupState.UNFINISHED) {
      throw new BaasException("任务组<{0}>当前状态为：{1},无法完成日结任务组", shopTaskGroup.getGroupName(), shopTaskGroup.getState());
    }
    List<ShopTask> shopTasks = shopTaskDao.getByShopTaskGroupId(tenant, uuid);
    ShopTask shopTask = shopTasks.stream()
        .filter(i -> !i.getState().name().equals(ShopTaskState.FINISHED.name()))
        .findAny()
        .orElse(null);
    if (shopTask != null) {
      throw new BaasException("该门店任务组下存在未完成任务");
    }

    Date time = shopTaskGroup.getEarliestFinishTime();
    if (time != null && time.getTime() > System.currentTimeMillis()) {
      throw new BaasException("尚未到任务组完成时间");
    }
    dao.finish(tenant, uuid, appId, operateInfo);
    dailyClear(shopTaskGroup);
  }

  @Override
  public DailyTaskFinishCheck finishCheck(String tenant, String shopId) throws BaasException {
    Assert.notNull(tenant, "tenant");
    Assert.notNull(shopId, "shopId");
    DailyTaskFinishCheck dailyTaskFinishCheck = new DailyTaskFinishCheck();
    Date preDate = DateUtils.addDays(new Date(), -1);
    ShopTaskGroup record = dao.getByShop(tenant, shopId, TaskGroupType.DAILY, preDate);
    if (record == null) {
      throw new BaasException("日结任务不存在");
    }
    dailyTaskFinishCheck.setEarliestFinishTime(record.getEarliestFinishTime());
    if (record.getState() != null && ShopTaskGroupState.FINISHED.name().equals(record.getState().name())) {
      dailyTaskFinishCheck.setIsFinish(true);
    } else {
      SysConfig config = sysConfigService.get(tenant, DailyTaskConfig.TIPS);
      if(config != null) {
        dailyTaskFinishCheck.setTips(config.getCfValue());
      }else {
        log.info("日结提示语未配置");
      }
      dailyTaskFinishCheck.setIsFinish(false);
    }
    return dailyTaskFinishCheck;
  }

  private void dailyClear(ShopTaskGroup shopTaskGroup) throws BaasException {
    if (shopTaskGroup == null || TaskGroupType.DAILY != shopTaskGroup.getType()) {
      log.info("当前任务类型不是日结任务，忽略调用日清接口。");
      return;
    }
    // 根据配置是否进行日清
    DailyTaskConfig config = baasConfigClient.getConfig(shopTaskGroup.getTenant(), DailyTaskConfig.class,
        shopTaskGroup.getShop());
    if (!config.isEnableDailyClear()) {
      log.info("当前门店{}没有开启日清功能", shopTaskGroup.getShop());
      return;
    }
    RSDailyClear rsDailyClear = new RSDailyClear();
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
    String date = sdf.format(shopTaskGroup.getPlanTime());
    rsDailyClear.setClearDate(date);
    rsDailyClear.setSourceAction("sop");
    String shopCode = shopTaskGroup.getShopCode();
    List<String> list = new ArrayList<>();
    list.add(shopCode);
    rsDailyClear.setStores(list);
    try {
      log.info("准备调用日清接口，门店={}", shopTaskGroup.getShop());
      RsAccountClient client = feignClientMgr.getClient(shopTaskGroup.getTenant(), shopTaskGroup.getShop(),
          RsAccountClient.class);
      client.dailyclear(rsDailyClear);
    } catch (Exception e) {
      log.error("调取门店日清接口失败:", e);
      throw new BaasException("调取门店日清接口失败");
    }
  }

  @Override
  public ShopTaskGroup get(String tenant, String uuid) {
    Assert.notNull(tenant, "租户");
    Assert.notNull(uuid, "uuid");
    return dao.get(tenant, uuid);
  }

  @Override
  @Tx
  public void saveNew(String tenant, ShopTaskGroup shopTaskGroup) {
    Assert.notNull(tenant, "租户");
    Assert.notNull(shopTaskGroup, "门店任务组");
    dao.insert(tenant, shopTaskGroup);
  }

  @Override
  public ShopTaskGroup getByShopAndGroupIdAndPlanDate(String tenant, String shop, String groupId, Date planTime) {
    Assert.notNull(tenant, "租户");
    Assert.notNull(groupId, "任务组id");
    Assert.notNull(planTime, "计划日期");

    return dao.getByShopAndGroupIdAndPlanDate(tenant, shop, groupId, planTime);
  }

  @Override
  @Tx
  public String checkLast(String tenant, String shop, String groupId) {

    Assert.notNull(tenant, "租户");
    Assert.notNull(groupId, "任务组");
    ShopTaskGroup shopTaskGroup = dao.getLast(tenant, shop, groupId);
    if (shopTaskGroup == null) {
      return null;
    }
    if (shopTaskGroup.getState().equals(ShopTaskGroupState.FINISHED)) {
      return shopTaskGroup.getUuid();
    }
    List<ShopTask> shopTasks = shopTaskDao.getByShopTaskGroupId(tenant, shopTaskGroup.getUuid());
    ShopTask shopTask = shopTasks.stream()
        .filter(i -> !i.getState().equals(ShopTaskState.FINISHED))
        .findAny()
        .orElse(null);
    if (shopTask != null) {
      dao.setState(tenant, shopTaskGroup.getUuid(), ShopTaskState.EXPIRED.name(), SopUtils.getSysOperateInfo());
    } else {
      dao.finish(tenant, shopTaskGroup.getUuid(), SopUtils.getSysOperateInfo().getOperator().getId(),
          SopUtils.getSysOperateInfo());
    }
    return shopTaskGroup.getUuid();
  }

  @Override
  @Tx
  public void modifyEarliestFinishTime(String tenant, String uuid, Date earliestFinishTime) {
    Assert.notNull(tenant, "租户");
    Assert.notNull(uuid, "uuid");
    Assert.notNull(earliestFinishTime, "最后完成时间");

    dao.updateEarliestFinishTime(tenant, uuid, earliestFinishTime);
  }

  @Override
  public List<ShopTaskGroup> listByShopAndPlanTimeAndState(String tenant, String shop, Date date,
      ShopTaskGroupState state) {
    Assert.notNull(tenant, "租户");
    Assert.notNull(shop, "门店");
    Assert.notNull(date, "日期");
    Assert.notNull(state, "状态");
    return dao.listByShopAndPlanTimeAndState(tenant, shop, date, state);
  }

}
