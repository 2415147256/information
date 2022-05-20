package com.hd123.baas.sop.service.api.taskplan.template;

import com.hd123.baas.sop.service.api.task.ShopTask;
import com.hd123.baas.sop.service.api.task.ShopTaskFeedbackInfo;
import com.hd123.baas.sop.service.api.task.ShopTaskService;
import com.hd123.baas.sop.remote.isvmkh.IsvMkhClient;
import com.hd123.baas.sop.remote.isvmkh.entity.OrderListResponse;
import com.hd123.baas.sop.remote.isvmkh.entity.OrderState;
import com.hd123.baas.sop.remote.jwt.TlspClient;
import com.hd123.baas.sop.utils.JsonUtil;
import com.qianfan123.baas.common.BaasException;
import com.qianfan123.baas.common.http2.BaasResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
public class MKHOrderCheckAllTemplateClsTaskPlan implements TemplateClsTaskPlan {

  @Autowired
  private IsvMkhClient isvMkhClient;

  @Autowired
  private ShopTaskService shopTaskService;

  @Autowired
  private TlspClient tlspClient;

  @Override
  public String getName() {
    return "线上订单核销检查";
  }

  @Override
  public String getDescription() {
    return "请确认全部线上订单已全部核销完成。可登录电商平台查看具体核销情况";
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
  public void check(String tenant, Object date) throws Exception {
    ShopTaskFeedbackInfo info = (ShopTaskFeedbackInfo) date;
    String taskId = info.getTaskId();
    ShopTask shopTask = shopTaskService.get(tenant, taskId);
    if (shopTask == null) {
      throw new BaasException("任务不存在:{}", taskId);
    }
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
    String dateString = sdf.format(new Date());

    BaasResponse<OrderListResponse> mkhOrderListResponse = isvMkhClient.orderList(getToken(tenant, info.getAppId()), 1,
        100, OrderState.DELIVERED, null, shopTask.getShopCode(), dateString);
    log.info("明康汇query response:{}", JsonUtil.objectToJson(mkhOrderListResponse));
    if (mkhOrderListResponse.getCode() != 0) {
      log.error("查询明康汇订单失败,msg:{}", mkhOrderListResponse.getMsg());
      throw new Exception("查询订单失败,msg:" + mkhOrderListResponse.getMsg());
    }
    if (CollectionUtils.isNotEmpty(mkhOrderListResponse.getData().getList())) {
      throw new BaasException("您当前有{0}张订单未核销，是否已和客户确认全部手动核销", mkhOrderListResponse.getData().getList().size());
    }
  }

  private String getToken(String tenant, String appId) {
    Map<String, Object> map = new HashMap<>();
    map.put("tenant", tenant);
    BaasResponse<String> response = tlspClient.jwtSign(tenant, appId, map);
    return "Bearer " + response.getData();
  }
}
