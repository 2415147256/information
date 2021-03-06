package com.hd123.baas.sop.service.impl.explosivev2.sign;

import com.alibaba.excel.util.DateUtils;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.hd123.baas.sop.annotation.Tx;
import com.hd123.baas.sop.service.api.explosivev2.ExplosiveV2;
import com.hd123.baas.sop.service.api.explosivev2.ExplosiveV2Line;
import com.hd123.baas.sop.service.api.explosivev2.LineLimitIncrInfo;
import com.hd123.baas.sop.service.api.explosivev2.report.ExplosiveLogV2;
import com.hd123.baas.sop.service.api.explosivev2.report.ExplosiveLogV2Line;
import com.hd123.baas.sop.service.api.explosivev2.report.ExplosiveLogV2Type;
import com.hd123.baas.sop.service.api.explosivev2.report.ExplosiveReportV2Service;
import com.hd123.baas.sop.service.api.explosivev2.sign.ExplosiveSignV2;
import com.hd123.baas.sop.service.api.explosivev2.sign.ExplosiveSignV2Line;
import com.hd123.baas.sop.service.api.explosivev2.sign.ExplosiveSignV2State;
import com.hd123.baas.sop.service.api.explosivev2.sign.ExplosiveV2SignService;
import com.hd123.baas.sop.service.dao.explosivev2.sign.ExplosiveSignV2DaoBof;
import com.hd123.baas.sop.service.dao.explosivev2.sign.ExplosiveV2SignLineDaoBof;
import com.hd123.baas.sop.service.impl.explosivev2.ExplosiveV2ServiceImpl;
import com.hd123.baas.sop.service.impl.explosivev2.report.ExplosiveReportV2ServiceImpl;
import com.hd123.baas.sop.evcall.EvCallEventPublisher;
import com.hd123.baas.sop.evcall.exector.explosivev2.ExplosiveV2EvCallExecutor;
import com.hd123.baas.sop.evcall.exector.explosivev2.ExplosiveV2EvCallMsg;
import com.hd123.baas.sop.utils.DateUtil;
import com.hd123.baas.sop.utils.IdGenUtils;
import com.hd123.mpa.api.common.ObjectNodeUtil;
import com.hd123.rumba.commons.biz.entity.Entity;
import com.hd123.rumba.commons.biz.entity.OperateInfo;
import com.hd123.rumba.commons.biz.entity.UCN;
import com.hd123.rumba.commons.biz.query.Cop;
import com.hd123.rumba.commons.biz.query.QueryDefinition;
import com.hd123.rumba.commons.biz.query.QueryResult;
import com.hd123.rumba.commons.lang.Assert;
import com.qianfan123.baas.common.BaasException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * ???????????????????????? ??????
 *
 * @author liuhaoxin
 * @since 2021-11-24
 */
@Slf4j
@Service
public class ExplosiveV2SignServiceImpl implements ExplosiveV2SignService {

  @Resource
  private ExplosiveSignV2DaoBof explosiveSignV2Dao;
  @Resource
  private ExplosiveV2SignLineDaoBof explosiveV2SignLineDao;
  @Autowired
  private EvCallEventPublisher publisher;
  @Autowired
  private ExplosiveReportV2ServiceImpl explosiveReportV2Service;
  @Autowired
  private ExplosiveV2ServiceImpl explosiveV2Service;
  @Autowired
  private ExplosiveReportV2Service reportV2Service;

  @Override
  @Tx
  public void batchSaveNew(String tenant, List<ExplosiveSignV2> signs, OperateInfo operateInfo) throws BaasException {
    Assert.hasText(tenant, "??????");
    Assert.notNull(signs, "????????????????????????");

    List<String> explosiveIds = signs.stream().map(ExplosiveSignV2::getExplosiveId).distinct().collect(Collectors.toList());

    QueryDefinition qd = new QueryDefinition();
    qd.addByField(ExplosiveV2.Queries.UUID, Cop.IN, signs.stream().map(ExplosiveSignV2::getExplosiveId).toArray());
    QueryResult<ExplosiveV2> result = explosiveV2Service.query(tenant, qd);

    if (result.getRecordCount() != explosiveIds.size()) {
      throw new BaasException("?????????????????????,???????????????????????????");
    }

    Map<String, ExplosiveV2> explosiveV2Map = result.getRecords().stream().collect(Collectors.toMap(ExplosiveV2::getUuid, o -> o));
    // ??????????????????????????? ??? ?????????????????????????????????
    List<ExplosiveSignV2Line> lines = new ArrayList<>();
    for (ExplosiveSignV2 sign : signs) {
      ExplosiveV2 explosiveV2 = explosiveV2Map.get(sign.getExplosiveId());
      initSign(explosiveV2, sign);

      lines.addAll(sign.getLines());
    }

    explosiveSignV2Dao.batchInsert(tenant, signs, operateInfo);
    explosiveV2SignLineDao.batchInsert(tenant, lines);
  }

