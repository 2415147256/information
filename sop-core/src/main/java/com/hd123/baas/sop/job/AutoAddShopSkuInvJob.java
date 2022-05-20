package com.hd123.baas.sop.job;

import com.hd123.baas.config.core.BaasConfigClient;
import com.hd123.baas.sop.remote.rsias.RsIasClient;
import com.hd123.baas.sop.remote.rsias.RsIasClientConfig;
import com.hd123.baas.sop.remote.rsias.RsIasClientVersion;
import com.hd123.baas.sop.remote.rsias.inv.RsIasResponse;
import com.hd123.baas.sop.remote.rsias.inv.RsInvSync;
import com.hd123.baas.sop.remote.rsias.inv.RsInvSyncReq;
import com.hd123.baas.sop.remote.rsmas.*;
import com.hd123.baas.sop.remote.rsmas.index.RsDocument;
import com.hd123.baas.sop.remote.rsmas.shopsku.RsShopSku;
import com.hd123.baas.sop.remote.rsmas.shopsku.RsShopSkuFilter;
import com.hd123.baas.sop.remote.rsmas.shopskuinvcrule.RsShopSkuInvRule;
import com.hd123.baas.sop.remote.rsmas.shopskuinvcrule.RsShopSkuInvRuleDef;
import com.hd123.baas.sop.remote.rsmas.shopskuinvcrule.RsShopSkuInvRuleFilter;
import com.hd123.baas.sop.remote.rsmas.tenant.RsTenant;
import com.hd123.baas.sop.remote.rsmas.tenant.RsTenantFilter;
import com.hd123.baas.sop.utils.JsonUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @author lins
 */
@Slf4j
@DisallowConcurrentExecution
public class AutoAddShopSkuInvJob implements Job {

  private static final SimpleDateFormat SDF = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
  private static final String DEFAULT_TYPE = "fixedShopSkuQt";
  private static final String QTY = "qty";
  private static final Integer PAGE_SIZE = 1000;

  @Autowired
  private RsMasClient rsMasClient;
  @Autowired
  private RsIasClient rsIasClient;
  @Autowired
  private BaasConfigClient configClient;

  @Override
  public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {

    log.info("执行同步门店商品库存Job");

    Date now = new Date();
    Set<String> tenants;
    try {
      tenants = getTenants();
      for (String tenant : tenants) {
        addShopSkuInv(tenant, now);
      }
    } catch (Exception e) {
      log.error("执行库存Job出错，{}", e.getMessage(), e);
      throw new RuntimeException(e);
    }

    log.info("执行同步门店商品库存Job结束");
  }

  private void addShopSkuInv(String tenant, Date date) throws ParseException {

    RsShopSkuInvRuleFilter filter = new RsShopSkuInvRuleFilter();
    filter.setPageSize(PAGE_SIZE);
    filter.setTypeEq(DEFAULT_TYPE);
    filter.setSorts(Arrays.asList(new RsSort("lastModifyInfo.time", true)));
    Set<String> syncKeys = new HashSet<>(PAGE_SIZE);

    int page = 0;
    int count = 0;
    while (true) {
      filter.setPage(page++);
      RsMasPageResponse<List<RsShopSkuInvRule>> response = rsMasClient.shopSkuInvRuleQuery(tenant, filter);
      if (CollectionUtils.isEmpty(response.getData())) {
        break;
      }
      count += response.getData().size();

      List<RsShopSkuInvRule> ruleList = filterData(response.getData(), date, syncKeys);
      if (CollectionUtils.isNotEmpty(ruleList)) {

        if (log.isDebugEnabled()) {
          log.debug("租户:{},同步库存数量:{},同步库存信息:{}", tenant, ruleList.size(), JsonUtil.objectToJson(ruleList));
        }
        syncIas(tenant, ruleList);

        updateIndex(tenant, ruleList);

      }
      if (count >= response.getTotal()) {
        break;
      }
    }
  }

  private List<RsShopSkuInvRule> filterData(List<RsShopSkuInvRule> shopSkuInvRuleList, Date date, Set<String> syncKeys) throws ParseException {

    List<RsShopSkuInvRule> ruleList = new ArrayList<>();
    for (RsShopSkuInvRule rule : shopSkuInvRuleList) {

      String shopId = rule.getCondition().getShopRange().getShops().get(0).getId();
      String skuId = rule.getCondition().getSkuRange().getSkus().get(0).getId();

      String key = getKey(shopId, skuId);
      if (syncKeys.contains(key)) {
        continue;
      }

      if (rule == null || rule.getDefinition() == null
          || CollectionUtils.isEmpty(rule.getDefinition().getParameters())) {
        continue;
      }

      for (RsParameter parameter : rule.getDefinition().getParameters()) {
        if (parameter == null || parameter.getName() == null
            || !parameter.getName().equals(RsShopSkuInvRuleDef.PARAMETER_NAME_START_DATE)) {
          continue;
        }

        if (parameter.getValue() != null && SDF.parse(parameter.getValue()).compareTo(date) <= 0) {
          syncKeys.add(key);
          ruleList.add(rule);
          break;
        }
      }

    }
    return ruleList;
  }

