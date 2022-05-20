package com.hd123.baas.sop.service.impl.explosivev2.report;

import com.hd123.baas.sop.annotation.Tx;
import com.hd123.baas.sop.service.api.explosivev2.ExplosiveV2;
import com.hd123.baas.sop.service.api.explosivev2.ExplosiveV2Line;
import com.hd123.baas.sop.service.api.explosivev2.report.ExplosiveLogV2;
import com.hd123.baas.sop.service.api.explosivev2.report.ExplosiveLogV2Line;
import com.hd123.baas.sop.service.api.explosivev2.report.ExplosiveLogV2Type;
import com.hd123.baas.sop.service.api.explosivev2.report.ExplosiveReportSummary;
import com.hd123.baas.sop.service.api.explosivev2.report.ExplosiveReportV2Service;
import com.hd123.baas.sop.service.api.explosivev2.report.ExplosiveSignV2DailyReport;
import com.hd123.baas.sop.service.dao.explosivev2.report.ExplosiveLogV2DaoBof;
import com.hd123.baas.sop.service.dao.explosivev2.report.ExplosiveLogV2LineDaoBof;
import com.hd123.baas.sop.service.dao.explosivev2.report.ExplosiveSignV2DailyReportDaoBof;
import com.hd123.baas.sop.evcall.EvCallEventPublisher;
import com.hd123.baas.sop.evcall.exector.explosivev2.ExplosiveReportV2EvCallExecutor;
import com.hd123.baas.sop.evcall.exector.explosivev2.ExplosiveReportV2EvCallMsg;
import com.hd123.baas.sop.utils.IdGenUtils;
import com.hd123.rumba.commons.biz.entity.OperateInfo;
import com.hd123.rumba.commons.biz.query.Cop;
import com.hd123.rumba.commons.biz.query.QueryDefinition;
import com.hd123.rumba.commons.biz.query.QueryResult;
import com.hd123.rumba.commons.lang.Assert;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 爆品活动日志服务 实现
 *
 * @author liuhaoxin
 * @since 2021-11-24
 */
@Slf4j
@Service
public class ExplosiveReportV2ServiceImpl implements ExplosiveReportV2Service {

  @Autowired
  private ExplosiveLogV2DaoBof explosiveLogV2Dao;
  @Autowired
  private ExplosiveLogV2LineDaoBof explosiveLogV2LineDao;
  @Autowired
  private ExplosiveSignV2DailyReportDaoBof explosiveSignV2DailyReportDao;
  @Autowired
  private EvCallEventPublisher publisher;

  @Override
  @Tx
  public void saveNewLog(String tenant, ExplosiveLogV2 explosiveLog, OperateInfo operateInfo) {
    Assert.hasText(tenant, "tenant");
    Assert.notNull(explosiveLog, "explosiveLog");

    // 同步保存叫货单和配货单到活动报名日志
    explosiveLogV2Dao.insert(tenant, explosiveLog, operateInfo);
    explosiveLogV2LineDao.batchInsert(tenant, explosiveLog.getLines());
    // 同步活动报名报表
    pushDailyReport(tenant, explosiveLog.getUuid(), operateInfo);
  }

  @Override
  public ExplosiveLogV2 getLog(String tenant, String logId, String... fetchParts) {
    Assert.hasText(tenant, "tenant");
    Assert.notNull(logId, "logId");

    ExplosiveLogV2 explosiveLog = explosiveLogV2Dao.get(tenant, logId);
    logFetchParts(tenant, explosiveLog, fetchParts);
    return explosiveLog;
  }

