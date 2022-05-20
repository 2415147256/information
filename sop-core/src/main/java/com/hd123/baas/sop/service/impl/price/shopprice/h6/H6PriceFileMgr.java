package com.hd123.baas.sop.service.impl.price.shopprice.h6;

import com.hd123.baas.config.core.BaasConfigClient;
import com.hd123.baas.sop.annotation.Tx;
import com.hd123.baas.sop.config.H6TaskConfig;
import com.hd123.baas.sop.service.api.h6task.H6Task;
import com.hd123.baas.sop.service.api.h6task.H6TaskService;
import com.hd123.baas.sop.service.api.h6task.H6TaskState;
import com.hd123.baas.sop.service.api.h6task.H6TaskType;
import com.hd123.baas.sop.service.api.price.pricepromotion.PricePromotion;
import com.hd123.baas.sop.service.api.price.pricepromotion.PricePromotionService;
import com.hd123.baas.sop.service.api.price.shopprice.ShopPriceManagerService;
import com.hd123.baas.sop.service.api.price.shopprice.bean.ShopPriceManager;
import com.hd123.baas.sop.service.api.price.tempshoppriceadjustment.TempShopPriceManager;
import com.hd123.baas.sop.service.api.price.tempshoppriceadjustment.TempShopPriceManagerService;
import com.hd123.baas.sop.common.DefaultOrgIdConvert;
import com.hd123.baas.sop.configuration.FeignClientMgr;
import com.hd123.baas.sop.remote.rsh6sop.RsH6SOPClient;
import com.hd123.baas.sop.remote.rsh6sop.price.GoodsPriceTaskType;
import com.hd123.baas.sop.remote.rsh6sop.price.RSGoodsPriceTaskCreation;
import com.hd123.baas.sop.utils.CsvWriter;
import com.hd123.baas.sop.utils.FileUtils;
import com.hd123.baas.sop.utils.IdGenUtils;
import com.hd123.baas.sop.utils.ZipUtils;
import com.hd123.rumba.commons.biz.entity.OperateInfo;
import com.hd123.rumba.commons.biz.query.Cop;
import com.hd123.rumba.commons.biz.query.QueryDefinition;
import com.hd123.rumba.commons.biz.query.QueryOrderDirection;
import com.hd123.rumba.oss.api.Bucket;
import com.qianfan123.baas.common.BaasException;
import com.qianfan123.baas.common.http2.BaasResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * @author zhengzewang on 2020/11/23.
 * <p>
 * 处理h6同步文件
 */
@Component
@Slf4j
public class H6PriceFileMgr {

  @Autowired
  private ShopPriceManagerService shopPriceManagerService;
  @Autowired
  private H6TaskService h6TaskService;
  @Autowired(required = false)
  private Bucket bucket;
  @Autowired
  FeignClientMgr feignClientMgr;
  @Autowired
  private BaasConfigClient configClient;
  @Autowired
  private PricePromotionService pricePromotionService;
  @Autowired
  private TempShopPriceManagerService tempShopPriceManagerService;

  private static final SimpleDateFormat FILE_NAME_FORMAT = new SimpleDateFormat("yyyyMMdd");
  private static final SimpleDateFormat FILE_NAME_FORMAT2 = new SimpleDateFormat("yyyy-MM-dd");

  @Tx
  public void uploadH6Task(String tenant, String taskId, OperateInfo operateInfo) throws BaasException {
    H6Task h6Task = h6TaskService.get(tenant, taskId);
    if (h6Task == null) {
      log.info("任务不存在，忽略");
      return;
    }
    if (h6Task.getState() != H6TaskState.DELIVERED) {
      log.info("任务当前状态<{}>，忽略", h6Task.getState().name());
      return;
    }

    H6TaskConfig h6TaskConfig = configClient.getConfig(tenant, H6TaskConfig.class);
    if (h6TaskConfig != null && !h6TaskConfig.isDelivered()) {
      log.info("当前任务不启用下发到H6，忽略，taskId={}", taskId);
      return;
    }
    //  构建对象并请求
    RSGoodsPriceTaskCreation taskCreation = buildGoodsPriceTaskCreation(h6Task);
    RsH6SOPClient rsH6SOPClient = feignClientMgr.getClient(tenant, null, RsH6SOPClient.class);
    BaasResponse<Void> response = rsH6SOPClient.createTask(tenant, taskCreation);
    if (!response.isSuccess()) {
      h6TaskService.logError(tenant, taskId, "调用H6创建价格下发任务失败，" + response.getMsg(), null, operateInfo);
      throw new BaasException("调用H6创建价格下发任务失败：" + response.getMsg());
    }
  }

