package com.hd123.baas.sop.service.impl.explosivev2;

import com.google.common.collect.Lists;
import com.hd123.baas.sop.annotation.Tx;
import com.hd123.baas.sop.evcall.exector.explosivev2.ExplosiveAutoStartEvCallExecutor;
import com.hd123.baas.sop.evcall.exector.explosivev2.ExplosivePrepareOnMsg;
import com.hd123.baas.sop.evcall.exector.explosivev2.ExplosivePrepareOnOffEvCallExecutor;
import com.hd123.baas.sop.evcall.exector.explosivev2.ExplosiveSignAutoEndMsg;
import com.hd123.baas.sop.service.api.basedata.store.Store;
import com.hd123.baas.sop.service.api.basedata.store.StoreFilter;
import com.hd123.baas.sop.service.api.basedata.store.StoreService;
import com.hd123.baas.sop.service.api.explosivev2.ExplosiveActionV2;
import com.hd123.baas.sop.service.api.explosivev2.ExplosiveScope;
import com.hd123.baas.sop.service.api.explosivev2.ExplosiveV2;
import com.hd123.baas.sop.service.api.explosivev2.ExplosiveV2Line;
import com.hd123.baas.sop.service.api.explosivev2.ExplosiveV2Service;
import com.hd123.baas.sop.service.api.explosivev2.LineLimitIncrInfo;
import com.hd123.baas.sop.service.api.explosivev2.sign.ExplosiveSignV2;
import com.hd123.baas.sop.service.api.explosivev2.sign.ExplosiveSignV2Line;
import com.hd123.baas.sop.service.api.explosivev2.sign.ExplosiveSignV2State;
import com.hd123.baas.sop.service.api.explosivev2.sign.ExplosiveV2SignService;
import com.hd123.baas.sop.service.impl.price.BillNumberMgr;
import com.hd123.baas.sop.evcall.EvCallEventPublisher;
import com.hd123.baas.sop.evcall.exector.explosivev2.ExplosiveV2EvCallExecutor;
import com.hd123.baas.sop.evcall.exector.explosivev2.ExplosiveV2EvCallMsg;
import com.hd123.baas.sop.service.dao.explosivev2.ExplosiveScopeDao;
import com.hd123.baas.sop.service.dao.explosivev2.ExplosiveV2Dao;
import com.hd123.baas.sop.service.dao.explosivev2.ExplosiveV2LineDao;
import com.hd123.baas.sop.utils.DateUtil;
import com.hd123.baas.sop.utils.IdGenUtils;
import com.hd123.baas.sop.utils.JsonUtil;
import com.hd123.rumba.commons.biz.entity.OperateInfo;
import com.hd123.rumba.commons.biz.entity.Operator;
import com.hd123.rumba.commons.biz.entity.UCN;
import com.hd123.rumba.commons.biz.query.Cop;
import com.hd123.rumba.commons.biz.query.QueryDefinition;
import com.hd123.rumba.commons.biz.query.QueryResult;
import com.hd123.rumba.commons.lang.Assert;
import com.qianfan123.baas.common.BaasException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author shenmin
 */
@Service
@Slf4j
public class ExplosiveV2ServiceImpl implements ExplosiveV2Service {
  @Autowired
  private ExplosiveV2Dao explosiveV2Dao;
  @Autowired
  private ExplosiveV2LineDao explosiveV2LineDao;
  @Autowired
  private ExplosiveScopeDao explosiveScopeDao;
  @Autowired
  private BillNumberMgr billNumberMgr;
  @Autowired
  private ExplosiveV2SignService explosiveV2SignService;
  @Autowired
  private StoreService storeService;
  @Autowired
  private EvCallEventPublisher publisher;

  @Tx
  @Override
  public String saveNew(String tenant, ExplosiveV2 item, OperateInfo operateInfo) throws BaasException {
    Assert.notNull(item);
    Assert.notBlank(tenant);
    checkLines(item);
    if (StringUtils.isBlank(item.getUuid())) {
      item.setUuid(IdGenUtils.buildRdUuid());
    }
    log.info("??????????????????,??????={},??????ID={}", tenant, item.getUuid());
    //???????????????
    String flowNo = billNumberMgr.generateExplosiveFlowNo(tenant);
    log.info("???????????????,flowNo={}", flowNo);
    item.setFlowNo(flowNo);

    item.setCreateInfo(operateInfo);
    item.setLastModifyInfo(operateInfo);
    //??????????????????
    fetchExt(item);
    //???????????????
    explosiveV2Dao.insert(tenant, item);
    //?????????????????????
    saveLines(tenant, item);
    //????????????????????????
    saveScopes(tenant, item);

    return item.getUuid();
  }

