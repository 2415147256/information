package com.hd123.baas.sop.evcall.exector.explosivev2;

import com.hd123.baas.sop.annotation.Tx;
import com.hd123.baas.sop.service.api.explosivev2.ExplosiveV2;
import com.hd123.baas.sop.service.api.explosivev2.ExplosiveV2Service;
import com.hd123.baas.sop.service.api.explosivev2.sign.ExplosiveV2SignService;
import com.hd123.baas.sop.evcall.AbstractEvCallExecutor;
import com.hd123.baas.sop.utils.BaasJSONUtil;
import com.hd123.baas.sop.utils.SopUtils;
import com.hd123.rumba.commons.biz.entity.Entity;
import com.hd123.rumba.evcall.EvCallExecutionContext;
import com.qianfan123.baas.common.BaasException;
import com.qianfan123.baas.common.util.JSONUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author shenmin
 */
@Slf4j
@Component
public class ExplosiveAutoStartEvCallExecutor extends AbstractEvCallExecutor<ExplosiveSignAutoEndMsg> {

  public static final String EXPLOSIVE_AUTO_START_EXECUTOR_ID = ExplosiveAutoStartEvCallExecutor.class.getSimpleName();

  @Autowired
  private ExplosiveV2SignService signService;
  @Autowired
  private ExplosiveV2Service explosiveV2Service;

  @Override
  @Tx
  protected void doExecute(ExplosiveSignAutoEndMsg msg, EvCallExecutionContext context) throws Exception {
    String tenant = msg.getTenant();
    if (StringUtils.isEmpty(tenant) || CollectionUtils.isEmpty(msg.getUuids())) {
      log.warn("关键数据为空，忽略");
      return;
    }
    // 先用校验下状态
    List<ExplosiveV2> list = explosiveV2Service.list(tenant, msg.getUuids());
    if (CollectionUtils.isEmpty(list)) {
      log.warn("查询的集合数据为空，忽略，uuids={}", JSONUtil.safeToJson(msg.getUuids()));
      return;
    }
    List<String> uuids = list.stream()
        .filter(i -> i.getState().equals(ExplosiveV2.State.ACTIVE))
        .map(Entity::getUuid)
        .collect(Collectors.toList());
    log.info("过滤前后的数据，msg.uuids={}, uuids={}", JSONUtil.safeToJson(msg.getUuids()), JSONUtil.safeToJson(uuids));
    signService.setFinish(tenant, uuids, SopUtils.getSysOperateInfo());
  }

  @Override
  protected ExplosiveSignAutoEndMsg decodeMessage(String msg) throws BaasException {
    log.info("收到ExplosiveStartMsg:{}", msg);
    return BaasJSONUtil.safeToObject(msg, ExplosiveSignAutoEndMsg.class);
  }
}
