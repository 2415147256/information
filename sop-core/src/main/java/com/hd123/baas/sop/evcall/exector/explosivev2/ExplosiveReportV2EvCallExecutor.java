package com.hd123.baas.sop.evcall.exector.explosivev2;

import com.hd123.baas.sop.service.api.explosivev2.ExplosiveV2;
import com.hd123.baas.sop.service.api.explosivev2.ExplosiveV2Line;
import com.hd123.baas.sop.service.api.explosivev2.report.ExplosiveLogV2;
import com.hd123.baas.sop.service.api.explosivev2.report.ExplosiveLogV2Line;
import com.hd123.baas.sop.service.api.explosivev2.report.ExplosiveLogV2Type;
import com.hd123.baas.sop.service.api.explosivev2.report.ExplosiveSignV2DailyReport;
import com.hd123.baas.sop.service.impl.explosivev2.ExplosiveV2ServiceImpl;
import com.hd123.baas.sop.service.impl.explosivev2.report.ExplosiveReportV2ServiceImpl;
import com.hd123.baas.sop.evcall.AbstractEvCallExecutor;
import com.hd123.baas.sop.utils.BaasJSONUtil;
import com.hd123.baas.sop.utils.DateUtil;
import com.hd123.rumba.commons.biz.entity.OperateInfo;
import com.hd123.rumba.commons.biz.query.Cop;
import com.hd123.rumba.commons.biz.query.QueryDefinition;
import com.hd123.rumba.evcall.EvCallExecutionContext;
import com.qianfan123.baas.common.BaasException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 爆品活动详情报表
 *
 * @author liuhaoxin
 * @date 2021-12-7
 */
@Slf4j
@Component
public class ExplosiveReportV2EvCallExecutor extends AbstractEvCallExecutor<ExplosiveReportV2EvCallMsg> {

  private static final ThreadLocal<DateFormat> SDF = ThreadLocal.withInitial(() -> new SimpleDateFormat("yyyy-MM-dd"));
  public static final String EXPLOSIVE_REPORT_V2_EXECUTOR_ID = ExplosiveReportV2EvCallExecutor.class.getSimpleName();

  @Autowired
  private ExplosiveReportV2ServiceImpl explosiveReportV2Service;
  @Autowired
  private ExplosiveV2ServiceImpl explosiveV2Service;

  @Override
  protected void doExecute(ExplosiveReportV2EvCallMsg message, EvCallExecutionContext context) throws Exception {
    log.info("生成爆品活动详情报表");
    String tenant = message.getTenant();
    String explosiveLogId = message.getExplosiveLogId();
    OperateInfo operateInfo = message.getOperateInfo();

    // 查询爆品报名信息
    ExplosiveLogV2 explosiveLog = explosiveReportV2Service.getLog(tenant, explosiveLogId, ExplosiveLogV2.FETCH_LINE);
    if (Objects.isNull(explosiveLog)) {
      log.info("不存在爆品活动报名日志信息；日志ID：{}", explosiveLogId);
      return;
    }
    if (CollectionUtils.isEmpty(explosiveLog.getLines())) {
      log.info("不存在爆品活动报名信息");
      return;
    }
    if (ExplosiveLogV2Type.SIGN.equals(explosiveLog.getSourceType())) {
      processSign(tenant, explosiveLog, operateInfo);
    }
    if (ExplosiveLogV2Type.ORDER.equals(explosiveLog.getSourceType())) {
      processOrder(tenant, explosiveLog, operateInfo);
    }
    if (ExplosiveLogV2Type.STK_OUT.equals(explosiveLog.getSourceType())
        || ExplosiveLogV2Type.CRO_ORG_SALE.equals(explosiveLog.getSourceType())) {
      processStkOut(tenant, explosiveLog, operateInfo);
    }
  }