  @Tx
  public void generateFile(String tenant, String taskId, String lastShop, OperateInfo operateInfo) throws Exception {
    File parent = null;
    try {
      H6Task h6Task = h6TaskService.getWithLock(tenant, taskId);
      if (h6Task == null) {
        log.info("任务不存在，忽略");
        return;
      }
      if (h6Task.getState() != H6TaskState.CONFIRMED) {
        log.info("任务当前状态<{}>，忽略", h6Task.getState().name());
        return;
      }
      parent = getParent(tenant, h6Task.getType().name(), h6Task.getExecuteDate());
      if (h6Task.getType() == H6TaskType.PRICE) {
        this.doGenerateFile(tenant, lastShop, h6Task, operateInfo);
      } else if (h6Task.getType() == H6TaskType.TEMP_SHOP) {
        this.doGenerateTempShopPriceFile(tenant, lastShop, h6Task, operateInfo);
      }
    } finally {
      if (parent != null) {
        FileUtils.deleteFile(parent);
      }
    }
  }

  // 临时到店价
  private void doGenerateTempShopPriceFile(String tenant, String lastShop, H6Task h6Task, OperateInfo operateInfo) throws Exception {
    // 文件
    File basePriceFile = getBasePriceFile(tenant, h6Task.getType(), h6Task.getExecuteDate());
    CsvWriter basePriceWriter = CsvWriter.newWriter(basePriceFile);
    String orgId = h6Task.getOrgId();

    log.info("临时到店价 H6BasePrice生成的文件路径为:{}", basePriceFile.getAbsolutePath());
    // 以最后一次门店为基准拿基础到店价
    String lastUuid = null;
    while (true) {
      List<TempShopPriceManager> baseResult = queryTempShopPriceManagers(tenant, orgId, lastShop, h6Task, lastUuid);
      if (CollectionUtils.isEmpty(baseResult)) {
        break;
      }
      TempShopPriceManager lastOne = baseResult.get(baseResult.size() - 1);
      lastUuid = lastOne.getUuid();
      for (TempShopPriceManager manager : baseResult) {
        // 只处理qpc=1
        if (manager.getSkuQpc() != null && manager.getSkuQpc().compareTo(BigDecimal.ONE) == 0) {
          H6BasePrice basePrice = buildH6BasePrice(h6Task, manager);
          basePriceWriter.writeNext(basePrice.toLine());
        }
      }
      basePriceWriter.flush();
    }

    File promotionPriceFile = getShopPromotionPriceFile(tenant, h6Task.getType(), h6Task.getExecuteDate());
    CsvWriter promotionPriceExcelWriter = CsvWriter.newWriter(promotionPriceFile);
    log.info("临时到店价 H6ShopPromotionPrice生成的文件路径为:{}", promotionPriceFile.getAbsolutePath());
    lastUuid = null;
    while (true) {
      List<TempShopPriceManager> result = queryTempShopPriceManagers(tenant, orgId, h6Task, lastUuid);
      if (CollectionUtils.isEmpty(result)) {
        break;
      }
      lastUuid = result.get(result.size() - 1).getUuid();
      for (TempShopPriceManager manager : result) {
        if (manager.getSkuQpc().compareTo(BigDecimal.ONE) == 0) {
          if (manager.getPromotionSource() != null) {
            PricePromotion promotion = pricePromotionService.get(tenant, manager.getPromotionSource());
            if (promotion != null) {
              H6ShopPromotionPrice promotionPrice = buildH6ShopPromotionPrice(h6Task, manager, promotion);
              promotionPriceExcelWriter.writeNext(promotionPrice.toLine());
            }
          }
        }
      }
      promotionPriceExcelWriter.flush();
    }
    try {
      basePriceWriter.close();
      promotionPriceExcelWriter.close();
    } catch (Throwable ex) {
      log.error("close异常", ex);
    }
    File zipFile = getZipFile(tenant, h6Task.getType(), h6Task.getExecuteDate());
    log.info("临时到店价压缩包生成的文件路径为:{}", zipFile.getAbsolutePath());
    ZipUtils.zip(promotionPriceFile.getParentFile(), zipFile);
    String zipOssPath = "sop/tempOf7Day/price/h6/temp/" + FILE_NAME_FORMAT.format(h6Task.getExecuteDate()) + "_"
        + IdGenUtils.buildIid() + "/" + zipFile.getName();
    try {
      bucket.put(zipOssPath, new FileInputStream(zipFile));
    } catch (FileNotFoundException e) {
      throw new BaasException(e);
    }
    h6TaskService.fixUrl(tenant, h6Task.getUuid(), bucket.getUrl(zipOssPath, Bucket.CONTENT_TYPE_OF_WILDCARD), operateInfo);
  }

