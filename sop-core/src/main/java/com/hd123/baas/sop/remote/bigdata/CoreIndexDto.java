package com.hd123.baas.sop.remote.bigdata;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * @author shenmin
 */
@Getter
@Setter
public class CoreIndexDto {
  //起始日期
  private String startDate;
  //截止日期
  private String endDate;
  //门店gid范围列表
  private List<String> storeList;
  //租户编号
  private String tenantCode;
}
