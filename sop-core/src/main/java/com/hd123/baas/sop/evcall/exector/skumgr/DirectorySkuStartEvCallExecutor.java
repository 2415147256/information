package com.hd123.baas.sop.evcall.exector.skumgr;

import com.hd123.baas.sop.service.api.h6task.H6Task;
import com.hd123.baas.sop.service.api.h6task.H6TaskService;
import com.hd123.baas.sop.service.api.h6task.H6TaskState;
import com.hd123.baas.sop.service.api.skumgr.Directory;
import com.hd123.baas.sop.service.api.skumgr.DirectoryService;
import com.hd123.baas.sop.service.api.skumgr.DirectoryShop;
import com.hd123.baas.sop.service.api.skumgr.DirectorySku;
import com.hd123.baas.sop.service.api.skumgr.DirectorySkuFilter;
import com.hd123.baas.sop.service.api.skumgr.DirectorySkuManager;
import com.hd123.baas.sop.service.dao.skumgr.DirectorySkuManagerDaoBof;
import com.hd123.baas.sop.evcall.AbstractEvCallExecutor;
import com.hd123.baas.sop.evcall.EvCallEventPublisher;
import com.hd123.baas.sop.utils.BaasJSONUtil;
import com.hd123.rumba.commons.biz.entity.OperateInfo;
import com.hd123.rumba.evcall.EvCallExecutionContext;
import com.qianfan123.baas.common.BaasException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * @author zhengzewang on 2020/11/18.
 */
@Slf4j
@Component
public class DirectorySkuStartEvCallExecutor extends AbstractEvCallExecutor<DirectorySkuStartMsg> {

  public static final String EXECUTOR_ID = DirectorySkuStartEvCallExecutor.class.getSimpleName();

  @Autowired
  private H6TaskService h6TaskService;
  @Autowired
  private DirectoryService directoryService;
  @Autowired
  private DirectorySkuManagerDaoBof directorySkuManagerDao;
  @Autowired
  private EvCallEventPublisher publisher;

  @Override
  protected void doExecute(DirectorySkuStartMsg message, EvCallExecutionContext context) throws Exception {

    String tenant = message.getTenant();
    Date issueDate = message.getExecuteDate();
    String taskId = message.getTaskId();
    log.info("开始计算价格 租户<{}>，日期<{}>，任务=<{}>", tenant, issueDate, taskId);
    if (taskId == null) {
      log.info("taskId is null，忽略");
      return;
    }
    if (issueDate.before(DateUtils.truncate(new Date(), Calendar.DATE))) {
      // 计算今天之前的，就算是补偿也没有意义了
      log.info("计算日期 {} 为今天之前的日期，忽略", issueDate);

      return;
    }

    H6Task h6Tasks = h6TaskService.get(tenant, taskId);
    if (h6Tasks == null) {
      log.info("任务不存在。");
      return;
    }
    if (h6Tasks.getState().equals(H6TaskState.FINISHED)) {
      log.info("任务已完成。");
      return;
    }
    String title = "";
    OperateInfo operateInfo = getSysOperateInfo();
    try {
      // 更新状态
      h6TaskService.updateState(tenant, taskId, H6TaskState.CONFIRMED, getSysOperateInfo());
      title = "查询商品目录";
      List<Directory> directories = directoryService.list(tenant, h6Tasks.getOrgId());

      for (Directory directory : directories) {
        title = "查询目录<" + directory.getUuid() + ">门店";
        List<DirectoryShop> shops = directoryService.queryShops(tenant, "*", directory.getUuid());
        title = "查询目录<" + directory.getUuid() + ">下的商品";
        DirectorySkuFilter skuFilter = new DirectorySkuFilter();
        skuFilter.setPage(0);
        skuFilter.setPageSize(Integer.MAX_VALUE);
        List<DirectorySku> skus = directoryService.queryDirectorySku(tenant, directory.getUuid(), skuFilter).getRecords();
        if (skus.isEmpty()){
          log.info("目录<{}>没有商品",directory.getUuid());
          continue;
        }
        for (DirectoryShop shop : shops) {
          List<DirectorySkuManager> managers = new ArrayList<>();
          for (DirectorySku sku : skus) {
            DirectorySkuManager directorySkuManager = toShopSkuManager(tenant, shop, sku, issueDate);
            managers.add(directorySkuManager);
          }
          directorySkuManagerDao.insert(tenant, managers);
        }
      }
      title = "发送门店商品完成消息";
      sendShopSkuFinishMsg(tenant, taskId);
      title = "删除今天之前的商品信息";
      directorySkuManagerDao.deleteBeforeDate(tenant, issueDate);

    } catch (Exception e) {
      String msg = title + " 发生异常";
      log.error(msg, e);
      h6TaskService.logError(tenant, taskId, title, e, operateInfo);
      throw e;
    }
  }

  @Override
  protected DirectorySkuStartMsg decodeMessage(String msg) throws BaasException {
    log.info("收到ShopSkuStartMsg:{}", msg);
    return BaasJSONUtil.safeToObject(msg, DirectorySkuStartMsg.class);
  }

  private DirectorySkuManager toShopSkuManager(String tenant, DirectoryShop shop, DirectorySku sku, Date executeDate) {
    DirectorySkuManager directorySkuManager = new DirectorySkuManager();
    directorySkuManager.setTenant(tenant);
    directorySkuManager.setSkuId(sku.getSkuId());
    directorySkuManager.setSkuCode(sku.getCode());
    directorySkuManager.setSkuName(sku.getName());
    directorySkuManager.setSkuQpc(sku.getQpc());
    directorySkuManager.setSkuGid(sku.getGid());
    directorySkuManager.setDirectoryRequired(sku.isDirectoryRequired());
    directorySkuManager.setChannelRequired(sku.isChannelRequired());
    directorySkuManager.setShop(shop.getShop());
    directorySkuManager.setShopCode(shop.getShopCode());
    directorySkuManager.setShopName(shop.getShopName());
    directorySkuManager.setIssueDate(executeDate);
    return directorySkuManager;
  }

  private void sendShopSkuFinishMsg(String tenant, String taskId) {
    ShopSkuFinishMsg msg = new ShopSkuFinishMsg();
    msg.setTenant(tenant);
    msg.setCreateDate(new Date());
    msg.setTaskId(taskId);
    msg.setTraceId(UUID.randomUUID().toString().replace("-", ""));
    publisher.publishForNormal(DirectorySkuFinishedEvCallExecutor.SHOP_SKU_FINISHED_EXECUTOR_ID, msg);
  }
}