  private void doGenerateFile(String tenant, String lastShop, H6Task h6Task, OperateInfo operateInfo) throws Exception {
    String orgId = h6Task.getOrgId();
    // 文件
    File basePriceFile = getBasePriceFile(tenant, h6Task.getType(), h6Task.getExecuteDate());
    CsvWriter basePricelWriter = CsvWriter.newWriter(basePriceFile);
    log.info("H6BasePrice生成的文件路径为:{}", basePriceFile.getAbsolutePath());
    // 以最后一次门店为基准拿基础到店价
    String lastUuid = null;
    while (true) {
      List<ShopPriceManager> result = queryShopPriceManagers(tenant, orgId, lastShop, h6Task, lastUuid);
      if (CollectionUtils.isEmpty(result)) {
        break;
      }
      ShopPriceManager lastOne = result.get(result.size() - 1);
      lastUuid = lastOne.getUuid();
      for (ShopPriceManager manager : result) {
        boolean basePriceChanged = (manager.getChanged() & 1) == 1;
        // 只处理qpc=1
        if (basePriceChanged && manager.getSku().getQpc() != null
            && manager.getSku().getQpc().compareTo(BigDecimal.ONE) == 0) {
          H6BasePrice basePrice = buildH6BasePrice(orgId, manager);
          basePricelWriter.writeNext(basePrice.toLine());
        }
      }
    }
    basePricelWriter.flush();
    try {
      basePricelWriter.close();
    } catch (Throwable ex) {
      // nothing
      log.error("basePricelWriter.close异常", ex);
    }

    File salePriceFile = getShopSalePriceFile(tenant, h6Task.getType(), h6Task.getExecuteDate());
    CsvWriter salePriceExcelWriter = CsvWriter.newWriter(salePriceFile);
    log.info("H6ShopSalePrice生成的文件路径为:{}", salePriceFile.getAbsolutePath());
    File promotionPriceFile = getShopPromotionPriceFile(tenant, h6Task.getType(), h6Task.getExecuteDate());
    CsvWriter shopPriceExcelWriter = CsvWriter.newWriter(promotionPriceFile);
    log.info("H6ShopPromotionPrice生成的文件路径为:{}", promotionPriceFile.getAbsolutePath());
    lastUuid = null;
    while (true) {
      List<ShopPriceManager> result = queryShopPriceManagers(tenant, orgId, h6Task, lastUuid);
      if (CollectionUtils.isEmpty(result)) {
        break;
      }
      lastUuid = result.get(result.size() - 1).getUuid();
      for (ShopPriceManager manager : result) {
        boolean salePriceChanged = (manager.getChanged() & 2) == 2;
        boolean shopPriceChanged = (manager.getChanged() & 4) == 4;
        // 促销价
        if (shopPriceChanged && manager.getShopPrice() != null && manager.getSku().getQpc() != null
            && manager.getSku().getQpc().compareTo(BigDecimal.ONE) == 0) {
          H6ShopPromotionPrice promotionPrice = buildH6ShopPromotionPrice(tenant, orgId, manager);
          shopPriceExcelWriter.writeNext(promotionPrice.toLine());
        }
        // 售价
        if (salePriceChanged && manager.getSalePrice() != null) {
          H6ShopSalePrice salePrice = buildH6ShopSalePrice(manager);
          salePriceExcelWriter.writeNext(salePrice.toLine());
        }
      }
      salePriceExcelWriter.flush();
      shopPriceExcelWriter.flush();
    }
    salePriceExcelWriter.flush();
    shopPriceExcelWriter.flush();
    try {
      salePriceExcelWriter.close();
      shopPriceExcelWriter.close();
    } catch (Throwable ex) {
      log.error("close异常", ex);
    }

    File zipFile = getZipFile(tenant, h6Task.getType(), h6Task.getExecuteDate());
    log.info("压缩包生成的文件路径为:{}", zipFile.getAbsolutePath());
    ZipUtils.zip(salePriceFile.getParentFile(), zipFile);

    String zipOssPath = "sop/tempOf7Day/price/h6/" + FILE_NAME_FORMAT.format(h6Task.getExecuteDate()) + "_"
        + IdGenUtils.buildIid() + "/" + zipFile.getName();
    try {
      bucket.put(zipOssPath, new FileInputStream(zipFile));
    } catch (FileNotFoundException e) {
      throw new BaasException(e);
    }
    h6TaskService.fixUrl(tenant, h6Task.getUuid(), bucket.getUrl(zipOssPath, Bucket.CONTENT_TYPE_OF_WILDCARD),
        operateInfo);
  }

