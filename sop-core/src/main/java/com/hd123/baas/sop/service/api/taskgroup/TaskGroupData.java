package com.hd123.baas.sop.service.api.taskgroup;

import com.hd123.baas.sop.service.api.TenantStandardEntity;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 任务组
 *
 * @author 申敏
 * @date 2022/1/6
 */
@Getter
@Setter
//任务组
public class TaskGroupData extends TenantStandardEntity {
  private static final long serialVersionUID = -9164110976852790646L;
  //任务组名称
  private String name;
  //任务组类型
  private String type;
  //模板数量
  private BigDecimal count;
  //总分
  private BigDecimal score;
  //创建时间
  private Date created;
  //状态
  private String state;
  //巡检主题编码
  private String code;
}