package com.hd123.baas.sop.service.api.taskplan.template;

import com.hd123.baas.sop.service.api.task.ShopTask;
import com.hd123.baas.sop.service.api.task.ShopTaskFeedbackInfo;
import com.hd123.baas.sop.service.api.task.ShopTaskService;
import com.hd123.baas.sop.common.DefaultOrgIdConvert;
import com.hd123.baas.sop.configuration.FeignClientMgr;
import com.hd123.baas.sop.remote.rssos.RsSOSClient;
import com.hd123.baas.sop.remote.rssos.receipt.RsReceiptDayCheck;
import com.qianfan123.baas.common.BaasException;
import com.qianfan123.baas.common.http2.BaasResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Calendar;
import java.util.Date;

/**
 * @author zhengzewang on 2020/11/3.
 */
@Service
@Slf4j
public class ReceiveCheckTemplateClsTaskPlan implements TemplateClsTaskPlan {

  @Autowired
  private ShopTaskService shopTaskService;
  @Autowired
  private FeignClientMgr feignClientMgr;

  @Override
  public String getName() {
    return "当日收货检查";
  }

  @Override
  public String getDescription() {
    return "此项任务需要您在收银机上的完成所有已到货单据的收货操作。操作流程：点击“店务”进入“门店收货”模块，对单据进行确认收货。";
  }

  @Override
  public boolean wordNeeded() {
    return false;
  }

  @Override
  public boolean imageNeeded() {
    return false;
  }

  @Override
  public void check(String tenant, Object date) throws BaasException {
    ShopTaskFeedbackInfo info = (ShopTaskFeedbackInfo) date;
    RsReceiptDayCheck check = new RsReceiptDayCheck();
    check.setEndTime(getEndTime());
    check.setStartTime(getStartTime());
    BaasResponse<Boolean> response = new BaasResponse<>();
    ShopTask shopTask = shopTaskService.get(tenant, info.getTaskId());
    try {
      RsSOSClient rsSOSClient = feignClientMgr.getClient(tenant, null, RsSOSClient.class);
      response = rsSOSClient. receiptCheck(shopTask.getTenant(), DefaultOrgIdConvert.toH6DefOrgId(shopTask.getOrgId(),false), shopTask.getShop(), check);
    } catch (Exception e) {
      log.error("调取sos收货检查接口异常:", e);
      throw new BaasException("调取sos收货检查接口异常");
    }

    if (response.getData()) {
      return;
    }
    throw new BaasException("收货检查失败,msg:{0}", response.getMsg());

  }

  private Date getStartTime() {
    Calendar todayStart = Calendar.getInstance();
    todayStart.set(Calendar.HOUR, 0);
    todayStart.set(Calendar.MINUTE, 0);
    todayStart.set(Calendar.SECOND, 0);
    todayStart.set(Calendar.MILLISECOND, 0);
    return todayStart.getTime();
  }

  private Date getEndTime() {
    Calendar todayEnd = Calendar.getInstance();
    todayEnd.set(Calendar.HOUR, 23);
    todayEnd.set(Calendar.MINUTE, 59);
    todayEnd.set(Calendar.SECOND, 59);
    todayEnd.set(Calendar.MILLISECOND, 999);
    return todayEnd.getTime();
  }

}