  @Tx
  @Override
  public String saveModify(String tenant, ExplosiveV2 explosiveV2,
      OperateInfo operateInfo) throws BaasException {
    Assert.notNull(explosiveV2);
    Assert.notBlank(tenant);
    checkLines(explosiveV2);
    log.info("??????????????????,??????={},??????ID={}", tenant, explosiveV2.getUuid());
    ExplosiveV2 history = get(tenant, explosiveV2.getUuid(), true);
    if (history == null) {
      throw new BaasException("??????????????????????????????={}???uuid={}", tenant, explosiveV2.getUuid());
    }
    if (!(ExplosiveV2.State.INIT.equals(history.getState())
        || ExplosiveV2.State.SUBMITTED.equals(history.getState())
        || ExplosiveV2.State.REFUSED.equals(history.getState()))) {
      throw new BaasException("?????????????????????????????????????????????={}???????????????={}", tenant, history.getState().name());
    }
    if (ExplosiveV2.State.SUBMITTED.equals(history.getState())
        || ExplosiveV2.State.REFUSED.equals(history.getState())) {
      //?????????????????????
      history.setLines(explosiveV2.getLines());
      checkExplosiveLine(tenant, history);
    }
    explosiveV2.setFlowNo(history.getFlowNo());
    explosiveV2.setState(history.getState());
    explosiveV2.setCreateInfo(history.getCreateInfo());
    explosiveV2.setLastModifyInfo(operateInfo);
    //??????????????????
    fetchExt(explosiveV2);
    //???????????????
    explosiveV2Dao.update(tenant, explosiveV2);
    //?????????????????????,????????????????????????
    explosiveV2LineDao.delete(tenant, explosiveV2.getUuid());
    explosiveScopeDao.delete(tenant, explosiveV2.getUuid());
    //?????????????????????
    saveLines(tenant, explosiveV2);
    //??????????????????????????????
    saveScopes(tenant, explosiveV2);

    return explosiveV2.getUuid();
  }

  @Tx
  @Override
  public String saveNewAndSubmit(String tenant, ExplosiveV2 explosiveV2, OperateInfo operateInfo) throws BaasException {
    Assert.notBlank(tenant);
    Assert.notNull(explosiveV2);
    Assert.notNull(operateInfo);
    //????????????
    String uuid = saveNew(tenant, explosiveV2, operateInfo);
    //??????????????????
    submit(tenant, uuid, operateInfo);
    return uuid;
  }

  @Tx
  @Override
  public String saveModifyAndSubmit(String tenant, ExplosiveV2 explosiveV2, OperateInfo operateInfo) throws BaasException {
    Assert.notBlank(tenant);
    Assert.notNull(explosiveV2);
    Assert.notNull(operateInfo);
    //????????????
    String uuid = saveModify(tenant, explosiveV2, operateInfo);
    //??????????????????
    submit(tenant, uuid, operateInfo);
    return uuid;
  }

  @Tx
  @Override
  public void submit(String tenant, String uuid, OperateInfo operateInfo) throws BaasException {
    log.info("??????????????????,??????={},??????ID={}", tenant, uuid);
    ExplosiveV2 history = get(tenant, uuid, true, ExplosiveV2.PART_LINE);
    if (history == null) {
      throw new BaasException("??????????????????????????????={}???uuid={}", tenant, uuid);
    }
    checkLines(history);
    if (history.getState().equals(ExplosiveV2.State.SUBMITTED)) {
      log.info("???????????????,??????");
      return;
    }
    if (!(history.getState().equals(ExplosiveV2.State.INIT)
        || history.getState().equals(ExplosiveV2.State.REFUSED))) {
      throw new BaasException("?????????????????????????????????????????????={}???????????????={}", tenant, history.getState().name());
    }
    //?????????????????????
    checkExplosiveLine(tenant, history);
    //???????????????
    explosiveV2Dao.updateState(tenant, uuid, ExplosiveV2.State.SUBMITTED.name(), operateInfo);
  }

