package com.hd123.baas.sop.service.api.task;

import lombok.Getter;
import lombok.Setter;

/**
 * 门店任务交接详情请求
 * 
 * @author shenmin
 * @date 2022/1/5 15:31
 */
@Setter
@Getter
public class ShopTaskTransferDetailReq {

  //交接人ID
  private String transferFrom;

  //任务详情ID
  private String shopTaskLogId;

  //接受人ID
  private String transferTo;

  //请求类型,transferTo,transferFrom
  private String type;
}