  @Override
  @Tx
  public String submit(String tenant, ExplosiveSignV2 sign, OperateInfo operateInfo) throws BaasException {
    Assert.hasText(tenant, "??????");
    Assert.notNull(sign, "????????????????????????");
    // ????????????
    ExplosiveV2 explosiveV2 = explosiveV2Service.get(tenant, sign.getExplosiveId(), true, ExplosiveV2.PART_LINE);
    // ????????????????????????
    checkExplosive(explosiveV2);

    ExplosiveSignV2 history = explosiveSignV2Dao.get(tenant, sign.getUuid());
    if (ExplosiveSignV2State.SUBMITTED.equals(history.getState())) {
      log.info("????????????????????????ID:{},??????{}", sign.getShop().getUuid(), sign.getExplosiveId());
      return history.getUuid();
    }
    // ??????????????????
    checkExplosiveSign(history);
    // ?????????????????????????????????????????????
    checkSignMinQty(explosiveV2.getLines(), sign.getLines());
    // ???????????????????????????????????????????????????
    checkExplosiveLineRemainQty(explosiveV2.getLines(), sign.getLines());

    // ??????????????????????????????
    sign.setState(ExplosiveSignV2State.SUBMITTED);
    buildExplosiveLine(sign);
    // ??????????????????????????????
    explosiveSignV2Dao.updateState(tenant, sign.getUuid(), sign.getState(), operateInfo);
    explosiveV2SignLineDao.delByOwner(tenant, sign.getUuid());
    explosiveV2SignLineDao.batchInsert(tenant, sign.getLines());
    // ?????????????????????????????????
    Map<String, BigDecimal> skuSignQtyMap = sign.getLines().stream().collect(Collectors.toMap(ExplosiveSignV2Line::getSkuId, ExplosiveSignV2Line::getQty));
    // ????????????
    List<LineLimitIncrInfo> infos = new ArrayList<>(skuSignQtyMap.size());
    for (Map.Entry<String, BigDecimal> entry : skuSignQtyMap.entrySet()) {
      LineLimitIncrInfo lineLimitIncrInfo = new LineLimitIncrInfo();
      lineLimitIncrInfo.setSkuId(entry.getKey());
      lineLimitIncrInfo.setQty(entry.getValue());
      infos.add(lineLimitIncrInfo);
    }
    explosiveV2Service.incrLineLimit(tenant, sign.getExplosiveId(), infos);
    //????????????????????????
    sign.setExplosive(explosiveV2);
    ExplosiveLogV2 log = buildExplosiveLogV2(IdGenUtils.buildRdUuid(), sign);
    reportV2Service.saveNewLog(tenant, log, operateInfo);
    return sign.getUuid();
  }

  @Override
  @Tx
  public void cancel(String tenant, List<String> uuids, OperateInfo operateInfo) {
    Assert.hasText(tenant, "??????");
    Assert.notNull(uuids, "????????????id");

    explosiveSignV2Dao.batchUpdateState(tenant, uuids, ExplosiveSignV2State.CANCELED, operateInfo);
  }