  @Tx
  @Override
  public void audit(String tenant, String uuid, OperateInfo operateInfo) throws BaasException {
    log.info("??????????????????,??????={},??????ID={}", tenant, uuid);
    ExplosiveV2 history = get(tenant, uuid, true, ExplosiveV2.PART_LINE, ExplosiveV2.PART_SCOPE);
    if (history == null) {
      throw new BaasException("??????????????????????????????={}???uuid={}", tenant, uuid);
    }
    checkLines(history);
    if (history.getState().equals(ExplosiveV2.State.AUDITED)) {
      log.info("???????????????????????????,??????");
      return;
    }
    if (!history.getState().equals(ExplosiveV2.State.SUBMITTED)) {
      throw new BaasException("?????????????????????????????????????????????={}???????????????={}", tenant, history.getState().name());
    }
    //?????????????????????
    checkExplosiveLine(tenant, history);
    ExplosiveV2.State state = ExplosiveV2.State.AUDITED;
    history.setState(state);
    //???????????????
    explosiveV2Dao.updateState(tenant, uuid, state.name(), operateInfo);
    //????????????
    initExplosiveSignByShop(tenant, history, operateInfo);
    // ??????????????????
    pushAuditedMsg(tenant, history);
  }

  @Tx
  @Override
  public void refuse(String tenant, String uuid, OperateInfo operateInfo) throws BaasException {
    log.info("??????????????????,??????={},??????ID={}", tenant, uuid);
    ExplosiveV2 history = explosiveV2Dao.get(tenant, uuid, true);
    if (history == null) {
      throw new BaasException("??????????????????????????????={}???uuid={}", tenant, uuid);
    }
    if (history.getState().equals(ExplosiveV2.State.REFUSED)) {
      log.info("?????????????????????,??????");
      return;
    }
    if (!history.getState().equals(ExplosiveV2.State.SUBMITTED)) {
      throw new BaasException("?????????????????????????????????????????????={}???????????????={}", tenant, history.getState().name());
    }
    //???????????????
    explosiveV2Dao.updateState(tenant, uuid, ExplosiveV2.State.REFUSED.name(), operateInfo);
  }

  @Tx
  @Override
  public void on(String tenant, String uuid, OperateInfo operateInfo) throws BaasException {
    log.info("??????????????????,??????={},??????ID={}", tenant, uuid);
    ExplosiveV2 history = explosiveV2Dao.get(tenant, uuid, true);
    if (history == null) {
      throw new BaasException("??????????????????????????????={}???uuid={}", tenant, uuid);
    }
    if (history.getState().equals(ExplosiveV2.State.ACTIVE)) {
      log.info("?????????????????????,??????");
      return;
    }
    if (history.getState().equals(ExplosiveV2.State.CANCELED)) {
      log.info("?????????????????????,??????");
      return;
    }
    if (!history.getState().equals(ExplosiveV2.State.AUDITED)) {
      throw new BaasException("?????????????????????????????????????????????={}???????????????={}", tenant, history.getState().name());
    }
    if (!history.getSignStartDate().before(new Date())) {
      throw new BaasException("????????????????????????????????????????????????????????????={}???????????????={}", tenant, history.getStartDate());
    }
    ExplosiveV2.State state = ExplosiveV2.State.ACTIVE;
    history.setState(state);
    //???????????????
    explosiveV2Dao.updateState(tenant, uuid, state.name(), operateInfo);
    // ????????????
    pushStartMsg(tenant, history);
    pushOnMsg(tenant, history);
  }

  @Tx
  @Override
  public void off(String tenant, String uuid, OperateInfo operateInfo) throws BaasException {
    log.info("??????????????????,??????={},??????ID={}", tenant, uuid);
    ExplosiveV2 history = explosiveV2Dao.get(tenant, uuid, true);
    if (history == null) {
      throw new BaasException("??????????????????????????????={}???uuid={}", tenant, uuid);
    }
    if (history.getState().equals(ExplosiveV2.State.CANCELED)) {
      log.info("???????????????,??????");
      return;
    }
    boolean off = false;
    if (history.getState().equals(ExplosiveV2.State.AUDITED)) {
      off = true;
    } else if (history.getState().equals(ExplosiveV2.State.ACTIVE)
        && (history.getEndDate().before(new Date()) || history.getStartDate().after(new Date()))) {
      off = true;
    }
    if (!off) {
      throw new BaasException("?????????????????????????????????????????????={}???????????????={}", tenant, history.getState().name());
    }
    ExplosiveV2.State state = ExplosiveV2.State.CANCELED;
    history.setState(state);
    //???????????????
    explosiveV2Dao.updateState(tenant, uuid, state.name(), operateInfo);
    // ?????????????????????????????????????????????????????????
    cancelExplosiveSignByShop(tenant, uuid, operateInfo);
  }