  private String convertRate(BigDecimal rate) {
    return rate == null ? BigDecimal.ZERO.toString() : rate.multiply(new BigDecimal(100)).toString();
  }

  private File getZipFile(String tenant, H6TaskType h6TaskType, Date executeDate) {
    String fileName = "PRICE_" + FILE_NAME_FORMAT.format(executeDate) + ".zip";
    return new File(getParent(tenant, h6TaskType.name(), executeDate), fileName);
  }

  private File getBasePriceFile(String tenant, H6TaskType h6TaskType, Date executeDate) {
    String fileName = "BASE_PRICE_" + FILE_NAME_FORMAT.format(executeDate) + ".csv";
    return new File(getParent(tenant, h6TaskType.name(), executeDate), fileName);
  }

  private File getShopSalePriceFile(String tenant, H6TaskType h6TaskType, Date executeDate) {
    String fileName = "SALE_PRICE_" + FILE_NAME_FORMAT.format(executeDate) + ".csv";
    return new File(getParent(tenant, h6TaskType.name(), executeDate), fileName);
  }

  private File getShopPromotionPriceFile(String tenant, H6TaskType taskType, Date executeDate) {
    String fileName = "PROMOTION_PRICE_" + FILE_NAME_FORMAT.format(executeDate) + ".csv";
    return new File(getParent(tenant, taskType.name(), executeDate), fileName);
  }

  private File getParent(String tenant, String pre, Date executeDate) {
    String defaultBaseDir = System.getProperty("java.io.tmpdir");
    String date = FILE_NAME_FORMAT.format(executeDate);
    File file = new File(defaultBaseDir,
        tenant + File.separator + "sop" + File.separator + pre + File.separator + "h6" + File.separator + date);
    if (!file.exists()) {
      file.mkdirs();
    }
    return file;
  }

  private List<TempShopPriceManager> queryTempShopPriceManagers(String tenant, String orgId, H6Task h6Task, String lastUuid) {
    return queryTempShopPriceManagers(tenant, orgId, null, h6Task, lastUuid);
  }

