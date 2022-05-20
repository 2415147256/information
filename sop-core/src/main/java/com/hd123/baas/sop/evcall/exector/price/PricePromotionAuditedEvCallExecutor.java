package com.hd123.baas.sop.evcall.exector.price;

import com.alibaba.fastjson.JSON;
import com.hd123.baas.config.core.BaasConfigClient;
import com.hd123.baas.sop.service.api.basedata.sku.Sku;
import com.hd123.baas.sop.service.api.basedata.sku.SkuFilter;
import com.hd123.baas.sop.service.api.basedata.sku.SkuService;
import com.hd123.baas.sop.service.api.entity.SkuGroupCategoryAssoc;
import com.hd123.baas.sop.service.api.price.PriceSku;
import com.hd123.baas.sop.service.api.price.pricepromotion.*;
import com.hd123.baas.sop.service.dao.group.SkuGroupCategoryAssocDaoBof;
import com.hd123.baas.sop.configuration.FeignClientMgr;
import com.hd123.baas.sop.evcall.AbstractEvCallExecutor;
import com.hd123.baas.sop.remote.rsh6sop.RsH6SOPClient;
import com.hd123.baas.sop.remote.rsh6sop.RsH6SOPConfig;
import com.hd123.baas.sop.remote.rsh6sop.storeprom.StorePromAlcPrcBill;
import com.hd123.baas.sop.remote.rsh6sop.storeprom.StorePromAlcPrcBillDtl;
import com.hd123.baas.sop.utils.BaasJSONUtil;
import com.hd123.rumba.commons.biz.query.Cop;
import com.hd123.rumba.commons.biz.query.QueryDefinition;
import com.hd123.rumba.commons.biz.query.QueryResult;
import com.hd123.rumba.evcall.EvCallExecutionContext;
import com.qianfan123.baas.common.BaasException;
import com.qianfan123.baas.common.http2.BaasResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @Author maodapeng
 * @Since
 */
@Slf4j
@Component
public class PricePromotionAuditedEvCallExecutor extends AbstractEvCallExecutor<PricePromotionAuditedMsg> {

  public static final String PRICE_PROMOTION_AUDITED_EXECUTOR_ID = PricePromotionAuditedEvCallExecutor.class
      .getSimpleName();
  @Autowired
  private PricePromotionService pricePromotionService;
  @Autowired
  private FeignClientMgr feignClientMgr;
  @Autowired
  private SkuGroupCategoryAssocDaoBof skuGroupCategoryAssocDao;
  @Autowired
  private SkuService skuService;
  @Autowired
  private BaasConfigClient configClient;

  @Override
  protected void doExecute(PricePromotionAuditedMsg msg, EvCallExecutionContext context) throws Exception {
    try {

      RsH6SOPConfig config = configClient.getConfig(msg.getTenant(), RsH6SOPConfig.class);
      if (Boolean.FALSE.equals(config.isEnabled())) {
        log.info("未开启H6-SOP服务，忽略执行。tenant = {}", msg.getTenant());
        return;
      }

      PricePromotion pricePromotion = pricePromotionService.get(msg.getTenant(), msg.getUuid(),
          PricePromotion.FETCH_SHOP, PricePromotion.FETCH_LINE);
      RsH6SOPClient h6SopClient = feignClientMgr.getClient(msg.getTenant(), null, RsH6SOPClient.class);
      StorePromAlcPrcBill bill = new StorePromAlcPrcBill();
      bill.setPromId(pricePromotion.getUuid());
      bill.setEffectiveStartTime(pricePromotion.getEffectiveStartDate());
      bill.setEffectiveEndTime(pricePromotion.getEffectiveEndDate());
      bill.setPromName("促销单" + pricePromotion.getFlowNo());
      PricePromotionShop shop = pricePromotion.getShops().get(0);
      bill.setStoreGid(Integer.valueOf(shop.getShop()));
      bill.setHeadSharingRate(getRate(pricePromotion.getHeadSharingRate()));
      bill.setSupervisorSharingRate(getRate(pricePromotion.getSupervisorSharingRate()));
      bill.setOrdLimitAmount(pricePromotion.getOrdLimitAmount());
      bill.setOrdLimitQty(pricePromotion.getOrdLimitQty());
      bill.setState(pricePromotion.getState().name());
      bill.setDetails(buildBillDtls(pricePromotion.getTenant(), pricePromotion.getOrgId(), pricePromotion.getType(), pricePromotion.getLines()));
      log.info("调用H6接口保存促销单：" + JSON.toJSONString(bill));
      BaasResponse<Void> response = h6SopClient.promBillSave(msg.getTenant(), bill);
      if (!response.isSuccess()) {
        throw new BaasException("保存促销单失败，msg:{}", response.getMsg());
      }
    } catch (Exception e) {
      log.error("PricePromotionAuditedEvCallExecutor错误", e);
      throw e;
    }
  }

  private BigDecimal getRate(BigDecimal rate) {
    return rate == null ? BigDecimal.ZERO : rate.multiply(new BigDecimal(100));
  }

  @Override
  protected PricePromotionAuditedMsg decodeMessage(String msg) throws BaasException {
    log.info("收到PricePromotionAuditedMsg:{}", msg);
    return BaasJSONUtil.safeToObject(msg, PricePromotionAuditedMsg.class);
  }

  /**
   * 根据类型构建h6促销行信息
   *
   * @param type
   *     促销单类别
   * @param lines
   *     行
   * @return h6促销行信息
   */
  private List<StorePromAlcPrcBillDtl> buildBillDtls(String tenant, String orgId, PricePromotionType type, List<PricePromotionLine> lines) throws BaasException {
    if (CollectionUtils.isEmpty(lines)) {
      return new ArrayList<>();
    }
    List<StorePromAlcPrcBillDtl> dtls = new ArrayList<>();
    if (type == PricePromotionType.SKU_LIMIT_PRMT) {
      List<String> skuIds = lines.stream().map(PricePromotionLine::getSku).map(PriceSku::getId).collect(Collectors.toList());
      SkuFilter skuFilter = new SkuFilter();
      skuFilter.setOrgIdEq(orgId);
      skuFilter.setIdIn(skuIds);
      QueryResult<Sku> skuQueryResult = skuService.query(tenant, skuFilter);
      log.info("查询商品结果：" + JSON.toJSONString(skuQueryResult));
      if (CollectionUtils.isNotEmpty(skuQueryResult.getRecords())) {
        Map<String, String> skuGdGidMap = skuQueryResult.getRecords().stream().collect(Collectors.toMap(Sku::getId, Sku::getGoodsGid));
        skuGdGidMap.forEach((key, value) -> dtls.add(new StorePromAlcPrcBillDtl(key, Integer.parseInt(value), null)));
      }
    } else {
      QueryDefinition assocQd = new QueryDefinition();
      assocQd.addByField(SkuGroupCategoryAssoc.Queries.SKU_GROUP_ID, Cop.IN, lines.stream().map(PricePromotionLine::getSkuGroup).distinct().toArray());
      List<SkuGroupCategoryAssoc> assocs = skuGroupCategoryAssocDao.query(tenant, assocQd).getRecords();
      if (CollectionUtils.isNotEmpty(assocs)) {
        assocs.stream().map(SkuGroupCategoryAssoc::getCategoryId)
            .forEach(s -> dtls.add(new StorePromAlcPrcBillDtl(null, null, s)));
      }
    }
    return dtls;
  }
}
