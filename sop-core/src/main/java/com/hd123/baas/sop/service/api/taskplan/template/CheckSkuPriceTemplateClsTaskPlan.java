package com.hd123.baas.sop.service.api.taskplan.template;

import com.hd123.baas.sop.service.api.electricscale.ElecScaleStateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author zhengzewang on 2020/11/3.
 */
@Service
public class CheckSkuPriceTemplateClsTaskPlan implements TemplateClsTaskPlan {

  @Autowired
  private ElecScaleStateService stateService;

  @Override
  public String getName() {
    return "检查商品主档已下发价格已传秤";
  }

  @Override
  public String getDescription() {
    return "请确认当前收银机的价格已更新成功并下发到电子秤。";
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
  public void check(String tenant, Object shopTask) {
    //该任务人工check
  }
}