  private void syncIas(String tenant, List<RsShopSkuInvRule> ruleList) {

    Map<String, BigDecimal> pkQtyMap = new HashMap<>(ruleList.size());
    for (RsShopSkuInvRule shopSkuInvRule : ruleList) {

      String shopId = shopSkuInvRule.getCondition().getShopRange().getShops().get(0).getId();
      String skuId = shopSkuInvRule.getCondition().getSkuRange().getSkus().get(0).getId();
      BigDecimal qty = BigDecimal.ZERO;
      for (RsParameter parameter : shopSkuInvRule.getDefinition().getParameters()) {
        if (QTY.equals(parameter.getName())) {
          qty = new BigDecimal(parameter.getValue());
          break;
        }
      }
      pkQtyMap.put(getPk(shopId, skuId), qty);
    }

    RsShopSkuFilter filter = new RsShopSkuFilter();
    filter.setPkIn(new ArrayList<>(pkQtyMap.keySet()));
    RsMasPageResponse<List<RsShopSku>> response = rsMasClient.shopSkuQuery(tenant, filter);
    if (!response.isSuccess()) {
      log.error("查询门店商品失败，{}", response.getEchoMessage());
      return;
    }

    if (CollectionUtils.isEmpty(response.getData())) {
      return;
    }

    List<RsInvSync> rsInvSyncs = new ArrayList<>(response.getData().size());
    Date now = new Date();
    for (RsShopSku shopSku : response.getData()) {
      if (shopSku.getShop() == null) {
        log.error("数据有问题,门店商品取不到shop；门店商品id：{}", shopSku.getId());
        continue;
      }
      if (shopSku.getSku() == null) {
        log.error("数据有问题,门店商品取不到sku；门店商品id：{}", shopSku.getId());
        continue;
      }

      String skuId = shopSku.getSku().getId();
      String shopId = shopSku.getShop().getId();
      String pk = getPk(shopId, skuId);

      RsInvSync rsInvSync = new RsInvSync();
      rsInvSync.setSkuId(skuId);
      rsInvSync.setSkuCode(shopSku.getSku().getCode());
      rsInvSync.setWrhId(shopId);
      rsInvSync.setWrhCode(shopSku.getShop().getCode());
      rsInvSync.setLastSynced(now);
      rsInvSync.setQpc(shopSku.getSku().getQpc());

      BigDecimal qty = pkQtyMap.getOrDefault(pk, BigDecimal.ZERO);
      rsInvSync.setQty(qty);
      rsInvSyncs.add(rsInvSync);
    }

    RsIasClientConfig iasClientConfig = configClient.getConfig(tenant, RsIasClientConfig.class);
    log.info("invSync --- 获取库存中台组件配置: {}", JsonUtil.objectToJson(iasClientConfig));

    RsInvSyncReq invSyncReq = new RsInvSyncReq();
    invSyncReq.setRequestId(UUID.randomUUID().toString());
    invSyncReq.setLines(rsInvSyncs);
    RsIasResponse<Void> rsIasResponse = RsIasClientVersion.V2.name().equals(iasClientConfig.getVersion()) ?
        rsIasClient.invSyncV2(tenant, invSyncReq)
        : rsIasClient.invSync(tenant, invSyncReq);
    if (!rsIasResponse.success) {
      log.error("库存中心同步失败，{}", rsIasResponse.getMsg());
    }
  }

  private void updateIndex(String tenant, List<RsShopSkuInvRule> ruleList) {

    List<RsDocument> docs = new ArrayList<>(ruleList.size());
    for (RsShopSkuInvRule shopSkuInvRule : ruleList) {

      String shopId = shopSkuInvRule.getCondition().getShopRange().getShops().get(0).getId();
      String skuId = shopSkuInvRule.getCondition().getSkuRange().getSkus().get(0).getId();
      BigDecimal qty = BigDecimal.ZERO;
      for (RsParameter parameter : shopSkuInvRule.getDefinition().getParameters()) {
        if (QTY.equals(parameter.getName())) {
          qty = new BigDecimal(parameter.getValue());
          break;
        }
      }

      RsDocument doc = buildDoc(tenant, shopId, skuId, qty, qty.compareTo(BigDecimal.ZERO) > 0);
      docs.add(doc);
    }

    RsMasRequest masRequest = new RsMasRequest();
    masRequest.setData(docs);
    rsMasClient.updateDoc(tenant, masRequest);
  }

  private RsDocument buildDoc(String tenant, String shopId, String skuId, BigDecimal qty, Boolean hasQty) {
    RsDocument doc = new RsDocument();
    doc.setIndex("mas");
    doc.setType("doc");
    doc.setId(new StringBuffer("shopsku")
        .append(tenant)
        .append("-")
        .append("-")
        .append(skuId)//
        .append(shopId)//
        .toString());
    doc.getFields().put("inv.qty", qty);
    doc.getFields().put("inv.hasQty", hasQty);
    return doc;
  }

  private String getPk(String shopId, String skuId) {
    return shopId + skuId;
  }

  private String getKey(String shopId, String skuId) {
    return new StringBuilder(shopId).append(":").append(skuId).toString();
  }

  /**
   * 获取所有租户
   *
   * @return 租户列表
   */
  private Set<String> getTenants() throws Exception {

    RsTenantFilter filter = new RsTenantFilter();
    RsMasResponse<List<RsTenant>> masResponse = rsMasClient.tenantQuery(filter);
    if (!masResponse.isSuccess()) {
      new Exception("租户查询失败:" + masResponse.getEchoMessage());
    }

    if (CollectionUtils.isEmpty(masResponse.getData())) {
      return Collections.EMPTY_SET;
    }

    Set<String> tenants = new HashSet<>(masResponse.getData().size());
    for (RsTenant rsTenant : masResponse.getData()) {
      tenants.add(rsTenant.getId());
    }
    return tenants;
  }

}
