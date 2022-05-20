package com.hd123.baas.sop.evcall.exector.goodsprm;

import com.hd123.baas.config.core.BaasConfigClient;
import com.hd123.baas.sop.service.api.h6task.H6Task;
import com.hd123.baas.sop.configuration.FeignClientMgr;
import com.hd123.baas.sop.remote.rsh6sop.RsH6SOPClient;
import com.hd123.baas.sop.remote.rsh6sop.RsH6SOPConfig;
import com.hd123.baas.sop.remote.rsh6sop.price.GoodsPriceTaskType;
import com.hd123.baas.sop.remote.rsh6sop.price.RSGoodsPriceTaskCreation;
import com.hd123.baas.sop.utils.JsonUtil;
import com.hd123.rumba.evcall.EvCallExecutionContext;
import com.hd123.rumba.evcall.EvCallExecutor;
import com.hd123.rumba.evcall.EvCallManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.validation.constraints.NotNull;
import java.util.Date;

/**
 * @author zhengzewang on 2020/11/18.
 */
@Slf4j
@Component(GoodsPrmPriceFinishedEvCallExecutor.BEAN_ID)
public class GoodsPrmPriceFinishedEvCallExecutor implements EvCallExecutor {
  public static final String BEAN_ID = "sop.GoodsPrmPriceFinishedEvCallExecutor";

  @Autowired
  private EvCallManager evCallManager;
  @Autowired
  private FeignClientMgr feignClientMgr;
  @Autowired
  private BaasConfigClient configClient;

  @PostConstruct
  public void init() {
    evCallManager.addExecutor(this, BEAN_ID);
  }

  @Override
  public void execute(String json, @NotNull EvCallExecutionContext evCallExecutionContext) throws Exception {
    log.info("GoodsPrmPriceFinishedEvCallExecutor:{}", json);
    H6Task task = JsonUtil.jsonToObject(json, H6Task.class);
    if (task == null || task.getFileUrl() == null || task.getTenant() == null) {
      return;
    }
    // 是否开启下发
    RsH6SOPConfig config = configClient.getConfig(task.getTenant(), RsH6SOPConfig.class);
    if (Boolean.FALSE.equals(config.isEnabled())) {
      log.info("商品促销价完成未开启h6-sop推送配置:任务url:{},商品价格任务类型:{},任务ID:{},", task.getFileUrl(), GoodsPriceTaskType.prom, task.getUuid());
      return;
    }

    try {
      RSGoodsPriceTaskCreation data = new RSGoodsPriceTaskCreation();
      data.setFileUrl(task.getFileUrl());
      data.setType(GoodsPriceTaskType.prom);
      data.setOccurredTime(new Date());
      data.setTaskId(task.getUuid());

      log.info("调用rsH6SOPClient.createTask\n{}", com.hd123.spms.commons.json.JsonUtil.objectToJson(data));
      RsH6SOPClient rsH6SOPClient = feignClientMgr.getClient(task.getTenant(), null, RsH6SOPClient.class);
      rsH6SOPClient.createTask(task.getTenant(), data);
    } catch (Exception e) {
      log.error("GoodsPrmPriceFinishedEvCallExecutor执行发生错误", e);
      throw e;
    }
  }
}