  private void processSign(String tenant, ExplosiveLogV2 explosiveLog, OperateInfo operateInfo) {
    List<ExplosiveSignV2DailyReport> reports = queryReport(tenant, explosiveLog);
    ExplosiveV2 explosive = explosiveV2Service.get(tenant, explosiveLog.getExplosiveId(), false, ExplosiveV2.PART_LINE);
    // 加工explosive_log日志的数据
    List<ExplosiveSignV2DailyReport> insert = new ArrayList<>();
    List<ExplosiveSignV2DailyReport> update = new ArrayList<>();
    if (CollectionUtils.isEmpty(reports)) {
      for (ExplosiveLogV2Line item : explosiveLog.getLines()) {
        ExplosiveSignV2DailyReport dailyReport = buildExplosiveSignV2DailyReport(tenant, explosive, explosiveLog, item);
        insert.add(dailyReport);
      }
      explosiveReportV2Service.batchInsert(tenant, insert, operateInfo);
    } else {
      // 生成日志报表
      Map<String, ExplosiveSignV2DailyReport> reportMap = reports.stream().
          collect(Collectors.toMap(ExplosiveSignV2DailyReport::getSkuCode, i -> i));

      for (ExplosiveLogV2Line item : explosiveLog.getLines()) {
        ExplosiveSignV2DailyReport report = reportMap.get(item.getSkuCode());
        if (Objects.isNull(report)) {
          ExplosiveSignV2DailyReport dailyReport = buildExplosiveSignV2DailyReport(tenant, explosive, explosiveLog, item);
          insert.add(dailyReport);
        } else {
          // 设置报表 报名数->数据库进行原子增加、订货数、配货数->数据库进行原子增加
          report.setSignQty(item.getQty().multiply(item.getSkuQpc()).divide(report.getSkuQpc(), 2, RoundingMode.HALF_UP));
          update.add(report);
        }
      }
      if (CollectionUtils.isNotEmpty(insert)) {
        explosiveReportV2Service.batchInsert(tenant, insert, operateInfo);
      }
      if (CollectionUtils.isNotEmpty(update)) {
        explosiveReportV2Service.batchUpdate(tenant, update, operateInfo);
      }
    }
  }

  private void processOrder(String tenant, ExplosiveLogV2 explosiveLog, OperateInfo operateInfo) {
    List<ExplosiveSignV2DailyReport> reports = queryReport(tenant, explosiveLog);
    ExplosiveV2 explosive = explosiveV2Service.get(tenant, explosiveLog.getExplosiveId(), false, ExplosiveV2.PART_LINE);
    // 加工explosive_log日志的数据
    List<ExplosiveSignV2DailyReport> insert = new ArrayList<>();
    List<ExplosiveSignV2DailyReport> update = new ArrayList<>();
    if (CollectionUtils.isEmpty(reports)) {
      for (ExplosiveLogV2Line item : explosiveLog.getLines()) {
        ExplosiveSignV2DailyReport dailyReport = buildExplosiveSignV2DailyReport(tenant, explosive, explosiveLog, item);
        insert.add(dailyReport);
      }
      explosiveReportV2Service.batchInsert(tenant, insert, operateInfo);
    } else {
      // 生成日志报表
      Map<String, ExplosiveSignV2DailyReport> reportMap = reports.stream().
          collect(Collectors.toMap(ExplosiveSignV2DailyReport::getSkuCode, i -> i));

      for (ExplosiveLogV2Line item : explosiveLog.getLines()) {
        ExplosiveSignV2DailyReport report = reportMap.get(item.getSkuCode());
        if (Objects.isNull(report)) {
          ExplosiveSignV2DailyReport dailyReport = buildExplosiveSignV2DailyReport(tenant, explosive, explosiveLog, item);
          insert.add(dailyReport);
        } else {
          // 设置报表 报名数->数据库进行原子增加、订货数、配货数->数据库进行原子增加
          report.setShippedQty(BigDecimal.ZERO);
          report.setOrderQty(item.getQty().multiply(item.getSkuQpc()).divide(report.getSkuQpc(), 2, RoundingMode.HALF_UP));
          update.add(report);
        }
      }
      if (CollectionUtils.isNotEmpty(insert)) {
        explosiveReportV2Service.batchInsert(tenant, insert, operateInfo);
      }
      if (CollectionUtils.isNotEmpty(update)) {
        explosiveReportV2Service.batchUpdate(tenant, update, operateInfo);
      }
    }
  }