  @Override
  @Tx
  public void changeLinesQty(String tenant, String uuid, List<ExplosiveSignV2Line> lines, OperateInfo operateInfo) throws BaasException {
    Assert.hasText(tenant, "??????");
    Assert.notNull(uuid, "????????????id");
    Assert.notEmpty(lines, "lines");

    ExplosiveSignV2 history = get(tenant, uuid, ExplosiveSignV2.FETCH_ALL);
    if (Objects.isNull(history)) {
      throw new BaasException("?????????????????????????????????!");
    }
    // ??????
    ExplosiveV2 explosiveV2 = explosiveV2Service.get(tenant, history.getExplosiveId(), true, ExplosiveV2.PART_LINE);
    if (Objects.isNull(explosiveV2)) {
      throw new BaasException("?????????????????????,????????????");
    }
    // ?????????????????????????????????????????????
    checkSignMinQty(explosiveV2.getLines(), lines);
    // ???????????????????????????
    for (ExplosiveSignV2Line line : lines) {
      for (ExplosiveSignV2Line item : history.getLines()) {
        if (line.getSkuId().equals(item.getSkuId())) {
          line.setHistoryQty(item.getQty());
        }
      }
    }
    // ??????
    checkExplosiveLineRemainQty(explosiveV2.getLines(), lines);
    explosiveV2SignLineDao.updateLineQty(tenant, uuid, lines);
    // ????????????
    List<LineLimitIncrInfo> infos = new ArrayList<>();
    lines.forEach(i -> {
      LineLimitIncrInfo item = new LineLimitIncrInfo();
      item.setSkuId(i.getSkuId());
      item.setQty(i.getQty().subtract(i.getHistoryQty()));
      infos.add(item);
    });
    explosiveV2Service.incrLineLimit(tenant, history.getExplosiveId(), infos);
    Map<String, ExplosiveSignV2Line> lineMap = lines.stream().collect(Collectors.toMap(ExplosiveSignV2Line::getUuid, i -> i));
    for (ExplosiveSignV2Line line : history.getLines()) {
      if (!Objects.isNull(lineMap.get(line.getUuid()))) {
        line.setQty(lineMap.get(line.getUuid()).getQty());
      }
    }
    ExplosiveLogV2 log = buildExplosiveLogV2(IdGenUtils.buildRdUuid(), history);
    reportV2Service.updateLog(tenant, log, operateInfo);
  }

  @Override
  @Tx
  public void setFinish(String tenant, List<String> explosiveIds, OperateInfo operateInfo) {
    Assert.notNull(tenant, "tenant");

    if (CollectionUtils.isEmpty(explosiveIds)) {
      return;
    }

    // ????????????????????????????????????
    QueryDefinition qd = new QueryDefinition();
    qd.addByField(ExplosiveSignV2.Queries.EXPLOSIVE_ID, Cop.IN, explosiveIds.toArray());
    qd.addByField(ExplosiveSignV2.Queries.STATE, Cop.EQUALS, ExplosiveSignV2State.SUBMITTED.name());
    QueryResult<ExplosiveSignV2> result = query(tenant, qd, ExplosiveSignV2.FETCH_ALL);
    if (CollectionUtils.isEmpty(result.getRecords())) {
      log.info("???????????????");
      return;
    }
    // ????????????????????????
    List<String> confirmUuids = result.getRecords().stream()
        .map(Entity::getUuid)
        .collect(Collectors.toList());
    if (CollectionUtils.isEmpty(confirmUuids)) {
      return;
    }
    explosiveSignV2Dao.batchUpdateState(tenant, confirmUuids, ExplosiveSignV2State.CONFIRMED, operateInfo);
    //??????N??????????????????????????? explosive_log_v2;
    List<ExplosiveLogV2> signLogs = new ArrayList<>();
    for (ExplosiveSignV2 item : result.getRecords()) {
      String tranId = IdGenUtils.buildIidAsString();
      if (ExplosiveSignV2State.SUBMITTED.equals(item.getState())) {
        ExplosiveLogV2 explosiveLogV2 = buildExplosiveLogV2(tranId, item);
        signLogs.add(explosiveLogV2);
      }
    }
    // ????????????????????????????????????
    if (CollectionUtils.isNotEmpty(signLogs)) {
      // ???????????????????????????H6
      // ??????
      Map<String, List<ExplosiveSignV2>> map = result.getRecords().stream()
          .filter(i -> ExplosiveSignV2State.SUBMITTED.equals(i.getState()))
          .collect(Collectors.groupingBy(ExplosiveSignV2::getExplosiveId));
      for (Map.Entry<String, List<ExplosiveSignV2>> item : map.entrySet()) {
        String explosiveId = item.getKey();
        List<String> values = item.getValue().stream().map(ExplosiveSignV2::getUuid).collect(Collectors.toList());
        log.info("?????????????????????h6,explosiveId={}", explosiveId);
        pushExplosiveMessage(tenant, explosiveId, values);
        log.info("????????????");
      }
    }
  }