  private void cancelExplosiveSignByShop(String tenant, String uuid, OperateInfo operateInfo) {
    QueryDefinition qd = new QueryDefinition();
    qd.addByField(ExplosiveSignV2.Queries.EXPLOSIVE_ID, Cop.EQUALS, uuid);
    qd.addByField(ExplosiveSignV2.Queries.STATE, Cop.EQUALS, ExplosiveSignV2State.INIT.name());
    QueryResult<ExplosiveSignV2> result = explosiveV2SignService.query(tenant, qd);
    if (CollectionUtils.isEmpty(result.getRecords())) {
      log.info("??????????????????,??????");
      return;
    }
    List<ExplosiveSignV2> explosiveSignV2s = result.getRecords();
    List<String> uuids = explosiveSignV2s.stream().map(ExplosiveSignV2::getUuid).collect(Collectors.toList());
    explosiveV2SignService.cancel(tenant, uuids, operateInfo);
  }

  private void initExplosiveSignByShop(String tenant, ExplosiveV2 item, OperateInfo operateInfo) throws BaasException {
    // ???????????????????????????????????????storeService.query????????????????????????
    List<ExplosiveV2Line> lines = item.getLines();
    List<ExplosiveScope> scopeLines = item.getScopeLines();
    List<ExplosiveSignV2> explosiveSignV2s = new ArrayList<ExplosiveSignV2>();
    if (CollectionUtils.isEmpty(scopeLines)) {
      log.warn("??????????????????,??????");
      return;
    }
    if (CollectionUtils.isEmpty(lines)) {
      log.warn("??????????????????,??????");
      return;
    }
    if ("*".equals(scopeLines.get(0).getOptionName())) {
      StoreFilter filter = new StoreFilter();
      filter.setOrgIdEq(item.getOrgId());
      QueryResult<Store> result = storeService.query(tenant, filter);
      if (CollectionUtils.isEmpty(result.getRecords())) {
        log.warn("??????????????????,??????");
        return;
      }
      scopeLines = new ArrayList<>();
      for (Store i : result.getRecords()) {
        ExplosiveScope scope = new ExplosiveScope();
        scope.setOptionUuid(i.getId());
        scope.setOptionCode(i.getCode());
        scope.setOptionName(i.getName());
        scopeLines.add(scope);
      }
    }
    for (ExplosiveScope scope : scopeLines) {
      ExplosiveSignV2 explosiveSignV2 = new ExplosiveSignV2();
      explosiveSignV2.setTenant(tenant);
      explosiveSignV2.setOrgId(item.getOrgId());
      explosiveSignV2.setExplosiveId(item.getUuid());
      explosiveSignV2.setShop(new UCN(scope.getOptionUuid(), scope.getOptionCode(), scope.getOptionName()));
      explosiveSignV2.setState(ExplosiveSignV2State.INIT);
      explosiveSignV2.setStartDate(item.getStartDate());
      explosiveSignV2.setEndDate(item.getEndDate());
      explosiveSignV2.setSignStartDate(item.getSignStartDate());
      explosiveSignV2.setSignEndDate(item.getSignEndDate());
      List<ExplosiveSignV2Line> explosiveSignV2Lines = new ArrayList<ExplosiveSignV2Line>();
      for (ExplosiveV2Line line : lines) {
        ExplosiveSignV2Line explosiveSignV2Line = new ExplosiveSignV2Line();
        explosiveSignV2Line.setTenant(tenant);
        explosiveSignV2Line.setOwner(line.getOwner());
        explosiveSignV2Line.setSkuId(line.getSkuId());
        explosiveSignV2Line.setSkuCode(line.getSkuCode());
        explosiveSignV2Line.setSkuGid(line.getSkuGid());
        explosiveSignV2Line.setSkuName(line.getSkuName());
        explosiveSignV2Line.setSkuQpc(line.getSkuQpc());
        explosiveSignV2Line.setSkuUnit(line.getSkuUnit());
        explosiveSignV2Line.setInPrice(line.getInPrice());
        explosiveSignV2Line.setQty(BigDecimal.ZERO);
        explosiveSignV2Lines.add(explosiveSignV2Line);
      }
      explosiveSignV2.setLines(explosiveSignV2Lines);
      explosiveSignV2s.add(explosiveSignV2);
    }
    explosiveV2SignService.batchSaveNew(tenant, explosiveSignV2s, operateInfo);
  }

