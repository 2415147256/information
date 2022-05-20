package com.hd123.baas.sop.evcall.exector.price;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

import com.hd123.baas.sop.service.api.price.priceadjustment.PriceAdjustment;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.hd123.baas.sop.annotation.Tx;
import com.hd123.baas.sop.service.api.basedata.sku.Sku;
import com.hd123.baas.sop.service.api.basedata.sku.SkuFilter;
import com.hd123.baas.sop.service.api.basedata.sku.SkuService;
import com.hd123.baas.sop.service.api.price.priceadjustment.PriceAdjustmentLine;
import com.hd123.baas.sop.service.api.price.priceadjustment.PriceAdjustmentService;
import com.hd123.baas.sop.configuration.FeignClientMgr;
import com.hd123.baas.sop.evcall.AbstractEvCallExecutor;
import com.hd123.baas.sop.remote.rsh6sop.RsH6SOPClient;
import com.hd123.baas.sop.remote.rsh6sop.price.RSGoodsOrderQty;
import com.hd123.baas.sop.remote.rsh6sop.price.RSGoodsOrderQueryFilter;
import com.hd123.baas.sop.utils.BaasJSONUtil;
import com.hd123.rumba.commons.biz.query.QueryDefinition;
import com.hd123.rumba.commons.biz.query.QueryResult;
import com.hd123.rumba.commons.lang.Assert;
import com.hd123.rumba.evcall.EvCallExecutionContext;
import com.qianfan123.baas.common.BaasException;
import com.qianfan123.baas.common.http2.BaasResponse;

import lombok.extern.slf4j.Slf4j;

/**
 * @Author maodapeng
 * @Since
 */
@Component
@Slf4j
public class AdjustmentLineWeekQtyEvCallExecutor extends AbstractEvCallExecutor<AdjustmentLineWeekQtyMsg> {
  public static final String ADJUSTMENT_LINE_WEEK_QTY_EXECUTOR_ID = AdjustmentLineWeekQtyEvCallExecutor.class
      .getSimpleName();
  @Autowired
  private PriceAdjustmentService adjustmentService;
  @Autowired
  private FeignClientMgr feignClientMgr;
  @Autowired
  private SkuService skuService;

  @Override
  @Tx
  protected void doExecute(AdjustmentLineWeekQtyMsg message, EvCallExecutionContext context) throws Exception {
    Assert.notNull(message.getTenant());
    Assert.notNull(message.getOwner());
    String tenant = message.getTenant();
    String owner = message.getOwner();
    PriceAdjustment priceAdjustment = adjustmentService.get(tenant, owner);
    QueryDefinition qd = new QueryDefinition();
    qd.setPageSize(200);
    int page = 0;
    while (true) {
      qd.setPage(page++);
      QueryResult<PriceAdjustmentLine> result = adjustmentService.queryLine(tenant, owner, qd);
      if (CollectionUtils.isEmpty(result.getRecords())) {
        break;
      }
      List<PriceAdjustmentLine> records = result.getRecords();
      Map<String, PriceAdjustmentLine> lineMap = records.stream()
          .collect(Collectors.toMap(s -> s.getSku().getId(), s -> s));
      List<String> skuIds = lineMap.keySet().stream().collect(Collectors.toList());

      // 查询gdGid
      SkuFilter skuFilter = new SkuFilter();
      skuFilter.setIdIn(skuIds);
      skuFilter.setOrgIdEq(priceAdjustment.getOrgId());
      QueryResult<Sku> query = skuService.query(tenant, skuFilter);
      if (CollectionUtils.isEmpty(query.getRecords())) {
        continue;
      }
      Map<String, Sku> skuMap = query.getRecords().stream().collect(Collectors.toMap(s -> s.getId(), s -> s));
      List<String> gdGids = query.getRecords().stream().map(s -> s.getGoodsGid()).collect(Collectors.toList());

      RsH6SOPClient rsH6SOPClient = feignClientMgr.getClient(tenant, null, RsH6SOPClient.class);
      RSGoodsOrderQueryFilter filter = new RSGoodsOrderQueryFilter();
      List<String> pageListGid = gdGids.stream().collect(Collectors.toList());
      Calendar calendar = initBeginTime();
      filter.setEndTimeLessOrEquals(calendar.getTime());
      calendar.add(Calendar.DATE, -7);
      calendar.add(Calendar.SECOND, -1);
      filter.setBeginTimeGreaterOrEquals(calendar.getTime());
      filter.setGdUuidsIn(pageListGid);
      filter.setOrgUuidEquals(priceAdjustment.getOrgId());
      BaasResponse<List<RSGoodsOrderQty>> listBaasResponse = rsH6SOPClient.listOrderQty(tenant, filter);

      if (CollectionUtils.isEmpty(listBaasResponse.getData())) {
        continue;
      }
      Map<String, BigDecimal> qtyMap = listBaasResponse.getData()
          .stream()
          .collect(Collectors.toMap(s -> s.getGdUuid(), s -> s.getQty()));

      List<PriceAdjustmentLine> updates = new ArrayList<>();
      for (PriceAdjustmentLine record : result.getRecords()) {
        String skuId = record.getSku().getId();
        Sku sku = skuMap.get(skuId);
        if (sku == null) {
          continue;
        }
        BigDecimal qty = qtyMap.get(sku.getGoodsGid());
        if (qty == null) {
          continue;
        }
        record.setAveWeekQty(qty);
        updates.add(record);
      }
      adjustmentService.batchModify(tenant, owner, updates, getSysOperateInfo());
    }

  }

  @Override
  protected AdjustmentLineWeekQtyMsg decodeMessage(String msg) throws BaasException {
    log.info("收到AdjustmentLineWeekQtyMsg:{}", msg);
    return BaasJSONUtil.safeToObject(msg, AdjustmentLineWeekQtyMsg.class);
  }

  private Calendar initBeginTime() {
    Calendar calendar = Calendar.getInstance();
    calendar.setTime(new Date());
    calendar.set(Calendar.HOUR_OF_DAY, 0);
    calendar.set(Calendar.MINUTE, 0);
    calendar.set(Calendar.SECOND, 0);
    calendar.set(Calendar.MILLISECOND, 0);
    return calendar;
  }
}