  private void processStkOut(String tenant, ExplosiveLogV2 explosiveLog, OperateInfo operateInfo) {
    List<ExplosiveSignV2DailyReport> reports = queryReport(tenant, explosiveLog);
    ExplosiveV2 explosive = explosiveV2Service.get(tenant, explosiveLog.getExplosiveId(), false, ExplosiveV2.PART_LINE);
    // 加工explosive_log日志的数据
    List<ExplosiveSignV2DailyReport> insert = new ArrayList<>();
    List<ExplosiveSignV2DailyReport> update = new ArrayList<>();
    if (CollectionUtils.isEmpty(reports)) {
      for (ExplosiveLogV2Line item : explosiveLog.getLines()) {
        ExplosiveSignV2DailyReport dailyReport = buildExplosiveSignV2DailyReport(tenant, explosive, explosiveLog, item);
        insert.add(dailyReport);
      }
      explosiveReportV2Service.batchInsert(tenant, insert, operateInfo);
    } else {
      // 生成日志报表
      Map<String, ExplosiveSignV2DailyReport> reportMap = reports.stream().
          collect(Collectors.toMap(ExplosiveSignV2DailyReport::getSkuCode, i -> i));

      for (ExplosiveLogV2Line item : explosiveLog.getLines()) {
        ExplosiveSignV2DailyReport report = reportMap.get(item.getSkuCode());
        if (Objects.isNull(report)) {
          ExplosiveSignV2DailyReport dailyReport = buildExplosiveSignV2DailyReport(tenant, explosive, explosiveLog, item);
          insert.add(dailyReport);
        } else {
          // 设置报表 报名数->数据库进行原子增加、订货数、配货数->数据库进行原子增加
          report.setOrderQty(BigDecimal.ZERO);
          report.setShippedQty(item.getQty().multiply(item.getSkuQpc()).divide(report.getSkuQpc(), 2, RoundingMode.HALF_UP));
          update.add(report);
        }
      }
      if (CollectionUtils.isNotEmpty(insert)) {
        explosiveReportV2Service.batchInsert(tenant, insert, operateInfo);
      }
      if (CollectionUtils.isNotEmpty(update)) {
        explosiveReportV2Service.batchUpdate(tenant, update, operateInfo);
      }
    }
  }

  private List<ExplosiveSignV2DailyReport> queryReport(String tenant, ExplosiveLogV2 explosiveLog) {
    List<String> skuCode = explosiveLog.getLines().stream().map(ExplosiveLogV2Line::getSkuCode).collect(Collectors.toList());
    //查询：每日活动报表中的记录信息；活动+门店+发生日期+商品CODE 唯一;前提，爆品活动创建：爆品商品唯一
    QueryDefinition qd = new QueryDefinition();
    qd.addByField(ExplosiveSignV2DailyReport.Queries.EXPLOSIVE_ID, Cop.EQUALS, explosiveLog.getExplosiveId());
    qd.addByField(ExplosiveSignV2DailyReport.Queries.SHOP_ID, Cop.EQUALS, explosiveLog.getShop().getUuid());
    if (!ExplosiveLogV2Type.SIGN.equals(explosiveLog.getSourceType())) {
      qd.addByField(ExplosiveSignV2DailyReport.Queries.BUSINESS_DATE, Cop.EQUALS, DateUtil.toDate(SDF.get().format(explosiveLog.getSourceBusinessDate())));
    }
    qd.addByField(ExplosiveSignV2DailyReport.Queries.SKU_CODE, Cop.IN, skuCode.toArray());

    List<ExplosiveSignV2DailyReport> reports = explosiveReportV2Service.query(tenant, qd).getRecords();
    return reports;
  }

