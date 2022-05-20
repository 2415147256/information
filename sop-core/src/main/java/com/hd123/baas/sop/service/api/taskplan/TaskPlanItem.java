package com.hd123.baas.sop.service.api.taskplan;

import java.math.BigDecimal;

import com.hd123.baas.sop.service.api.TenantStandardEntity;

import lombok.Getter;
import lombok.Setter;

/**
 * @author guyahui
 * @date 2021/5/24 13:30
 */
@Getter
@Setter
public class TaskPlanItem extends TenantStandardEntity {
  private static final long serialVersionUID = 1233604746904512400L;

  // 任务计划ID
  private String owner;
  // 附件列表
  private String attachFiles;
  // 内容
  private String comment;
  // 分数
  private BigDecimal point = BigDecimal.ZERO;
  // 是否审核
  private Boolean audit;
  // 执行人列表
  private String assignees;
  // 门店列表
  private String shops;
}
