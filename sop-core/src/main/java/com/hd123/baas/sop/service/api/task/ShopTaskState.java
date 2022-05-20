package com.hd123.baas.sop.service.api.task;

/**
 * @author zhengzewang on 2020/11/3.
 */
public enum ShopTaskState {
  FINISHED, // 已完成
  NOT_STARTED, // 未开始
  UNFINISHED, // 未完成/待完成
  EXPIRED, // 已过期
  TERMINATE, // 已终止
  SUBMITTED, // 已提交
  ALL // 所有的抽象概念，不包含已终止的所有任务
}