  @Override
  public QueryResult<ExplosiveSignV2> query(String tenant, QueryDefinition qd, String... fetchParts) {
    Assert.hasText(tenant, "??????");
    Assert.notNull(qd, "qd");

    QueryResult<ExplosiveSignV2> result = explosiveSignV2Dao.query(tenant, qd);
    if (result.getRecordCount() == 0) {
      return result;
    }
    List<ExplosiveSignV2> signs = result.getRecords();
    fetchParts(tenant, signs, fetchParts);
    result.setRecords(signs);
    return result;
  }

  @Override
  public ExplosiveSignV2 get(String tenant, String uuid, String... fetchParts) throws BaasException {
    Assert.hasText(tenant, "??????");
    Assert.notNull(uuid, "owner");

    ExplosiveSignV2 sign = explosiveSignV2Dao.get(tenant, uuid);
    // fetchPart
    fetchParts(tenant, sign, fetchParts);
    return sign;
  }

  @Override
  public List<ExplosiveSignV2> list(String tenant, List<String> uuids, String... fetchParts) {
    Assert.hasText(tenant, "??????");
    Assert.notNull(uuids, "uuids");

    List<ExplosiveSignV2> signs = explosiveSignV2Dao.list(tenant, uuids, false);
    fetchParts(tenant, signs, fetchParts);
    return signs;
  }

  private void checkExplosive(ExplosiveV2 explosiveV2) throws BaasException {
    if (Objects.isNull(explosiveV2)) {
      throw new BaasException("???????????????????????????");
    }
    if (explosiveV2.getSignStartDate().after(new Date())) {
      throw new BaasException("???????????????????????????????????????????????????");
    }
    if (explosiveV2.getSignEndDate().before(new Date())) {
      throw new BaasException("??????????????????????????????????????????????????????");
    }
    if (!ExplosiveV2.State.ACTIVE.equals(explosiveV2.getState())) {
      log.info("???????????????????????????????????????????????????????????????id???{},?????????{}", explosiveV2.getUuid(), explosiveV2.getState());
      throw new BaasException("???????????????????????????????????????????????????");
    }
  }

  private void initSign(ExplosiveV2 explosiveV2, ExplosiveSignV2 sign) {
    // ??????????????????
    if (StringUtils.isBlank(sign.getUuid())) {
      sign.setUuid(IdGenUtils.buildRdUuid());
    }
    sign.setState(ExplosiveSignV2State.INIT);
    sign.setExt(buildExt(explosiveV2));
    buildLines(sign);
  }

  private void checkExplosiveSign(ExplosiveSignV2 oldSign) throws BaasException {
    if (Objects.isNull(oldSign)) {
      throw new BaasException("?????????????????????????????????!");
    }
    if (ExplosiveSignV2State.CANCELED.equals(oldSign.getState())) {
      throw new BaasException("?????????????????????????????????????????????!");
    }
    if (ExplosiveSignV2State.CONFIRMED.equals(oldSign.getState())) {
      throw new BaasException("?????????????????????????????????????????????!");
    }
  }

  private void pushExplosiveMessage(String tenant, String explosiveId, List<String> signIds) {
    ExplosiveV2EvCallMsg msg = new ExplosiveV2EvCallMsg();
    msg.setTenant(tenant);
    msg.setExplosiveSignIds(signIds);
    msg.setExplosiveId(explosiveId);
    publisher.publishForNormal(ExplosiveV2EvCallExecutor.EXPLOSIVE_V2_EXECUTOR_ID, msg);
  }

