package com.hd123.baas.sop.service.api.taskplan.template;

import com.hd123.baas.config.core.BaasConfigClient;
import com.hd123.baas.sop.service.api.task.ShopTaskFeedbackInfo;
import com.hd123.baas.sop.service.api.task.TurnoverBoxCheckInfo;
import com.hd123.baas.sop.service.api.task.ShopTask;
import com.hd123.baas.sop.service.api.task.ShopTaskService;
import com.hd123.baas.sop.configuration.FeignClientMgr;
import com.hd123.baas.sop.remote.rsIwms.BassIwmsConfig;
import com.hd123.baas.sop.remote.rsIwms.RsIwmsClient;
import com.hd123.baas.sop.remote.rsIwms.RsrecycleStoreContainerReq;
import com.hd123.baas.sop.remote.rsIwms.RsrecycleStoreContainerRes;
import com.hd123.baas.sop.utils.IdGenUtils;
import com.qianfan123.baas.common.BaasException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author zhengzewang on 2020/11/3.
 */
@Service
@Slf4j
public class TurnoverBoxCheckTemplateClsTaskPlan implements TemplateClsTaskPlan {

  @Autowired
  FeignClientMgr feignClientMgr;

  @Autowired
  private BaasConfigClient configClient;
  @Autowired
  private ShopTaskService shopTaskService;

  @Override
  public String getName() {
    return "周转箱登记";
  }

  @Override
  public String getDescription() {
    return "记录下门店今日到的周转箱个数及全部在店的周转箱个数，并上传周转箱的图片。保护门店财产，人人有责。";
  }

  @Override
  public boolean wordNeeded() {
    return true;
  }

  @Override
  public boolean imageNeeded() {
    return true;
  }

  @Override
  public void check(String tenant, Object date) throws BaasException {
    ShopTaskFeedbackInfo info = (ShopTaskFeedbackInfo) date;

    List<TurnoverBoxCheckInfo> turnoverBoxCheckInfos = info.getTurnoverBoxCheckInfos();
    if (CollectionUtils.isEmpty(turnoverBoxCheckInfos)) {
      return;
    }
    ShopTask shopTask = shopTaskService.get(tenant, info.getTaskId());
    BassIwmsConfig config = configClient.getConfig(shopTask.getTenant(), BassIwmsConfig.class);
    RsrecycleStoreContainerReq req = new RsrecycleStoreContainerReq();
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
    req.setStoreCode(shopTask.getShopCode());
    sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    req.setSendTime(sdf.format(new Date()));
    req.setDcCode(config.getDcCode());
    req.setSource("SOP-SERVICE");
    List<RsrecycleStoreContainerReq.Line> lines = new ArrayList<>();
    for (TurnoverBoxCheckInfo item : turnoverBoxCheckInfos) {
      RsrecycleStoreContainerReq.Line line = new RsrecycleStoreContainerReq.Line();
      line.setQty(item.getQty());
      line.setContainerType(item.getContainerType());
      lines.add(line);
    }
    req.setContainerTypeReturnInfos(lines);
    String traceId = MDC.get("trace_id");
    if (StringUtils.isEmpty(traceId)) {
      traceId = IdGenUtils.buildIidAsString();
    }
    req.setTraceId(traceId);
    RsrecycleStoreContainerRes response = null;
    try {
      RsIwmsClient rsIwmsClient = feignClientMgr.getClient(shopTask.getTenant(), shopTask.getShop(),
          RsIwmsClient.class);
      response = rsIwmsClient.recycleStoreContainer(config.getCompanyUuid(), req);
    } catch (Exception e) {
      log.error("调用店回收容器反馈接⼝失败:", e);
      throw new BaasException("调用店回收容器反馈接⼝失败");
    }

    if (!response.getSuccess()) {
      throw new BaasException("周转箱登记未完成");
    }
  }

  public static void main(String[] args) {

  }
}
