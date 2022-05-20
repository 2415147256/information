package com.hd123.baas.sop.evcall.exector.price;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.hd123.baas.sop.annotation.Tx;
import com.hd123.baas.sop.service.api.price.PriceSku;
import com.hd123.baas.sop.service.api.price.shopprice.bean.ShopPriceManager;
import com.hd123.baas.sop.service.api.price.temppriceadjustment.TempPriceAdjustment;
import com.hd123.baas.sop.service.api.price.temppriceadjustment.TempPriceAdjustmentLine;
import com.hd123.baas.sop.service.api.price.temppriceadjustment.TempPriceAdjustmentService;
import com.hd123.baas.sop.service.api.price.temppriceadjustment.TempPriceAdjustmentState;
import com.hd123.baas.sop.service.dao.price.shopprice.ShopPriceManagerDaoBof;
import com.hd123.baas.sop.service.dao.price.tempadjustment.TempPriceAdjustmentLineDaoBof;
import com.hd123.baas.sop.service.impl.price.BillNumberMgr;
import com.hd123.baas.sop.configuration.FeignClientMgr;
import com.hd123.baas.sop.evcall.AbstractEvCallExecutor;
import com.hd123.baas.sop.remote.rsh6sop.RsH6SOPClient;
import com.hd123.baas.sop.remote.rsh6sop.price.adj.RSSalePriceTmpAdjCreation;
import com.hd123.baas.sop.remote.rsh6sop.price.adj.RSSalePriceTmpAdjDetail;
import com.hd123.baas.sop.utils.BaasJSONUtil;
import com.hd123.baas.sop.utils.SopUtils;
import com.hd123.rumba.commons.biz.query.Cop;
import com.hd123.rumba.commons.biz.query.QueryDefinition;
import com.hd123.rumba.evcall.EvCallExecutionContext;
import com.qianfan123.baas.common.BaasException;

import lombok.extern.slf4j.Slf4j;

/**
 * @Author maodapeng
 * @Since
 */
@Slf4j
@Component
public class TempPriceAdjustmentExecutor extends AbstractEvCallExecutor<TempPriceAdjustmentMsg> {
  public static final String TEMP_SHOP_PRICE_ADJUSTMENT_EXECUTOR_ID = TempPriceAdjustmentExecutor.class
      .getSimpleName();
  @Autowired
  private ShopPriceManagerDaoBof shopPriceManagerDao;
  @Autowired
  private TempPriceAdjustmentService tempPriceAdjustmentService;
  @Autowired
  private TempPriceAdjustmentLineDaoBof tempPriceAdjustmentLineDao;
  @Autowired
  FeignClientMgr feignClientMgr;
  @Autowired
  private BillNumberMgr billNumberMgr;

  @Override
  @Tx
  protected void doExecute(TempPriceAdjustmentMsg message, EvCallExecutionContext context) throws Exception {
    String tenant = message.getTenant();
    String uuid = message.getPk();
    TempPriceAdjustment tempPriceAdjustment = tempPriceAdjustmentService.get(tenant, uuid);
    if (tempPriceAdjustment == null) {
      throw new BaasException("改价单不存在");
    }
    if (tempPriceAdjustment.getState() != TempPriceAdjustmentState.AUDITED) {
      throw new BaasException("改价单状态有误");
    }
    QueryDefinition lineQd = new QueryDefinition();
    lineQd.addByField(TempPriceAdjustmentLine.Queries.OWNER, Cop.EQUALS, uuid);
    List<TempPriceAdjustmentLine> lines = tempPriceAdjustmentLineDao.query(tenant, lineQd).getRecords();
    if (lines.isEmpty()) {
      throw new BaasException("改价单行为空");
    }
    List<String> skuIds = lines.stream().map(TempPriceAdjustmentLine::getSkuId).collect(Collectors.toList());
    List<ShopPriceManager> insert = new ArrayList<>();
    List<ShopPriceManager> update = new ArrayList<>();
    QueryDefinition qd = new QueryDefinition();
    qd.addByField(ShopPriceManager.Queries.SKU_ID, Cop.IN, skuIds.toArray());
    qd.addByField(ShopPriceManager.Queries.EFFECTIVE_DATE, Cop.EQUALS, tempPriceAdjustment.getEffectiveStartDate());
    List<ShopPriceManager> managerResult = shopPriceManagerDao.query(tenant, qd).getRecords();

    for (TempPriceAdjustmentLine line : lines) {
      ShopPriceManager manager = managerResult.stream()
          .filter(s -> s.getSku().getId().equals(line.getSkuId()) && s.getShop().equals(line.getShop()))
          .findFirst()
          .orElse(null);
      if (manager == null) {
        ShopPriceManager insertManager = toShopPriceManager(tempPriceAdjustment.getOrgId(), line,
            tempPriceAdjustment.getEffectiveStartDate());
        insert.add(insertManager);
      } else {
        manager.setSalePrice(line.getSalePrice());
        update.add(manager);
      }
    }
    shopPriceManagerDao.batchInsert(tenant, insert);
    shopPriceManagerDao.batchUpdatePrice(tenant, update);

    try {
      RsH6SOPClient rsH6SOPClient = feignClientMgr.getClient(tenant, null, RsH6SOPClient.class);
      RSSalePriceTmpAdjCreation creation = new RSSalePriceTmpAdjCreation();
      creation.setBillNumber(billNumberMgr.generateTempPriceAdjustmentFlowNo(tenant));
      creation.setEffectDate(tempPriceAdjustment.getEffectiveStartDate());
      creation.setLstUpdTime(tempPriceAdjustment.getEffectiveStartDate());
      List<RSSalePriceTmpAdjDetail> details = new ArrayList<>();
      for (TempPriceAdjustmentLine line : lines) {
        RSSalePriceTmpAdjDetail detail = new RSSalePriceTmpAdjDetail();
        detail.setSkuId(line.getSkuId());
        detail.setPrice(line.getSalePrice());
        detail.setStoreId(line.getShop());
        details.add(detail);
      }
      creation.setDetails(details);
      rsH6SOPClient.createTmpAdj(tenant, creation);
      tempPriceAdjustmentService.publish(message.getTenant(), message.getPk(), SopUtils.getSysOperateInfo());
    } catch (Exception e) {
      log.error("H6临时改价调用发生异常", e);
      throw e;
    }
  }

  @Override
  protected TempPriceAdjustmentMsg decodeMessage(String msg) throws BaasException {
    log.info("收到TempPriceAdjustmentMsg:{}", msg);
    return BaasJSONUtil.safeToObject(msg, TempPriceAdjustmentMsg.class);
  }

  private ShopPriceManager toShopPriceManager(String orgId, TempPriceAdjustmentLine line, Date effectiveStartDate) {
    ShopPriceManager manager = new ShopPriceManager();
    manager.setOrgId(orgId);
    manager.setShop(line.getShop());
    manager.setShopCode(line.getShopCode());
    manager.setShopName(line.getShopName());

    PriceSku priceSku = new PriceSku();
    priceSku.setId(line.getSkuId());
    priceSku.setCode(line.getSkuCode());
    priceSku.setName(line.getSkuName());
    priceSku.setQpc(line.getSkuQpc());
    priceSku.setGoodsGid(line.getSkuGid());
    manager.setSku(priceSku);

    manager.setEffectiveDate(effectiveStartDate);
    manager.setSalePrice(line.getSalePrice());
    return manager;
  }
}