  private ExplosiveSignV2DailyReport buildExplosiveSignV2DailyReport(String tenant, ExplosiveV2 explosive, ExplosiveLogV2 explosiveLog, ExplosiveLogV2Line logLine) {

    ExplosiveSignV2DailyReport dailyReport = new ExplosiveSignV2DailyReport();
    dailyReport.setTenant(tenant);
    dailyReport.setOrgId(explosiveLog.getOrgId());
    dailyReport.setExplosiveId(explosiveLog.getExplosiveId());

    // 活动名称:删除数据库，找不到对应爆品
    if (Objects.nonNull(explosive)) {
      dailyReport.setExplosiveName(explosive.getName());
    }

    if (Objects.nonNull(explosiveLog.getShop())) {
      dailyReport.setShopId(explosiveLog.getShop().getUuid());
      dailyReport.setShopCode(explosiveLog.getShop().getCode());
      dailyReport.setShopName(explosiveLog.getShop().getName());
    }
    // Map<skuId,ExplosiveV2Line>
    Map<String, ExplosiveV2Line> explosiveLineMap = explosive.getLines().stream().collect(Collectors.toMap(ExplosiveV2Line::getSkuCode, i -> i));
    ExplosiveV2Line explosiveLine = explosiveLineMap.get(logLine.getSkuCode());
    if (Objects.nonNull(explosiveLine)) {
      dailyReport.setInPrice(explosiveLine.getInPrice());
      dailyReport.setLimitQty(explosiveLine.getLimitQty());
      dailyReport.setMinQty(explosiveLine.getMinQty());
      dailyReport.setRemark(explosiveLine.getRemark());
    }
    dailyReport.setSkuId(explosiveLine.getSkuId());
    dailyReport.setSkuCode(explosiveLine.getSkuCode());
    dailyReport.setSkuName(explosiveLine.getSkuName());
    dailyReport.setSkuGid(explosiveLine.getSkuGid());
    dailyReport.setSkuQpc(explosiveLine.getSkuQpc());
    dailyReport.setSkuUnit(explosiveLine.getSkuUnit());

    // 时间格式化（yyyy-MM-dd）
    dailyReport.setBusinessDate(DateUtil.toDate(SDF.get().format(explosiveLog.getSourceBusinessDate())));

    dailyReport.setSignQty(BigDecimal.ZERO);
    dailyReport.setOrderQty(BigDecimal.ZERO);
    dailyReport.setShippedQty(BigDecimal.ZERO);
    if (ExplosiveLogV2Type.SIGN.equals(explosiveLog.getSourceType())) {
      dailyReport.setSignQty(logLine.getQty().multiply(logLine.getSkuQpc()).divide(dailyReport.getSkuQpc(), 2, RoundingMode.HALF_UP));
    }
    if (ExplosiveLogV2Type.STK_OUT.equals(explosiveLog.getSourceType())) {
      dailyReport.setShippedQty(logLine.getQty().multiply(logLine.getSkuQpc()).divide(dailyReport.getSkuQpc(), 2, RoundingMode.HALF_UP));
    }
    if (ExplosiveLogV2Type.CRO_ORG_SALE.equals(explosiveLog.getSourceType())) {
      dailyReport.setShippedQty(logLine.getQty().multiply(logLine.getSkuQpc()).divide(dailyReport.getSkuQpc(), 2, RoundingMode.HALF_UP));
    }
    if (ExplosiveLogV2Type.ORDER.equals(explosiveLog.getSourceType())) {
      dailyReport.setOrderQty(logLine.getQty().multiply(logLine.getSkuQpc()).divide(dailyReport.getSkuQpc(), 2, RoundingMode.HALF_UP));
    }

    return dailyReport;
  }

  @Override
  protected ExplosiveReportV2EvCallMsg decodeMessage(String msg) throws BaasException {
    log.info("爆品活动报表消息SubsidyPlanMsg:{}", msg);
    return BaasJSONUtil.safeToObject(msg, ExplosiveReportV2EvCallMsg.class);
  }
}
