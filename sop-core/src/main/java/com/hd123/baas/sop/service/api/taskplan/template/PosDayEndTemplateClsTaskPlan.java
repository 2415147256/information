package com.hd123.baas.sop.service.api.taskplan.template;

import com.qianfan123.baas.common.BaasException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * @author zhengzewang on 2020/11/3.
 */
@Service
@Slf4j
public class PosDayEndTemplateClsTaskPlan implements TemplateClsTaskPlan {


  @Override
  public String getName() {
    return "收银机日结";
  }

  @Override
  public String getDescription() {
    return "此项任务需要您完成收银机上的“门店日结”。操作流程：点击“店务”进入“门店日结”模块，根据操作提醒完成操作";
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
  public void check(String tenant, Object shopTask) throws BaasException {


  }
}