  @Tx
  @Override
  public void delete(String tenant, String uuid) throws BaasException {
    log.info("??????????????????,??????={},??????ID={}", tenant, uuid);
    ExplosiveV2 history = explosiveV2Dao.get(tenant, uuid, true);
    if (history == null) {
      throw new BaasException("??????????????????????????????={}???uuid={}", tenant, uuid);
    }
    if (!(history.getState().equals(ExplosiveV2.State.INIT)
        || history.getState().equals(ExplosiveV2.State.REFUSED))) {
      throw new BaasException("?????????????????????????????????????????????={}???????????????={}", tenant, history.getState().name());
    }
    explosiveV2Dao.delete(tenant, uuid);
    explosiveV2LineDao.delete(tenant, uuid);
    explosiveScopeDao.delete(tenant, uuid);
  }

  @Override
  public ExplosiveV2 get(String tenant, String uuid, boolean forUpdate, String... fetchParts) {
    Assert.notNull(tenant, "tenant");
    Assert.notNull(uuid, "uuid");

    ExplosiveV2 explosiveV2 = explosiveV2Dao.get(tenant, uuid, forUpdate);
    if (explosiveV2 != null) {
      List<String> fetchList = Arrays.asList(fetchParts);
      if (fetchList.contains(ExplosiveV2.PART_LINE)) {
        explosiveV2.setLines(explosiveV2LineDao.listByOwner(tenant, uuid));
      }
      if (fetchList.contains(ExplosiveV2.PART_SCOPE)) {
        explosiveV2.setScopeLines(explosiveScopeDao.listByOwner(tenant, uuid));
      }
    }
    return explosiveV2;
  }

  @Override
  public QueryResult<ExplosiveV2> query(String tenant, QueryDefinition qd, String... fetchParts) {
    QueryResult<ExplosiveV2> result = explosiveV2Dao.query(tenant, qd);
    if (fetchParts == null || fetchParts.length == 0) {
      return result;
    }
    //????????????
    fetch(tenant, result.getRecords(), fetchParts);
    return result;
  }

  @Override
  public List<ExplosiveV2> list(String tenant, List<String> uuids) {
    Assert.notNull(tenant, "tenant");
    if (CollectionUtils.isEmpty(uuids)) {
      return null;
    }
    return explosiveV2Dao.list(tenant, uuids);
  }

  @Tx
  @Override
  public void incrLineLimit(String tenant, String uuid, List<LineLimitIncrInfo> infos) throws BaasException {
    Assert.notNull(tenant);
    Assert.notNull(uuid);
    Assert.notEmpty(infos);
    Map<String, LineLimitIncrInfo> infoMap = infos.stream().collect(Collectors.toMap(LineLimitIncrInfo::getSkuId, info -> info));
    Set<String> skuIds = infoMap.keySet();
    List<ExplosiveV2Line> lines = explosiveV2LineDao.listBySkuIds(tenant, uuid, skuIds);
    if (lines == null) {
      throw new BaasException("??????????????????,??????={},??????ID={},skuIDs={}", tenant, uuid, skuIds);
    }
    for (ExplosiveV2Line line : lines) {
      BigDecimal qty = infoMap.get(line.getSkuId()).getQty();
      log.info("???????????????skuID={},????????????????????????={},????????????={}", line.getSkuId(), line.getUsedLimit(), qty);
      line.setUsedLimit(line.getUsedLimit().add(qty));
    }
    explosiveV2LineDao.batchUpdate(tenant, uuid, lines);
  }

  @Tx
  @Override
  public void saveLimitQty(String tenant, String uuid, List<ExplosiveV2Line> lines, List<String> explosiveSignIds, OperateInfo operateInfo) throws BaasException {
    ExplosiveV2 explosiveV2 = get(tenant, uuid, true, ExplosiveV2.PART_LINE);
    List<ExplosiveV2.State> allowChangeLines = Lists.newArrayList(ExplosiveV2.State.SUBMITTED, ExplosiveV2.State.REFUSED, ExplosiveV2.State.AUDITED, ExplosiveV2.State.ACTIVE);
    if (!allowChangeLines.contains(explosiveV2.getState())) {
      throw new BaasException("?????????????????????????????????????????????,??????id={},??????id={},????????????={}", tenant, uuid, explosiveV2.getState());
    }
    saveLineLimitQty(tenant, uuid, lines, explosiveV2);
    explosiveV2.setLastModifyInfo(operateInfo);
    explosiveV2.setVersion(explosiveV2.getVersion() + 1L);
    explosiveV2Dao.update(tenant, explosiveV2);
    if (explosiveV2.getStartDate().before(new Date())) {
      pushExplosiveMessage(tenant, uuid, explosiveSignIds);
    }
  }

