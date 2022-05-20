package com.hd123.baas.sop.service.api.taskplan.template;

import java.util.Calendar;
import java.util.Date;

import com.hd123.baas.sop.service.api.task.ShopTaskFeedbackInfo;
import com.hd123.baas.sop.common.DefaultOrgIdConvert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.hd123.baas.sop.service.api.task.ShopTask;
import com.hd123.baas.sop.service.api.task.ShopTaskService;
import com.hd123.baas.sop.configuration.FeignClientMgr;
import com.hd123.baas.sop.remote.rssos.RsSOSClient;
import com.hd123.baas.sop.remote.rssos.invxf.RsInvXFDayCheck;
import com.qianfan123.baas.common.BaasException;
import com.qianfan123.baas.common.http2.BaasResponse;

import lombok.extern.slf4j.Slf4j;

/**
 * @author zhengzewang on 2020/11/3.
 */
@Service
@Slf4j
public class TransferOrderCheckTemplateClsTaskPlan implements TemplateClsTaskPlan {

  @Autowired
  private ShopTaskService shopTaskService;
  @Autowired
  private FeignClientMgr feignClientMgr;

  @Override
  public String getName() {
    return "调拨单确认检查";
  }

  @Override
  public String getDescription() {
    return "此项任务需要您在收银机上的完成所有调拨单据的收货操作。操作流程：点击“店务”进入“门店调拨”模块，对单据进行确认收货。";
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
    RsInvXFDayCheck check = new RsInvXFDayCheck();
    check.setEndTime(getEndTime());
    check.setStartTime(getStartTime());
    BaasResponse<Boolean> response = new BaasResponse<>();
    ShopTask shopTask = shopTaskService.get(tenant, info.getTaskId());
    try {
      RsSOSClient rsSOSClient = feignClientMgr.getClient(tenant, null, RsSOSClient.class);
      response = rsSOSClient.invxfCheck(shopTask.getTenant(), DefaultOrgIdConvert.toH6DefOrgId(shopTask.getOrgId(), false), shopTask.getShop(), check);
    } catch (Exception e) {
      log.error("调取sos调拨检查接口异常:", e);
      throw new BaasException("调取sos调拨检查接口异常");
    }
    if (response.isSuccess() && response.getData()) {
      return;
    }
    throw new BaasException("调拨单确认检查,msg:{0}", response.getMsg());
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
