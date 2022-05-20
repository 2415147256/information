package com.hd123.baas.sop.service.impl.explosivev2.plan;

import com.hd123.baas.sop.annotation.Tx;
import com.hd123.baas.sop.service.api.explosivev2.plan.ExplosivePlanV2;
import com.hd123.baas.sop.service.api.explosivev2.plan.ExplosivePlanV2Line;
import com.hd123.baas.sop.service.api.explosivev2.plan.ExplosivePlanV2Service;
import com.hd123.baas.sop.service.impl.price.BillNumberMgr;
import com.hd123.baas.sop.service.dao.explosivev2.plan.ExplosivePlanV2Dao;
import com.hd123.baas.sop.service.dao.explosivev2.plan.ExplosivePlanV2LineDao;
import com.hd123.baas.sop.utils.JsonUtil;
import com.hd123.rumba.commons.biz.entity.OperateInfo;
import com.hd123.rumba.commons.biz.query.QueryDefinition;
import com.hd123.rumba.commons.biz.query.QueryResult;
import com.hd123.rumba.commons.lang.Assert;
import com.qianfan123.baas.common.BaasException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * @author shenmin
 */
@Service
@Slf4j
public class ExplosivePlanV2ServiceImpl implements ExplosivePlanV2Service {
  @Autowired
  private ExplosivePlanV2Dao explosivePlanV2Dao;
  @Autowired
  private ExplosivePlanV2LineDao explosivePlanV2LineDao;
  @Autowired
  private BillNumberMgr billNumberMgr;

  @Tx
  @Override
  public String saveNew(String tenant, ExplosivePlanV2 explosivePlanV2, OperateInfo operateInfo) throws BaasException {
    Assert.notNull(explosivePlanV2);
    Assert.notBlank(tenant);
    checkLines(explosivePlanV2);
    if (StringUtils.isEmpty(explosivePlanV2.getUuid())) {
      explosivePlanV2.setUuid(UUID.randomUUID().toString());
    }
    log.info("新增爆品计划,租户={},爆品计划ID={}", tenant, explosivePlanV2.getUuid());
    //获取订单号
    String flowNo = billNumberMgr.generateExplosivePlanFlowNo(tenant);
    log.info("构建的单号,flowNo={}", flowNo);
    explosivePlanV2.setFlowNo(flowNo);
    explosivePlanV2.setCreateInfo(operateInfo);
    explosivePlanV2.setLastModifyInfo(operateInfo);
    fetchExt(explosivePlanV2);
    //插入数据库
    explosivePlanV2Dao.insert(tenant, explosivePlanV2);
    //保存爆品计划行
    saveLines(tenant, explosivePlanV2);
    // 返回
    return explosivePlanV2.getUuid();
  }

  @Tx
  @Override
  public String saveModify(String tenant, ExplosivePlanV2 explosivePlanV2, OperateInfo operateInfo) throws BaasException {
    log.info("修改爆品计划,租户={},爆品计划ID={}", tenant, explosivePlanV2.getUuid());
    Assert.notBlank(tenant);
    Assert.notNull(explosivePlanV2);
    Assert.notNull(explosivePlanV2.getUuid());
    checkLines(explosivePlanV2);

    // 获取详情
    ExplosivePlanV2 history = get(tenant, explosivePlanV2.getUuid());
    // 判断
    if (history == null) {
      throw new BaasException("爆品活动计划不存在，租户={}，uuid={}", tenant, explosivePlanV2.getUuid());
    }
    if (ExplosivePlanV2.State.DISABLE.equals(history.getState())) {
      throw new BaasException("当前爆品活动计划状态不可编辑，租户={}，当前状态={}", tenant, history.getState().name());
    }
    // 替换不可更新值
    explosivePlanV2.setFlowNo(history.getFlowNo());
    explosivePlanV2.setState(history.getState());
    explosivePlanV2.setCreateInfo(history.getCreateInfo());
    explosivePlanV2.setLastModifyInfo(operateInfo);
    fetchExt(explosivePlanV2);
    //更新数据库
    explosivePlanV2Dao.update(tenant, explosivePlanV2);
    //先删除商品行家旧数据,再新增
    explosivePlanV2LineDao.delete(tenant, explosivePlanV2.getUuid());
    //保存爆品计划行
    saveLines(tenant, explosivePlanV2);

    return explosivePlanV2.getUuid();
  }

  @Tx
  @Override
  public String saveNewAndEnable(String tenant, ExplosivePlanV2 explosivePlanV2, OperateInfo operateInfo) throws BaasException {
    Assert.notBlank(tenant);
    Assert.notNull(explosivePlanV2);
    Assert.notNull(operateInfo);
    //保存新增
    String uuid = saveNew(tenant, explosivePlanV2, operateInfo);
    //启用爆品计划
    enable(tenant, uuid, operateInfo);
    return uuid;
  }

  @Tx
  @Override
  public String saveModifyAndEnable(String tenant, ExplosivePlanV2 explosivePlanV2, OperateInfo operateInfo) throws BaasException {
    Assert.notBlank(tenant);
    Assert.notNull(explosivePlanV2);
    Assert.notNull(operateInfo);
    //保存修改
    String uuid = saveModify(tenant, explosivePlanV2, operateInfo);
    //启用爆品计划
    enable(tenant, uuid, operateInfo);
    return uuid;
  }