  public void saveLineLimitQty(String tenant, String uuid, List<ExplosiveV2Line> lines, ExplosiveV2 explosiveV2) {
    Map<String, ExplosiveV2Line> lineMap = lines.stream().collect(Collectors.toMap(ExplosiveV2Line::getUuid, line -> line));
    for (ExplosiveV2Line line : explosiveV2.getLines()) {
      if (lineMap.keySet().contains(line.getUuid())) {
        line.setLimitQty(lineMap.get(line.getUuid()).getLimitQty());
      }
    }
    explosiveV2LineDao.batchUpdate(tenant, uuid, explosiveV2.getLines());
  }

  private void pushExplosiveMessage(String tenant, String explosiveId, List<String> signIds) {
    ExplosiveV2EvCallMsg msg = new ExplosiveV2EvCallMsg();
    msg.setTenant(tenant);
    msg.setExplosiveSignIds(signIds);
    msg.setExplosiveId(explosiveId);
    publisher.publishForNormal(ExplosiveV2EvCallExecutor.EXPLOSIVE_V2_EXECUTOR_ID, msg);
  }

  /**
   * ????????????????????????
   *
   * @param tenant
   *     ??????
   * @param item
   *     ????????????
   */
  private void saveScopes(String tenant, ExplosiveV2 item) {
    if (CollectionUtils.isEmpty(item.getScopeLines())) {
      return;
    }
    int lineNo = 1;
    for (ExplosiveScope line : item.getScopeLines()) {
      line.setOwner(item.getUuid());
      line.setLineNo(lineNo++);
    }
    explosiveScopeDao.insert(tenant, item.getScopeLines());
  }

  /**
   * ????????????????????????
   *
   * @param item
   *     ????????????
   */
  private void fetchExt(ExplosiveV2 item) {
    Map<String, String> map = new HashMap<>();
    if (CollectionUtils.isNotEmpty(item.getLines())) {
      List<ExplosiveV2Line> lines = item.getLines();
      map.put("sku_info", lines.get(0).getSkuName() + "???" + lines.size() + "?????????");
    }
    List<ExplosiveScope> scopes = item.getScopeLines();
    if (CollectionUtils.isNotEmpty(scopes)) {
      if ("*".equals(scopes.get(0).getOptionName())) {
        map.put("shop_info", "????????????");
      } else {
        map.put("shop_info", scopes.get(0).getOptionName() + "???" + scopes.size() + "?????????");
      }
    }
    log.info("????????????????????????????????????,sku_info={},shop_info={}", map.get("sku_info"), map.get("shop_info"));
    String ext = JsonUtil.objectToJson(map);
    item.setExt(ext);
  }

  /**
   * ?????????????????????
   *
   * @param tenant
   *     ??????
   * @param item
   *     ????????????
   */
  private void saveLines(String tenant, ExplosiveV2 item) {
    if (CollectionUtils.isEmpty(item.getLines())) {
      return;
    }
    int lineNo = 1;
    for (ExplosiveV2Line line : item.getLines()) {
      line.setOwner(item.getUuid());
      line.setLineNo(lineNo++);
    }
    explosiveV2LineDao.insert(tenant, item.getLines());
  }

