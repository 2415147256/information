package com.hd123.baas.sop.evcall.exector.explosivev2;

import com.hd123.baas.sop.service.api.explosivev2.ExplosiveV2;
import com.hd123.baas.sop.service.api.explosivev2.ExplosiveV2Line;
import com.hd123.baas.sop.service.api.explosivev2.sign.ExplosiveSignV2;
import com.hd123.baas.sop.service.api.explosivev2.sign.ExplosiveSignV2Line;
import com.hd123.baas.sop.service.impl.explosivev2.ExplosiveV2ServiceImpl;
import com.hd123.baas.sop.service.impl.explosivev2.sign.ExplosiveV2SignServiceImpl;
import com.hd123.baas.sop.configuration.FeignClientMgr;
import com.hd123.baas.sop.evcall.AbstractEvCallExecutor;
import com.hd123.baas.sop.remote.rsh6sop.RsH6SOPClient;
import com.hd123.baas.sop.remote.rsh6sop.explosivev2.HotActivity;
import com.hd123.baas.sop.remote.rsh6sop.explosivev2.HotActivityDtl;
import com.hd123.baas.sop.utils.BaasJSONUtil;
import com.hd123.baas.sop.utils.JsonUtil;
import com.hd123.rumba.evcall.EvCallExecutionContext;
import com.qianfan123.baas.common.BaasException;
import com.qianfan123.baas.common.http2.BaasResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 爆品活动推送
 *
 * @author liuhaoxin
 * @date 2021-12-7
 */
@Slf4j
@Component
public class ExplosiveV2EvCallExecutor extends AbstractEvCallExecutor<ExplosiveV2EvCallMsg> {

  public static final String EXPLOSIVE_V2_EXECUTOR_ID = ExplosiveV2EvCallExecutor.class.getSimpleName();
  public static final DecimalFormat DECIMAL_FORMAT = new DecimalFormat("##########.##########");

  @Autowired
  private FeignClientMgr feignClientMgr;
  @Autowired
  private ExplosiveV2SignServiceImpl explosiveV2SignService;
  @Autowired
  private ExplosiveV2ServiceImpl explosiveV2Service;

  @Override
  protected void doExecute(ExplosiveV2EvCallMsg message, EvCallExecutionContext context) throws Exception {
    log.info("推送爆品活动消息->h6");
    String tenant = message.getTenant();
    String explosiveId = message.getExplosiveId();
    List<String> explosiveSignIds = message.getExplosiveSignIds();

    // 查询：爆品活动
    ExplosiveV2 explosiveV2 = explosiveV2Service.get(tenant, explosiveId, false, ExplosiveV2.PART_LINE);
    // 查询：爆品活动报名信息
    List<ExplosiveSignV2> signs = explosiveV2SignService.list(tenant, explosiveSignIds, ExplosiveSignV2.FETCH_LINE);

    Map<String, List<ExplosiveSignV2>> signMap = new HashMap<>();
    if (CollectionUtils.isNotEmpty(signs)) {
      signMap = signs.stream().collect(Collectors.groupingBy(ExplosiveSignV2::getExplosiveId));
    }

    List<ExplosiveSignV2> singleExplosiveSigns = signMap.get(explosiveV2.getUuid());
    if (CollectionUtils.isNotEmpty(singleExplosiveSigns)) {
      HotActivity hotActivity = buildHotActivity(explosiveV2, singleExplosiveSigns);
      BaasResponse<Void> result = getH6Client(tenant).explosiveOn(tenant, hotActivity);
      if (!result.isSuccess()) {
        log.error("爆品活动上架推送H6失败！，result={}", JsonUtil.objectToJson(result));
        throw new BaasException("爆品活动上架推送给H6失败！");
      }
    }
  }

  private HotActivity buildHotActivity(ExplosiveV2 explosive, List<ExplosiveSignV2> singleExplosiveSigns) {
    HotActivity hotActivity = new HotActivity();

    // 爆品活动单号flowNo
    hotActivity.setActivityId(explosive.getFlowNo());
    hotActivity.setBeginDate(explosive.getStartDate());
    hotActivity.setEndDate(explosive.getEndDate());
    hotActivity.setLstupdTime(explosive.getLastModifyInfo().getTime());
    hotActivity.setOrgGid(explosive.getOrgId());

    List<ExplosiveV2Line> lines = explosive.getLines();
    Map<String, ExplosiveV2Line> lineMap = lines.stream().collect(Collectors.toMap(ExplosiveV2Line::getSkuId, o -> o));

    List<HotActivityDtl> storeGoodss = buildHotActivityDtls(singleExplosiveSigns, lineMap);
    hotActivity.setStoreGoodss(storeGoodss);
    return hotActivity;
  }

  private List<HotActivityDtl> buildHotActivityDtls(List<ExplosiveSignV2> singleExplosiveSigns, Map<String, ExplosiveV2Line> lineMap) {
    List<HotActivityDtl> storeGoodss = new ArrayList<>();

    for (ExplosiveSignV2 sign : singleExplosiveSigns) {
      List<ExplosiveSignV2Line> lines = sign.getLines();
      for (ExplosiveSignV2Line signLine : lines) {
        ExplosiveV2Line line = lineMap.get(signLine.getSkuId());
        HotActivityDtl dtl = new HotActivityDtl();
        dtl.setGdGid(line.getSkuGid());
        if (line.getLimitQty().compareTo(BigDecimal.ZERO) == 0) {
          dtl.setIsLimit(0);
        } else {
          dtl.setIsLimit(1);
        }
        dtl.setMinQty(BigDecimal.ZERO);

        dtl.setPrice(signLine.getInPrice());
        dtl.setQpc(signLine.getSkuQpc());
        dtl.setQpcStr("1*" + DECIMAL_FORMAT.format(signLine.getSkuQpc()));
        dtl.setQty(signLine.getQty().multiply(signLine.getSkuQpc()));
        dtl.setMunit(signLine.getSkuUnit());

        dtl.setStoreGid(sign.getShop().getUuid());

        storeGoodss.add(dtl);
      }
    }
    return storeGoodss;
  }


  @Override
  protected ExplosiveV2EvCallMsg decodeMessage(String msg) throws BaasException {
    log.info("商品上架推送消息ExplosiveV2EvCallMsg:{}", msg);
    return BaasJSONUtil.safeToObject(msg, ExplosiveV2EvCallMsg.class);
  }

  private RsH6SOPClient getH6Client(String tenant) throws BaasException {
    return feignClientMgr.getClient(tenant, null, RsH6SOPClient.class);
  }
}
