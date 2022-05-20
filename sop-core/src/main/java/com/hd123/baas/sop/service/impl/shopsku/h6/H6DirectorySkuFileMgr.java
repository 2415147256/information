package com.hd123.baas.sop.service.impl.shopsku.h6;

import com.hd123.baas.config.core.BaasConfigClient;
import com.hd123.baas.sop.annotation.Tx;
import com.hd123.baas.sop.service.api.h6task.H6Task;
import com.hd123.baas.sop.service.api.h6task.H6TaskService;
import com.hd123.baas.sop.service.api.h6task.H6TaskState;
import com.hd123.baas.sop.service.api.h6task.H6TaskType;
import com.hd123.baas.sop.service.api.skumgr.DirectorySkuManager;
import com.hd123.baas.sop.service.dao.h6task.H6TaskDaoBof;
import com.hd123.baas.sop.service.dao.skumgr.DirectorySkuManagerDaoBof;
import com.hd123.baas.sop.configuration.FeignClientMgr;
import com.hd123.baas.sop.remote.rsh6sop.RsH6SOPClient;
import com.hd123.baas.sop.remote.rsh6sop.RsH6SOPConfig;
import com.hd123.baas.sop.remote.rsh6sop.price.GoodsPriceTaskType;
import com.hd123.baas.sop.remote.rsh6sop.price.RSGoodsPriceTaskCreation;
import com.hd123.baas.sop.utils.CsvWriter;
import com.hd123.baas.sop.utils.FileUtils;
import com.hd123.baas.sop.utils.IdGenUtils;
import com.hd123.baas.sop.utils.ZipUtils;
import com.hd123.rumba.commons.biz.entity.OperateInfo;
import com.hd123.rumba.commons.biz.query.Cop;
import com.hd123.rumba.commons.biz.query.QueryDefinition;
import com.hd123.rumba.commons.biz.query.QueryResult;
import com.hd123.rumba.oss.api.Bucket;
import com.qianfan123.baas.common.BaasException;
import com.qianfan123.baas.common.http2.BaasResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.text.SimpleDateFormat;
import java.util.Date;

@Component
@Slf4j
public class H6DirectorySkuFileMgr {

  @Autowired
  private DirectorySkuManagerDaoBof directorySkuManagerDao;
  @Autowired
  private H6TaskService h6TaskService;
  @Autowired(required = false)
  private Bucket bucket;
  @Autowired
  FeignClientMgr feignClientMgr;
  @Autowired
  private H6TaskDaoBof dao;
  @Autowired
  private BaasConfigClient configClient;

  private static final SimpleDateFormat FILE_NAME_FORMAT = new SimpleDateFormat("yyyyMMdd");

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
    if (h6Task.getType() != H6TaskType.SKU) {
      log.info("任务类型是<{}>，忽略", h6Task.getType().name());
      return;
    }
    // 是否开启下发
    RsH6SOPConfig config = configClient.getConfig(tenant, RsH6SOPConfig.class);
    if (Boolean.FALSE.equals(config.isEnabled())) {
      log.info("目录商品下发未开启h6-sop推送配置:任务url:{},商品价格任务类型:{},任务ID:{},", h6Task.getFileUrl(), GoodsPriceTaskType.storeOnGoods, h6Task.getUuid());
      return;
    }

    RSGoodsPriceTaskCreation taskCreation = new RSGoodsPriceTaskCreation();
    taskCreation.setFileUrl(h6Task.getFileUrl());
    taskCreation.setOccurredTime(h6Task.getOccurredTime());
    taskCreation.setTaskId(h6Task.getUuid());
    taskCreation.setType(GoodsPriceTaskType.storeOnGoods);