  /**
   * ????????????
   *
   * @param tenant
   *     ??????
   * @param item
   *     ??????????????????
   * @param fetchParts
   *     ??????????????????
   */
  private void fetch(String tenant, List<ExplosiveV2> item, String... fetchParts) {
    if (CollectionUtils.isEmpty(item)) {
      return;
    }
    List<String> fetchList = Arrays.asList(fetchParts);
    List<String> explosiveIds = item.stream().map(ExplosiveV2::getUuid).collect(Collectors.toList());
    Map<String, List<ExplosiveV2Line>> lineMap = new HashMap<>();
    Map<String, List<ExplosiveScope>> scopeMap = new HashMap<>();
    if (fetchList.contains(ExplosiveV2.PART_LINE)) {
      List<ExplosiveV2Line> lines = explosiveV2LineDao.listByOwners(tenant, explosiveIds);
      if (CollectionUtils.isNotEmpty(lines)) {
        lineMap = lines.stream().collect(Collectors.groupingBy(ExplosiveV2Line::getOwner));
      }
    }
    if (fetchList.contains(ExplosiveV2.PART_SCOPE)) {
      List<ExplosiveScope> scopes = explosiveScopeDao.listByOwners(tenant, explosiveIds);
      if (CollectionUtils.isNotEmpty(scopes)) {
        scopeMap = scopes.stream().collect(Collectors.groupingBy(ExplosiveScope::getOwner));
      }
    }
    for (ExplosiveV2 explosiveV2 : item) {
      explosiveV2.setLines(lineMap.get(explosiveV2.getUuid()));
      explosiveV2.setScopeLines(scopeMap.get(explosiveV2.getUuid()));
    }
  }

  /**
   * ?????????????????????
   */
  private void checkLines(ExplosiveV2 item) throws BaasException {
    if (CollectionUtils.isEmpty(item.getLines())) {
      throw new BaasException("????????????????????????");
    }
    for (ExplosiveV2Line line : item.getLines()) {
      if (line.getSkuName() == null) {
        throw new BaasException("?????????????????????");
      }
      if (line.getSkuCode() == null) {
        throw new BaasException("????????????????????????");
      }
      if (line.getSkuQpc() == null) {
        throw new BaasException("????????????????????????");
      }
      if (line.getSkuUnit() == null) {
        throw new BaasException("????????????????????????");
      }
      if (line.getInPrice() == null) {
        throw new BaasException("???????????????????????????");
      }
      if (line.getLimitQty() == null) {
        throw new BaasException("????????????????????????");
      }
      if (line.getMinQty() == null) {
        throw new BaasException("???????????????????????????");
      }
      if (line.getLimitQty().compareTo(BigDecimal.ZERO) > 0 && line.getMinQty().compareTo(line.getLimitQty()) > 0) {
        throw new BaasException("?????????????????????????????????????????????");
      }
    }
  }

  private void checkExplosiveLine(String tenant, ExplosiveV2 history) throws BaasException {
    if (CollectionUtils.isEmpty(history.getLines())) {
      log.info("?????????????????????,???????????????");
      return;
    }
    log.info("???????????????????????????,?????????????????????,?????????????????????????????????????????????");
    //???????????????????????????????????????skuId
    List<String> skuIds = history.getLines().stream().map(ExplosiveV2Line::getSkuId).collect(Collectors.toList());
    List<String> skuGids = history.getLines().stream().map(ExplosiveV2Line::getSkuGid).collect(Collectors.toList());
    boolean isRepeatQpc = skuGids.size() != new HashSet<>(skuGids).size();
    if (isRepeatQpc) {
      throw new BaasException("?????????????????????????????????????????????????????????????????????");
    }
    //select sku_id
    //from explosive_v2_line
    //where owner in (select uuid
    //                from explosive_v2
    //                where explosive_v2.state in ('SUBMITTED', 'AUDITED', 'ACTIVE')
    //                  and NOT (
    //                        (end_date < history.getStartDate())
    //                        OR (start_date > history.getEndDate())
    //                    )) group by sku_id;

    //?????????,???????????????,????????????,????????????????????????(?????????????????????????????????)
    QueryDefinition qd = new QueryDefinition();
    qd.addByField(ExplosiveV2.Queries.STATE, Cop.IN,
        ExplosiveV2.State.SUBMITTED.name(), ExplosiveV2.State.AUDITED.name(), ExplosiveV2.State.ACTIVE.name());
    qd.addByOperation(ExplosiveV2.Queries.ACTIVE_DATE_BTW, history.getStartDate(), history.getEndDate());
    qd.addByField(ExplosiveV2.Queries.ORG_ID, Cop.EQUALS, history.getOrgId());
    QueryResult<ExplosiveV2> result = explosiveV2Dao.query(tenant, qd);

    if (CollectionUtils.isNotEmpty(result.getRecords())) {
      List<String> uuids = result.getRecords().stream().map(ExplosiveV2::getUuid).filter(i -> !history.getUuid().equals(i)).collect(Collectors.toList());
      if (CollectionUtils.isEmpty(uuids)) {
        log.info("????????????????????????,?????????????????????");
        return;
      }
      Map<String, String> uuidMapFlowNo = result.getRecords()
          .stream()
          .filter(i -> !history.getUuid().equals(i.getUuid()))
          .collect(Collectors.toMap(ExplosiveV2::getUuid, ExplosiveV2::getFlowNo));
      //??????????????????ID?????????????????????
      List<ExplosiveV2Line> lines = explosiveV2LineDao.listByOwners(tenant, uuids);
      if (CollectionUtils.isNotEmpty(lines)) {
        //?????????????????????????????????skuId???????????????
        Map<String, List<ExplosiveV2Line>> lineMap = lines.stream().collect(Collectors.groupingBy(ExplosiveV2Line::getSkuId));
        List<String> conflictSkuIds = skuIds.stream().filter(lineMap::containsKey).collect(Collectors.toList());
        if (CollectionUtils.isNotEmpty(conflictSkuIds)) {
          List<String> conflictSkuNames = new ArrayList<>();
          List<String> conflictFlowNos = new ArrayList<>();
          for (String conflictSkuId : conflictSkuIds) {
            for (ExplosiveV2Line line : lineMap.get(conflictSkuId)) {
              String skuName = line.getSkuName();
              if (!conflictSkuNames.contains(skuName)) {
                conflictSkuNames.add(skuName);
              }
              String conflictFlowNo = uuidMapFlowNo.get(line.getOwner());
              if (!conflictFlowNos.contains(conflictFlowNo)) {
                conflictFlowNos.add(conflictFlowNo);
              }
            }
          }
          throw new BaasException("???????????????????????????????????????????????????????????????????????????,??????????????????={},????????????={}", conflictFlowNos, conflictSkuNames);
        }
      }
    }
    log.info("????????????????????????,?????????????????????");
  }