  private List<TempShopPriceManager> queryTempShopPriceManagers(String tenant, String orgId, String lastShop, H6Task h6Task, String lastUuid) {
    log.info("queryTempShopPriceManagers的条件，tenant={}, orgId={}, shop={}, executeDate={}, lastUuid={}"
        , tenant, orgId, lastShop, h6Task.getExecuteDate(), lastUuid);

    QueryDefinition baseQd = new QueryDefinition();
    baseQd.addByField(TempShopPriceManager.Queries.TENANT, Cop.EQUALS, tenant);
    if (StringUtils.isNotEmpty(orgId)) {
      baseQd.addByField(TempShopPriceManager.Queries.ORG_ID, Cop.EQUALS, orgId);
    }
    if (StringUtils.isNotEmpty(lastShop)) {
      baseQd.addByField(TempShopPriceManager.Queries.SHOP, Cop.EQUALS, lastShop);
    }
    baseQd.addByField(TempShopPriceManager.Queries.EFFECTIVE_DATE, Cop.EQUALS, h6Task.getExecuteDate());
    if (lastUuid != null) {
      baseQd.addByField(TempShopPriceManager.Queries.UUID, Cop.GREATER, lastUuid);
    }
    baseQd.addOrder(TempShopPriceManager.Queries.UUID, QueryOrderDirection.asc);
    baseQd.setPageSize(10000);
    return tempShopPriceManagerService.query(tenant, baseQd).getRecords();
  }

  private RSGoodsPriceTaskCreation buildGoodsPriceTaskCreation(H6Task h6Task) {
    RSGoodsPriceTaskCreation taskCreation = new RSGoodsPriceTaskCreation();
    taskCreation.setFileUrl(h6Task.getFileUrl());
    taskCreation.setOccurredTime(h6Task.getOccurredTime());
    taskCreation.setTaskId(h6Task.getUuid());
    GoodsPriceTaskType type = GoodsPriceTaskType.prom;
    if (h6Task.getType() == H6TaskType.PRICE || h6Task.getType() == H6TaskType.TEMP_SHOP) {
      type = GoodsPriceTaskType.price;
    }
    taskCreation.setType(type);
    return taskCreation;
  }

  private H6BasePrice buildH6BasePrice(H6Task h6Task, TempShopPriceManager manager) {
    H6BasePrice basePrice = new H6BasePrice();
    basePrice.setEffectiveStartDate(FILE_NAME_FORMAT2.format(manager.getEffectiveDate()));
    basePrice.setGoodsId(manager.getSkuGid());
    basePrice.setBasePrice(manager.getBasePrice());
    basePrice.setOrgId(DefaultOrgIdConvert.toH6DefOrgId(h6Task.getOrgId()));
    return basePrice;
  }

  private H6ShopPromotionPrice buildH6ShopPromotionPrice(H6Task h6Task, TempShopPriceManager manager, PricePromotion promotion) {
    H6ShopPromotionPrice promotionPrice = new H6ShopPromotionPrice();
    promotionPrice.setOrgId(DefaultOrgIdConvert.toH6DefOrgId(h6Task.getOrgId()));
    promotionPrice.setEffectiveStartDate(FILE_NAME_FORMAT2.format(manager.getEffectiveDate()));
    if (manager.getEffectiveEndDate() == null) {
      promotionPrice.setEffectiveEndDate("-");
    } else {
      promotionPrice.setEffectiveEndDate(FILE_NAME_FORMAT2.format(manager.getEffectiveEndDate()));
    }
    promotionPrice.setShopId(manager.getShop());
    promotionPrice.setGoodsId(manager.getSkuGid());
    promotionPrice.setShopPrice(manager.getShopPrice());
    promotionPrice.setBaseShopPrice(manager.getBasePrice());
    promotionPrice.setPromotionUuid(promotion.getUuid());
    promotionPrice.setOrdLimitAmount(promotion.getOrdLimitAmount());
    promotionPrice.setOrdLimitQty(promotion.getOrdLimitQty());
    promotionPrice.setHeadFavorSharing(convertRate(promotion.getHeadSharingRate()));
    promotionPrice.setSupervisorFavorSharing(convertRate(promotion.getSupervisorSharingRate()));
    return promotionPrice;
  }

  private List<ShopPriceManager> queryShopPriceManagers(String tenant, String orgId, H6Task h6Task, String lastUuid) {
    return queryShopPriceManagers(tenant, orgId, null, h6Task, lastUuid);
  }