  private ExplosiveLogV2 buildExplosiveLogV2(String tranId, ExplosiveSignV2 sign) {
    // ??????????????????
    ExplosiveLogV2 explosiveLog = new ExplosiveLogV2();
    explosiveLog.setUuid(IdGenUtils.buildRdUuid());
    explosiveLog.setOrgId(sign.getOrgId());
    explosiveLog.setExplosiveId(sign.getExplosiveId());
    if (Objects.nonNull(sign.getShop())) {
      UCN shop = new UCN();
      shop.setUuid(sign.getShop().getUuid());
      shop.setCode(sign.getShop().getCode());
      shop.setName(sign.getShop().getName());
      explosiveLog.setShop(shop);
    }
    explosiveLog.setSourceId(sign.getUuid());
    explosiveLog.setTranId(tranId);
    if (Objects.nonNull(sign.getExplosive())) {
      explosiveLog.setSourceFlowNo(sign.getExplosive().getFlowNo());
    }
    explosiveLog.setSourceType(ExplosiveLogV2Type.SIGN);
    explosiveLog.setSourceAction("SIGN");
    explosiveLog.setSourceBusinessDate(DateUtil.toDate(DateFormatUtils.format(new Date(), "yyyy-MM-dd")));

    //???????????????????????????
    List<ExplosiveLogV2Line> lines = new ArrayList<>(0);
    if (CollectionUtils.isNotEmpty(sign.getLines())) {
      for (ExplosiveSignV2Line line : sign.getLines()) {
        ExplosiveLogV2Line logLine = new ExplosiveLogV2Line();
        logLine.setUuid(IdGenUtils.buildRdUuid());
        logLine.setTenant(explosiveLog.getTenant());
        logLine.setOwner(explosiveLog.getUuid());
        logLine.setSkuGid(line.getSkuGid());
        logLine.setSkuId(line.getSkuId());
        logLine.setSkuCode(line.getSkuCode());
        logLine.setSkuName(line.getSkuName());
        logLine.setSkuQpc(line.getSkuQpc());
        logLine.setSkuUnit(line.getSkuUnit());
        logLine.setQty(line.getQty());

        lines.add(logLine);
      }
    }
    explosiveLog.setLines(lines);
    return explosiveLog;
  }


  private void fetchParts(String tenant, ExplosiveSignV2 sign, String... fetchParts) {
    if (Objects.isNull(sign)) {
      return;
    }
    List<ExplosiveSignV2> signs = new ArrayList<>(0);
    signs.add(sign);
    fetchParts(tenant, signs, fetchParts);
  }

  private void fetchParts(String tenant, List<ExplosiveSignV2> signs, String... fetchParts) {
    if (CollectionUtils.isEmpty(signs) || ArrayUtils.isEmpty(fetchParts)) {
      return;
    }
    List<String> owners = signs.stream().map(ExplosiveSignV2::getUuid).collect(Collectors.toList());
    List<String> explosiveIds = signs.stream().map(ExplosiveSignV2::getExplosiveId).collect(Collectors.toList());

    if (ArrayUtils.contains(fetchParts, ExplosiveSignV2.FETCH_LINE)) {
      List<ExplosiveSignV2Line> result = explosiveV2SignLineDao.listByOwners(tenant, owners);
      if (CollectionUtils.isNotEmpty(result)) {
        Map<String, List<ExplosiveSignV2Line>> lineMap = result.stream()
            .collect(Collectors.groupingBy(ExplosiveSignV2Line::getOwner));
        signs.forEach(i -> i.setLines(lineMap.get(i.getUuid())));
      }
    }
    //??????????????????
    if (ArrayUtils.contains(fetchParts, ExplosiveSignV2.FETCH_EXPLOSIVE)) {
      QueryDefinition qd = new QueryDefinition();
      qd.addByField(ExplosiveV2.Queries.UUID, Cop.IN, explosiveIds.toArray());
      QueryResult<ExplosiveV2> result = explosiveV2Service.query(tenant, qd, ExplosiveV2.PART_LINE, ExplosiveV2.PART_SCOPE);
      if (result.getRecordCount() > 0) {
        Map<String, ExplosiveV2> explosiveV2Map = result.getRecords().stream().collect(Collectors.toMap(ExplosiveV2::getUuid, o -> o));
        signs.forEach(i -> i.setExplosive(explosiveV2Map.get(i.getExplosiveId())));
      }
    }
  }

  private void buildExplosiveLine(ExplosiveSignV2 sign) {
    if (CollectionUtils.isNotEmpty(sign.getLines())) {
      sign.getLines().forEach(i -> {
        i.setOwner(sign.getUuid());
        if (StringUtils.isEmpty(i.getUuid())) {
          i.setUuid(IdGenUtils.buildRdUuid());
        }
      });
    }
  }