  private void pushAuditedMsg(String tenant, ExplosiveV2 item) {
    ExplosivePrepareOnMsg msg = new ExplosivePrepareOnMsg();
    msg.setTenant(tenant);
    msg.setUuid(item.getUuid());
    msg.setAction(ExplosiveActionV2.ON);
    msg.setOperateInfo(getSysOperateInfo());
    msg.setTraceId(IdGenUtils.buildIidAsString());
    // ????????????
    log.info("?????????????????????????????????????????????ID={},??????????????????????????????={}", msg.getUuid(), DateUtil.toDateStr(item.getSignStartDate()));
    publisher.publishForNormal(ExplosivePrepareOnOffEvCallExecutor.EXECUTOR_ID, msg, item.getSignStartDate());
  }

  private void pushOnMsg(String tenant, ExplosiveV2 item) {
    ExplosivePrepareOnMsg msg = new ExplosivePrepareOnMsg();
    msg.setTenant(tenant);
    msg.setUuid(item.getUuid());
    msg.setAction(ExplosiveActionV2.OFF);
    msg.setOperateInfo(getSysOperateInfo());
    msg.setTraceId(IdGenUtils.buildIidAsString());
    // ????????????
    log.info("?????????????????????????????????????????????ID={},??????????????????????????????={}", msg.getUuid(), DateUtil.toDateStr(item.getEndDate()));
    publisher.publishForNormal(ExplosivePrepareOnOffEvCallExecutor.EXECUTOR_ID, msg, item.getEndDate());
  }

  private void pushStartMsg(String tenant, ExplosiveV2 item) {
    ExplosiveSignAutoEndMsg msg = new ExplosiveSignAutoEndMsg();
    msg.setTenant(tenant);
    msg.setUuids(Arrays.asList(item.getUuid()));
    msg.setTraceId(IdGenUtils.buildIidAsString());
    // ????????????
    log.info("?????????????????????????????????????????????ID={},??????????????????????????????={}", msg.getUuids(), DateUtil.toDateStr(item.getStartDate()));
    publisher.publishForNormal(ExplosiveAutoStartEvCallExecutor.EXPLOSIVE_AUTO_START_EXECUTOR_ID, msg, item.getStartDate());
  }

  protected OperateInfo getSysOperateInfo() {
    OperateInfo operateInfo = new OperateInfo();
    Operator operator = new Operator();
    operateInfo.setTime(new Date());
    operator.setId("system");
    operator.setFullName("????????????");
    operator.setNamespace("system");
    operateInfo.setOperator(operator);
    return operateInfo;
  }
}
