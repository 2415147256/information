package com.hd123.baas.sop.service.api.task;

import com.hd123.baas.sop.service.api.TenantStandardEntity;
import com.hd123.rumba.commons.biz.entity.OperateInfo;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * @Author maodapeng
 * @Since
 */
@Setter
@Getter
public class ShopTaskLog extends TenantStandardEntity {
  // 记录名称
  private String name;

  // 门店任务Id
  private String owner;

  // 分值
  private BigDecimal point = BigDecimal.ZERO;
  // 分值说明
  private String pointDesc;

  // 得分
  private BigDecimal score;

  private OperateInfo finishInfo;
  // 完成设备
  private String finishAppId;

  // 经办人ID
  private String operatorId;

  // 经办人姓名
  private String operatorName;

  // 完成情况
  private String state = ShopTaskState.UNFINISHED.name();

  // 反馈
  private String feedback;

  // 类型,AUDIT,REPLY
  private String type;

  //备注
  private String note;
  // 需要文字反馈
  private boolean wordNeeded;
  // 需要图片反馈
  private boolean imageNeeded;
  // 需要视频反馈
  private boolean videoNeeded;

}