  @Override
  @Tx
  public void batchSave(String tenant, List<ExplosiveSignV2DailyReport> items, OperateInfo operateInfo) {
    Assert.hasText(tenant, "tenant");
    Assert.notEmpty(items, "dailyReports");

    List<ExplosiveSignV2DailyReport> inserts = items.stream().filter(i -> StringUtils.isEmpty(i.getUuid())).collect(Collectors.toList());
    List<ExplosiveSignV2DailyReport> updates = items.stream().filter(i -> StringUtils.isNotEmpty(i.getUuid())).collect(Collectors.toList());

    if (CollectionUtils.isNotEmpty(inserts)) {
      for (ExplosiveSignV2DailyReport dailyReport : inserts) {
        dailyReport.setUuid(IdGenUtils.buildRdUuid());
      }
      explosiveSignV2DailyReportDao.batchInsert(tenant, inserts, operateInfo);
    }
    if (CollectionUtils.isNotEmpty(updates)) {
      explosiveSignV2DailyReportDao.batchUpdateQty(tenant, updates, operateInfo);
    }
  }

  @Override
  public QueryResult<ExplosiveSignV2DailyReport> query(String tenant, QueryDefinition qd) {
    Assert.hasText(tenant, "tenant");
    Assert.notNull(qd, "qd");

    return explosiveSignV2DailyReportDao.query(tenant, qd);
  }

  @Override
  public QueryResult<ExplosiveReportSummary> queryGroupByShopAndSkuId(String tenant, String orgId, QueryDefinition qd) {
    Assert.hasText(tenant, "tenant");
    Assert.notNull(qd, "qd");

    QueryResult<ExplosiveReportSummary> result = explosiveSignV2DailyReportDao.queryGroupByShop(tenant, qd);
    return result;
  }

  @Override
  public QueryResult<ExplosiveReportSummary> queryGroupByDate(String tenant, QueryDefinition qd) {
    Assert.hasText(tenant, "tenant");
    Assert.notNull(qd, "qd");

    QueryResult<ExplosiveReportSummary> result = explosiveSignV2DailyReportDao.queryGroupByDate(tenant, qd);
    return result;
  }

  @Override
  public void batchInsert(String tenant, List<ExplosiveSignV2DailyReport> dailyReports, OperateInfo operateInfo) {
    Assert.hasText(tenant, "tenant");
    Assert.notEmpty(dailyReports, "dailyReports");
    for (ExplosiveSignV2DailyReport dailyReport : dailyReports) {
      dailyReport.setUuid(IdGenUtils.buildRdUuid());
    }
    explosiveSignV2DailyReportDao.batchInsert(tenant, dailyReports, operateInfo);
  }

  @Override
  public void batchUpdate(String tenant, List<ExplosiveSignV2DailyReport> dailyReports, OperateInfo operateInfo) {
    Assert.hasText(tenant, "tenant");
    Assert.notEmpty(dailyReports, "dailyReports");
    explosiveSignV2DailyReportDao.batchUpdateQty(tenant, dailyReports, operateInfo);
  }

  @Override
  public void updateLog(String tenant, ExplosiveLogV2 explosiveLog, OperateInfo operateInfo) {
    Assert.hasText(tenant, "tenant");
    Assert.notNull(explosiveLog, "explosiveLog");
    ExplosiveLogV2 historyLog = explosiveLogV2Dao.getByExplosiveId(tenant, explosiveLog.getExplosiveId(), explosiveLog.getShop().getUuid(), ExplosiveLogV2Type.SIGN);
    explosiveLog.setUuid(historyLog.getUuid());
    // 同步保存叫货单和配货单到活动报名日志
    explosiveLogV2Dao.update(tenant, explosiveLog, operateInfo);
    for (ExplosiveLogV2Line line : explosiveLog.getLines()) {
      line.setOwner(historyLog.getUuid());
    }
    explosiveLogV2LineDao.batchUpdateQty(tenant, explosiveLog.getLines());
    // 同步活动报名报表
    pushDailyReport(tenant, explosiveLog.getUuid(), operateInfo);
  }