    RsH6SOPClient rsH6SOPClient = feignClientMgr.getClient(tenant, null, RsH6SOPClient.class);
    BaasResponse<Void> response = rsH6SOPClient.createTask(tenant, taskCreation);
    if (!response.isSuccess()) {
      h6TaskService.logError(tenant, taskId, "调用H6创建价格下发任务失败，" + response.getMsg(), null, operateInfo);
      throw new BaasException("调用H6创建价格下发任务失败：" + response.getMsg());
    }
  }

  @Tx
  public void generateSkuFile(String tenant, String taskId, OperateInfo operateInfo) throws Exception {
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
      parent = getParent(tenant, h6Task.getExecuteDate());
      this.doGenerateFile(tenant, h6Task, operateInfo);
    } finally {
      if (parent != null) {
        FileUtils.deleteFile(parent);
      }
    }
  }

  private void doGenerateFile(String tenant, H6Task h6Task, OperateInfo operateInfo) throws Exception {
    // 文件
    File shopSkuFile = getShopSkuFile(tenant, h6Task.getExecuteDate());
    CsvWriter shopSkuExcelWriter = CsvWriter.newWriter(shopSkuFile);

    log.info("H6ShopSku生成的文件路径为:{}", shopSkuFile.getAbsolutePath());

    QueryDefinition qd = new QueryDefinition();
    qd.addByField(DirectorySkuManager.Queries.ISSUE_DATE, Cop.EQUALS, h6Task.getExecuteDate());
    qd.setPageSize(1000);
    int page = 0;
    while (true) {
      qd.setPage(page);
      QueryResult<DirectorySkuManager> result = directorySkuManagerDao.query(tenant, qd);
      if (result.getRecords().isEmpty()) {
        break;
      }
      page = page + 1;
      for (DirectorySkuManager manager : result.getRecords()) {
        H6ShopSku h6ShopSku = new H6ShopSku();
        h6ShopSku.setSkuGid(manager.getSkuGid());
        h6ShopSku.setShopId(manager.getShop());
        h6ShopSku.setRequired(manager.isChannelRequired() ? "1" : manager.isDirectoryRequired() ? "1" : "0");
        shopSkuExcelWriter.writeNext(h6ShopSku.toLine());
      }
      shopSkuExcelWriter.flush();
    }
    shopSkuExcelWriter.flush();

    try {
      shopSkuExcelWriter.close();
    } catch (Throwable ex) {
    }
    File zipFile = getZipFile(tenant, h6Task.getExecuteDate());
    log.info("压缩包生成的文件路径为:{}", zipFile.getAbsolutePath());
    ZipUtils.zip(shopSkuFile.getParentFile(), zipFile);

    String zipOssPath = "sop/tempOf7Day/sku/h6/" + FILE_NAME_FORMAT.format(h6Task.getExecuteDate()) + "_"
        + IdGenUtils.buildIid() + "/" + zipFile.getName();
    try {
      bucket.put(zipOssPath, new FileInputStream(zipFile));
    } catch (FileNotFoundException e) {
      throw new BaasException(e);
    }
    h6Task.setState(H6TaskState.DELIVERED);
    h6Task.setFileUrl(bucket.getUrl(zipOssPath, Bucket.CONTENT_TYPE_OF_WILDCARD));
    h6Task.setOccurredTime(operateInfo.getTime());
    dao.update(tenant, h6Task, operateInfo);
  }

  private File getZipFile(String tenant, Date executeDate) {
    String fileName = "SKU_" + FILE_NAME_FORMAT.format(executeDate) + ".zip";
    return new File(getParent(tenant, executeDate), fileName);
  }

  private File getShopSkuFile(String tenant, Date executeDate) {
    String fileName = "STORE_ON_GOODS_" + FILE_NAME_FORMAT.format(executeDate) + ".csv";
    return new File(getParent(tenant, executeDate), fileName);
  }

  private File getParent(String tenant, Date executeDate) {
    String defaultBaseDir = System.getProperty("java.io.tmpdir");
    String date = FILE_NAME_FORMAT.format(executeDate);
    File file = new File(defaultBaseDir,
        tenant + File.separator + "sop" + File.separator + "sku" + File.separator + "h6" + File.separator + date);
    if (!file.exists()) {
      file.mkdirs();
    }
    return file;
  }
}
