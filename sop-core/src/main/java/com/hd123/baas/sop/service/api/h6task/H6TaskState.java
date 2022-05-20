package com.hd123.baas.sop.service.api.h6task;

/**
 * @author zhengzewang on 2020/11/23.
 */
public enum H6TaskState {

  INIT, // 待执行
  CONFIRMED, // 计算中
  DELIVERED, // 已发货，表示文件已生成
  FINISHED, // 已完成。表示已下载，目前暂时不需要

}