  @Tx
  @Override
  public void updateLimitQty(String tenant, ExplosiveV2 explosiveV2, List<ExplosiveV2Line> lines, OperateInfo operateInfo) {
    Assert.hasText(tenant, "tenant");
    Assert.notNull(explosiveV2, "explosiveV2");
    Assert.notEmpty(lines, "lines");

    List<ExplosiveV2Line> modifyLines = new ArrayList<>();
    for (ExplosiveV2Line line : lines) {
      ExplosiveV2Line modifyLine = explosiveV2.getLines()
          .stream()
          .filter(i -> line.getUuid().equals(i.getUuid()))
          .map(i -> {
            i.setLimitQty(line.getLimitQty());
            return i;
          })
          .findFirst().orElse(null);
      modifyLines.add(modifyLine);
    }
    Map<String, ExplosiveV2Line> mapModifyLines = modifyLines.stream().collect(Collectors.toMap(ExplosiveV2Line::getSkuId, i -> i));
    QueryDefinition qd = new QueryDefinition();
    qd.addByField(ExplosiveSignV2DailyReport.Queries.EXPLOSIVE_ID, Cop.EQUALS, explosiveV2.getUuid());
    qd.addByField(ExplosiveSignV2DailyReport.Queries.SKU_ID, Cop.IN, mapModifyLines.keySet().toArray());
    List<ExplosiveSignV2DailyReport> reports = explosiveSignV2DailyReportDao.list(tenant, qd);
    for (ExplosiveSignV2DailyReport report : reports) {
      ExplosiveV2Line explosiveV2Line = mapModifyLines.get(report.getSkuId());
      if (null == explosiveV2Line) {
        continue;
      }
      report.setLimitQty(explosiveV2Line.getLimitQty());
    }
    explosiveSignV2DailyReportDao.batchUpdate(tenant, reports, operateInfo);
  }

  @Override
  public QueryResult<ExplosiveReportSummary> queryGroupBySku(String tenant, String orgId, QueryDefinition qd) {
    Assert.hasText(tenant, "tenant");
    Assert.notNull(qd, "qd");

    //活动id查询汇总信息
    QueryResult<ExplosiveReportSummary> result = explosiveSignV2DailyReportDao.queryGroupBySku(tenant, qd);
    return result;
  }

  private void logFetchParts(String tenant, ExplosiveLogV2 log, String... fetchParts) {
    List<ExplosiveLogV2> logs = new ArrayList<>();
    logs.add(log);
    logFetchParts(tenant, logs, fetchParts);
  }

  private void logFetchParts(String tenant, List<ExplosiveLogV2> logs, String... fetchParts) {
    if (CollectionUtils.isEmpty(logs) || ArrayUtils.isEmpty(fetchParts)) {
      return;
    }
    List<String> owners = logs.stream().map(ExplosiveLogV2::getUuid).collect(Collectors.toList());
    // 爆品活动日志行
    if (ArrayUtils.contains(fetchParts, ExplosiveLogV2.FETCH_LINE)) {
      List<ExplosiveLogV2Line> result = explosiveLogV2LineDao.listByOwners(tenant, owners);
      if (CollectionUtils.isNotEmpty(result)) {
        Map<String, List<ExplosiveLogV2Line>> lineMap = result.stream()
            .collect(Collectors.groupingBy(ExplosiveLogV2Line::getOwner));
        logs.forEach(i -> i.setLines(lineMap.get(i.getUuid())));
      }
    }
  }

  private void pushDailyReport(String tenant, String explosiveLogId, OperateInfo operateInfo) {
    List<String> explosiveLogIds = new ArrayList<>(1);
    explosiveLogIds.add(explosiveLogId);
    pushDailyReport(tenant, explosiveLogIds, operateInfo);
  }

  private void pushDailyReport(String tenant, List<String> explosiveLogIds, OperateInfo operateInfo) {
    for (String uuid : explosiveLogIds) {
      ExplosiveReportV2EvCallMsg msg = new ExplosiveReportV2EvCallMsg();
      msg.setTenant(tenant);
      msg.setExplosiveLogId(uuid);
      msg.setOperateInfo(operateInfo);
      publisher.publishForNormal(ExplosiveReportV2EvCallExecutor.EXPLOSIVE_REPORT_V2_EXECUTOR_ID, msg);
    }
  }
}