  @Tx
  @Override
  public void enable(String tenant, String uuid, OperateInfo operateInfo) throws BaasException {
    log.info("启用爆品计划,租户={},爆品计划ID={}", tenant, uuid);
    ExplosivePlanV2 history = get(tenant, uuid, ExplosivePlanV2.PART_LINE);
    // 判断
    if (history == null) {
      throw new BaasException("爆品活动计划不存在，租户={}，uuid={}", tenant, uuid);
    }
    checkLines(history);
    if (ExplosivePlanV2.State.ENABLE.equals(history.getState())) {
      return;
    }
    history.setState(ExplosivePlanV2.State.ENABLE);
    history.setLastModifyInfo(operateInfo);
    //更新爆品计划
    explosivePlanV2Dao.update(tenant, history);
  }

  @Tx
  @Override
  public void disable(String tenant, String uuid, OperateInfo operateInfo) throws BaasException {
    log.info("禁用爆品计划,租户={},爆品计划ID={}", tenant, uuid);
    ExplosivePlanV2 history = explosivePlanV2Dao.get(tenant, uuid, true);
    // 判断
    if (history == null) {
      throw new BaasException("爆品活动计划不存在，租户={}，uuid={}", tenant, uuid);
    }
    if (ExplosivePlanV2.State.INIT.equals(history.getState())) {
      throw new BaasException("当前爆品活动计划不可禁用，租户={}，状态={}", tenant, history.getState().name());
    }
    if (ExplosivePlanV2.State.DISABLE.equals(history.getState())) {
      return;
    }
    history.setState(ExplosivePlanV2.State.DISABLE);
    history.setLastModifyInfo(operateInfo);
    //更新爆品计划
    explosivePlanV2Dao.update(tenant, history);
  }

  @Tx
  @Override
  public void delete(String tenant, String uuid) throws BaasException {
    log.info("删除爆品计划,租户={},爆品计划ID={}", tenant, uuid);
    ExplosivePlanV2 history = explosivePlanV2Dao.get(tenant, uuid, true);
    // 判断
    if (history == null) {
      throw new BaasException("爆品活动计划不存在，租户={}，uuid={}", tenant, uuid);
    }
    if (!ExplosivePlanV2.State.INIT.equals(history.getState())) {
      throw new BaasException("当前爆品活动计划不可删除，租户={}，状态={}", tenant, history.getState().name());
    }
    explosivePlanV2Dao.delete(tenant, uuid);
    explosivePlanV2LineDao.delete(tenant, uuid);
  }

  @Override
  public ExplosivePlanV2 get(String tenant, String uuid, String... fetchParts) {
    log.info("查询爆品计划详情,租户={},爆品计划ID={}", tenant, uuid);
    ExplosivePlanV2 explosivePlanV2 = explosivePlanV2Dao.get(tenant, uuid, true);
    if (explosivePlanV2 == null) {
      return null;
    }
    // 补充数据
    List<String> fetchList = Arrays.asList(fetchParts);
    if (fetchList.contains(ExplosivePlanV2.PART_LINE)) {
      explosivePlanV2.setLines(explosivePlanV2LineDao.listByOwner(tenant, uuid));
    }
    return explosivePlanV2;
  }

  @Override
  public QueryResult<ExplosivePlanV2> query(String tenant, QueryDefinition qd) {
    Assert.notBlank(tenant);
    log.info("构建的查询条件,qd={}", qd.getCondition());
    QueryResult<ExplosivePlanV2> result = explosivePlanV2Dao.query(tenant, qd);
    // 不处理fetch_part
    return result;
  }

  /**
   * 获取额外信息
   */
  private void fetchExt(ExplosivePlanV2 explosivePlanV2) {
    if (CollectionUtils.isNotEmpty(explosivePlanV2.getLines())) {
      List<ExplosivePlanV2Line> lines = explosivePlanV2.getLines();
      Map<String, String> map = new HashMap<>();
      map.put("sku_info", lines.get(0).getSkuName() + "等" + lines.size() + "个商品");
      log.info("获取的商品信息,sku_info={}", map.get("sku_info"));
      String ext = JsonUtil.objectToJson(map);
      explosivePlanV2.setExt(ext);
    }
  }

  /**
   * 保存爆品计划行
   */
  private void saveLines(String tenant, ExplosivePlanV2 explosivePlanV2) {
    if (CollectionUtils.isEmpty(explosivePlanV2.getLines())) {
      return;
    }
    int lineNo = 1;
    for (ExplosivePlanV2Line line : explosivePlanV2.getLines()) {
      line.setOwner(explosivePlanV2.getUuid());
      line.setLineNo(lineNo++);
    }
    explosivePlanV2LineDao.insert(tenant, explosivePlanV2.getLines());
  }

  /**
   * 校验爆品活动计划行
   */
  private void checkLines(ExplosivePlanV2 item) throws BaasException {
    if (CollectionUtils.isEmpty(item.getLines())) {
      throw new BaasException("爆品商品不能为空");
    }
    for (ExplosivePlanV2Line line : item.getLines()) {
      if (line.getSkuName() == null) {
        throw new BaasException("商品名不能为空");
      }
      if (line.getSkuCode() == null) {
        throw new BaasException("商品代码不能为空");
      }
      if (line.getSkuQpc() == null) {
        throw new BaasException("商品规格不能为空");
      }
      if (line.getSkuUnit() == null) {
        throw new BaasException("商品单位不能为空");
      }
      if (line.getInPrice() == null) {
        throw new BaasException("商品活动订货价必填");
      }
      if (line.getLimitQty() == null) {
        throw new BaasException("商品是否限量必填");
      }
      if (line.getMinQty() == null) {
        throw new BaasException("商品最低报名量必填");
      }

      if (line.getLimitQty().compareTo(BigDecimal.ZERO) > 0 && line.getMinQty().compareTo(line.getLimitQty()) > 0) {
        throw new BaasException("门店最小起订量要小于等于总限量");
      }
    }
  }
}
