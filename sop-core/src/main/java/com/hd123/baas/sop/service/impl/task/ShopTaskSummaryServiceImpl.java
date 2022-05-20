package com.hd123.baas.sop.service.impl.task;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.hd123.baas.sop.annotation.Tx;
import com.hd123.baas.sop.service.api.task.*;
import com.hd123.baas.sop.service.dao.task.ShopTaskLogDaoBof;
import com.hd123.baas.sop.service.dao.task.ShopTaskSummaryDaoBof;
import com.hd123.rumba.commons.biz.query.QueryDefinition;
import com.hd123.rumba.commons.biz.query.QueryResult;
import com.qianfan123.baas.common.BaasException;

/**
 * @Author maodapeng
 * @Since
 */
@Service
public class ShopTaskSummaryServiceImpl implements ShopTaskSummaryService {
  @Autowired
  private ShopTaskSummaryDaoBof shopTaskSummaryDao;
  @Autowired
  private ShopTaskService shopTaskService;
  @Autowired
  private ShopTaskLogDaoBof shopTaskLogDao;

  @Override
  public QueryResult<PlanSummary> queryPlanSummary(String tenant, QueryDefinition qd) {
    QueryResult<PlanSummary> query = shopTaskSummaryDao.queryPlanSummary(tenant, qd);
    if (CollectionUtils.isNotEmpty(query.getRecords())) {
      // 去重
      final Map<String, PlanSummary> map = new HashMap<>();
      List<PlanSummary> planSummaries = query.getRecords()
          .stream()
          .filter(p -> map.put(p.getPlan() + p.getPeriodCode(), p) == null)
          .collect(Collectors.toList());
      for (PlanSummary plan : planSummaries) {
        List<ShopTaskSummary> list = shopTaskSummaryDao.list(tenant, plan.getPlan(), plan.getPeriodCode());
        plan.setPlanTasks(list);
      }
    }
    return query;
  }

  @Override
  public PlanSummary getPlanSummary(String tenant, String plan, String periodCode) {
    PlanSummary planSummary = shopTaskSummaryDao.getPlanSummary(tenant, plan, periodCode);
    if (planSummary != null) {
      List<ShopTaskSummary> list = shopTaskSummaryDao.list(tenant, planSummary.getPlan(), planSummary.getPeriodCode());
      planSummary.setPlanTasks(list);
    }
    return planSummary;
  }

  // 门店任务汇总列表查询
  public QueryResult<ShopTaskSummary> query(String tenant, QueryDefinition qd) {
    QueryResult<ShopTaskSummary> query = shopTaskSummaryDao.query(tenant, qd);
    if (CollectionUtils.isNotEmpty(query.getRecords())) {
      for (ShopTaskSummary record : query.getRecords()) {
        fetchPart(tenant, record);
      }
    }
    return query;
  }

  @Override
  public QueryResult<ShopTaskSummary> queryByLoginId(String tenant, String loginId, QueryDefinition qd) {
    QueryResult<ShopTaskSummary> query = shopTaskSummaryDao.query(tenant, qd);
    if (CollectionUtils.isNotEmpty(query.getRecords())) {
      for (ShopTaskSummary record : query.getRecords()) {
        fetchPartByLoginId(tenant, loginId, record);
      }
    }
    return query;
  }

  public ShopTaskSummary get(String tenant, String uuid) {
    ShopTaskSummary shopTaskSummary = shopTaskSummaryDao.get(tenant, uuid);
    fetchPart(tenant, shopTaskSummary);
    return shopTaskSummary;
  }

  // 更新排名
  @Tx
  public void summary(String tenant, String uuid) throws BaasException {
    ShopTaskSummary shopTaskSummary = shopTaskSummaryDao.get(tenant, uuid);
    if (shopTaskSummary == null) {
      throw new BaasException("查询门店统计为空");
    }
    List<ShopTask> tasks = shopTaskService.list(tenant, uuid);
    if (CollectionUtils.isEmpty(tasks)) {
      throw new BaasException("没有门店任务");
    }
    BigDecimal shopScores = tasks.stream().map(ShopTask::getScore).reduce(BigDecimal.ZERO, BigDecimal::add);
    shopTaskSummaryDao.updateScore(tenant, uuid, shopScores);

    updateRank(tenant, shopTaskSummary.getPlan(), shopTaskSummary.getPlanPeriodCode());

    boolean allFinish = tasks.stream().allMatch(s -> ShopTaskState.FINISHED == s.getState());
    if (allFinish) {
      shopTaskSummaryDao.finish(tenant, uuid);
    }
  }

  @Override
  public List<PlanSummary> listExpirePlan(String tenant) {
    return shopTaskSummaryDao.listBeforeEndTime(tenant, ShopTaskState.UNFINISHED.name(), new Date());
  }

  @Override
  public QueryResult<ShopTaskSummary> mobileQuery(String tenant, QueryDefinition qd) {
    return shopTaskSummaryDao.mobileQuery(tenant, qd);
  }

  @Override
  public ShopTaskSummary mobileGet(String tenant, String shopSummaryId) {
    return shopTaskSummaryDao.mobileGet(tenant, shopSummaryId);
  }

  @Tx
  public void updateRank(String tenant, String plan, String periodCode) {
    List<ShopTaskSummary> shopTaskSummaries = shopTaskSummaryDao.list(tenant, plan, periodCode);
    if (CollectionUtils.isNotEmpty(shopTaskSummaries)) {
      for (int i = 0; i < shopTaskSummaries.size(); i++) {
        ShopTaskSummary task = shopTaskSummaries.get(i);
        task.setRank(new BigDecimal(i + 1));
      }
      shopTaskSummaryDao.updateRank(tenant, shopTaskSummaries);
    }
  }

  private void fetchPart(String tenant, ShopTaskSummary summary) {
    if (summary == null) {
      return;
    }
    List<ShopTask> list = shopTaskService.list(tenant, summary.getUuid());
    summary.setTasks(list);
    if (CollectionUtils.isNotEmpty(list)) {
      List<String> owners = list.stream().map(ShopTask::getUuid).collect(Collectors.toList());
      int finishedCount = shopTaskLogDao.count(tenant, owners, ShopTaskState.FINISHED.name());
      int total = shopTaskLogDao.count(tenant, owners);
      summary.setFinishedLogCount(finishedCount);
      summary.setLogTotalCount(total);
    }
  }

  private void fetchPartByLoginId(String tenant, String loginId, ShopTaskSummary summary) {
    if (summary == null) {
      return;
    }
    List<ShopTask> list = shopTaskService.listByLoginId(tenant, summary.getUuid(), loginId);
    summary.setTasks(list);
    if (CollectionUtils.isNotEmpty(list)) {
      List<String> owners = list.stream().map(ShopTask::getUuid).collect(Collectors.toList());
      int finishedCount = shopTaskLogDao.countByLoginId(tenant, owners, ShopTaskState.FINISHED.name(), loginId);
      int total = shopTaskLogDao.countByLoginId(tenant, owners, loginId);
      summary.setFinishedLogCount(finishedCount);
      summary.setLogTotalCount(total);
    }
  }
}