  private List<ShopPriceManager> queryShopPriceManagers(String tenant, String orgId, String lastShop, H6Task h6Task, String lastUuid) {
    log.info("queryShopPriceManagers的条件，tenant={}, orgId={}, shop={}, executeDate={}, lastUuid={}"
        , tenant, orgId, lastShop, h6Task.getExecuteDate(), lastUuid);

    QueryDefinition qd = new QueryDefinition();
    qd.addByField(ShopPriceManager.Queries.TENANT, Cop.EQUALS, tenant);
    if (StringUtils.isNotEmpty(orgId)) {
      qd.addByField(ShopPriceManager.Queries.ORG_ID, Cop.EQUALS, orgId);
    }
    if (StringUtils.isNotEmpty(lastShop)) {
      qd.addByField(ShopPriceManager.Queries.SHOP, Cop.EQUALS, lastShop);
    }
    qd.addByField(ShopPriceManager.Queries.EFFECTIVE_DATE, Cop.EQUALS, h6Task.getExecuteDate());
    if (lastUuid != null) {
      qd.addByField(ShopPriceManager.Queries.UUID, Cop.GREATER, lastUuid);
    }
    qd.addOrder(ShopPriceManager.Queries.UUID, QueryOrderDirection.asc);
    qd.setPageSize(10000);
    return shopPriceManagerService.list(tenant, qd);
  }

  private H6BasePrice buildH6BasePrice(String orgId, ShopPriceManager manager) {
    H6BasePrice basePrice = new H6BasePrice();
    basePrice.setOrgId(DefaultOrgIdConvert.toH6DefOrgId(orgId));
    basePrice.setEffectiveStartDate(FILE_NAME_FORMAT2.format(manager.getEffectiveDate()));
    basePrice.setGoodsId(manager.getSku().getGoodsGid());
    basePrice.setBasePrice(manager.getBasePrice());
    return basePrice;
  }

  private H6ShopSalePrice buildH6ShopSalePrice(ShopPriceManager manager) {
    H6ShopSalePrice salePrice = new H6ShopSalePrice();
    salePrice.setEffectiveStartDate(FILE_NAME_FORMAT2.format(manager.getEffectiveDate()));
    salePrice.setShopId(manager.getShop());
    salePrice.setSkuId(manager.getSku().getId());
    salePrice.setSalePrice(manager.getSalePrice());
    return salePrice;
  }

  private H6ShopPromotionPrice buildH6ShopPromotionPrice(String tenant, String orgId, ShopPriceManager manager) {
    H6ShopPromotionPrice promotionPrice = new H6ShopPromotionPrice();
    promotionPrice.setOrgId(DefaultOrgIdConvert.toH6DefOrgId(orgId));
    promotionPrice.setEffectiveStartDate(FILE_NAME_FORMAT2.format(manager.getEffectiveDate()));
    if (manager.getEffectiveEndDate() == null) {
      promotionPrice.setEffectiveEndDate("-");
    } else {
      promotionPrice.setEffectiveEndDate(FILE_NAME_FORMAT2.format(manager.getEffectiveEndDate()));
    }
    promotionPrice.setShopId(manager.getShop());
    promotionPrice.setGoodsId(manager.getSku().getGoodsGid());
    promotionPrice.setShopPrice(manager.getShopPrice());
    promotionPrice.setBaseShopPrice(manager.getBasePrice());

    if (manager.getPromotionSource() != null) {
      PricePromotion promotion = pricePromotionService.get(tenant, manager.getPromotionSource());
      if (promotion != null) {
        promotionPrice.setPromotionUuid(promotion.getUuid());
        promotionPrice.setOrdLimitAmount(promotion.getOrdLimitAmount());
        promotionPrice.setOrdLimitQty(promotion.getOrdLimitQty());
        promotionPrice.setHeadFavorSharing(convertRate(promotion.getHeadSharingRate()));
        promotionPrice.setSupervisorFavorSharing(convertRate(promotion.getSupervisorSharingRate()));
      }
    }
    return promotionPrice;
  }
}