  private ObjectNode buildExt(ExplosiveV2 source) {
    Assert.notNull(source);
    //??????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????? ext {"explosive":{"name","????????????","XX":"YYY"}}
    ObjectNode ext = ObjectNodeUtil.createObjectNode();
    ObjectNode explosive = ObjectNodeUtil.createObjectNode();
    explosive.put(ExplosiveSignV2.Ext.EXPLOSIVE_NAME, source.getName());
    explosive.put(ExplosiveSignV2.Ext.EXPLOSIVE_START_TIME, DateUtils.format(source.getStartDate()));
    explosive.put(ExplosiveSignV2.Ext.EXPLOSIVE_END_TIME, DateUtils.format(source.getEndDate()));
    explosive.put(ExplosiveSignV2.Ext.EXPLOSIVE_SIGN_START_TIME, DateUtils.format(source.getSignStartDate()));
    explosive.put(ExplosiveSignV2.Ext.EXPLOSIVE_SIGN_END_TIME, DateUtils.format(source.getSignEndDate()));
    ext.set(ExplosiveSignV2.Ext.EXPLOSIVE, explosive);
    return ext;
  }

  private void buildLines(ExplosiveSignV2 item) {
    // ???????????????
    if (CollectionUtils.isEmpty(item.getLines())) {
      return;
    }
    for (ExplosiveSignV2Line line : item.getLines()) {
      if (StringUtils.isBlank(line.getUuid())) {
        line.setUuid(IdGenUtils.buildRdUuid());
      }
      line.setOwner(item.getUuid());
    }
  }


  private void checkExplosiveLineRemainQty(List<ExplosiveV2Line> explosiveLines, List<ExplosiveSignV2Line> signLines) throws BaasException {
    // ??????????????????????????????(?????? ?????????)??????????????????;  ????????? = ?????????-???????????????
    Map<String, ExplosiveV2Line> remainQtyMap = explosiveLines.stream()
        .collect(Collectors.toMap(ExplosiveV2Line::getSkuId, i -> i));

    for (ExplosiveSignV2Line item : signLines) {
      BigDecimal remainQty = getRemainQty(remainQtyMap.get(item.getSkuId()));
      BigDecimal diffQty = item.getQty().subtract(item.getHistoryQty());
      if (remainQty.compareTo(diffQty) < 0) {
        log.info("??????Id{},??????????????????{},?????????????????????{}", item.getSkuId(), remainQty, diffQty);
        throw new BaasException("???????????????????????????????????????");
      }
    }
  }

  private BigDecimal getRemainQty(ExplosiveV2Line line) {
    if (line.getLimitQty() == null || line.getLimitQty().compareTo(BigDecimal.ZERO) <= 0) {
      return new BigDecimal(9999999);
    }
    return line.getLimitQty().subtract(line.getUsedLimit());
  }

  private void checkSignMinQty(List<ExplosiveV2Line> explosiveLines, List<ExplosiveSignV2Line> signLines) throws BaasException {
    // ?????????????????????????????????
    List<ExplosiveSignV2Line> noZeroLines = signLines.stream().filter(i -> i.getQty().compareTo(BigDecimal.ZERO) > 0).collect(Collectors.toList());
    Map<String, ExplosiveV2Line> explosiveLineMap = explosiveLines.stream().collect(Collectors.toMap(ExplosiveV2Line::getSkuId, o -> o));
    for (ExplosiveSignV2Line signLine : noZeroLines) {
      ExplosiveV2Line explosiveLine = explosiveLineMap.get(signLine.getSkuId());
      if (Objects.isNull(explosiveLine)) {
        log.info("?????????????????????!?????????id{},??????id{}", signLine.getUuid(), signLine.getSkuId());
        throw new BaasException("?????????????????????!");
      }
      if (signLine.getQty().compareTo(explosiveLine.getMinQty()) < 0) {
        log.info("???????????????????????????????????????!?????????id{},??????id{},????????????{},????????????{}???", signLine.getUuid(), signLine.getSkuId(), explosiveLine.getMinQty(), signLine.getQty());
        throw new BaasException("??????????????????????????????????????????");
      }
    }
  }
}
