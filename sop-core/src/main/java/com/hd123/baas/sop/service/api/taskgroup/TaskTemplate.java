package com.hd123.baas.sop.service.api.taskgroup;

import com.hd123.baas.sop.service.api.TenantStandardEntity;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.math.BigDecimal;

/**
 * @author guyahui
 * @date 2021/4/29 19:58
 */
@Getter
@Setter
@ToString
public class TaskTemplate extends TenantStandardEntity {

  private static final long serialVersionUID = 4416129989670370893L;
  // 巡检主题ID
  private String owner;
  // 类
  private String templateClass;
  // 名称
  private String name;
  // 模版分值
  private BigDecimal score;
  // 内容
  private String content;
  // 业务ID
  private String flowNo;
  //备注
  private String note;
  // 需要文字反馈
  private boolean wordNeeded;
  // 需要图片反馈
  private boolean imageNeeded;
  // 需要视频反馈
  private boolean videoNeeded;
  // 排序
  private int seq = 1;

}
