package com.hd123.baas.sop.service.api.task;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * @author zhengzewang on 2020/11/3.
 * 
 *         门店日结完成信息
 * 
 */
@Getter
@Setter
public class ShopTaskFeedbackInfo {

  //任务id
  private String taskId;
  //图片
  private List<String> images;
  //文字反馈
  private String remark;
  //周转箱信息
  private List<TurnoverBoxCheckInfo> turnoverBoxCheckInfos;
  //appId
  private String appId;

}
