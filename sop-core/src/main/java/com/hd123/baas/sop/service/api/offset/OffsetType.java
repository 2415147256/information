package com.hd123.baas.sop.service.api.offset;

/**
 * @author zhengzewang on 2020/11/17.
 */
public enum OffsetType {

  MESSAGE, // 消息
  PRICE_ADJUSTMENT, // 价格调整单（试算单）
  GRADE_ADJUSTMENT, // 门店价格级调整单
  PRICE_PROMOTION, // 到店价促销单
  TEMP_PRICE_ADJUSTMENT, // 临时改价单
  H6TASK, // 任务
  TASK_GROUP, // 巡检主题
  TASK_PLAN, // 巡检计划
  FAQ_CATEGORY, // FAQ分类
  FAQ_ARTICLE, // FAQ文章
  ASSIGNABLE_TASK_PLAN, // 普通任务计划
  SKU_PUBLISH_PLAN,// 商品上下架方案
  EXPLOSIVE, //爆品活动
  EXPLOSIVE_PLAN //爆品计划
}
