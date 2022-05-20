/**
 * 版权所有(C)，上海海鼎信息工程股份有限公司，2020，所有权利保留。
 * <p>
 * 项目名： workspace_mkh
 * 文件名： GoodsPriceTaskType.java
 * 模块说明：
 * 修改历史：
 * 2020年11月21日 - huangjunxian - 创建。
 */
package com.hd123.baas.sop.remote.rsh6sop.price;

/**
 * 下发任务类型。
 *
 * @author huangjunxian
 * @since 1.0
 */
public enum GoodsPriceTaskType {
  /** 价格任务：到店价、促销到店价、售价 */
  price,
  /** 促销数据 */
  prom,
  /** 门店商品 */
  storeOnGoods;
}
