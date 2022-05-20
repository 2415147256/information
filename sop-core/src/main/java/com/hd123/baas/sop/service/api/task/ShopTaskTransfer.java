package com.hd123.baas.sop.service.api.task;

import com.hd123.baas.sop.service.api.TenantEntity;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

/**
 * @author guyahui
 * @date 2021/5/20 14:00
 */
@Getter
@Setter
public class ShopTaskTransfer extends TenantEntity {

  private static final long serialVersionUID = 3596207727311889211L;
  // 任务详情ID
  private String shopTaskLogId;
  // 任务ID
  private String shopTaskId;
  // 店铺ID
  private String shop;
  // 店铺代码
  private String shopCode;
  // 店铺名称
  private String shopName;
  // 转出人ID
  private String transferFrom;
  // 转出人姓名
  private String transferFromName;
  // 转出时间
  private Date transferTime;
  // 接受人ID
  private String transferTo;
  // 接受人姓名
  private String transferToName;
  // 接受人岗位code
  private String transferToPositionCode;
  // 接受人岗位名称
  private String transferToPositionName;
  // 接受人操作时间
  private Date operTime;
  // 接受人备注
  private String reason;
  // 交接状态
  private ShopTaskTransferState state = ShopTaskTransferState.TRANSFER;
  //交接类型
  private String type;
  // 批量交接ID
  private String batchId;
}
