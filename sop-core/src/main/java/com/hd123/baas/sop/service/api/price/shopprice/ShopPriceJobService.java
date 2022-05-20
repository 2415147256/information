package com.hd123.baas.sop.service.api.price.shopprice;

import java.util.Collection;

import com.hd123.baas.sop.service.api.price.shopprice.bean.ShopPriceJob;
import com.hd123.baas.sop.service.api.price.shopprice.bean.ShopPriceJobState;
import com.hd123.rumba.commons.biz.entity.OperateInfo;
import com.hd123.rumba.commons.biz.query.QueryDefinition;
import com.hd123.rumba.commons.biz.query.QueryResult;
import com.qianfan123.baas.common.BaasException;

/**
 * @author zhengzewang on 2020/11/23.
 */
public interface ShopPriceJobService {

  /**
   * 新增门店价格job
   * 
   * @param tenant
   *          租户
   * @param priceJob
   *          job
   * @param operateInfo
   *          操作人信息
   */
  void saveNew(String tenant, ShopPriceJob priceJob, OperateInfo operateInfo) throws BaasException;

  /**
   * 批量新增门店价格job
   * 
   * @param tenant
   *          租户
   * @param priceJobs
   *          jobs
   * @param operateInfo
   *          操作人信息
   */
  void batchSaveNew(String tenant, Collection<ShopPriceJob> priceJobs, OperateInfo operateInfo) throws BaasException;

  /**
   * 完成任务
   * 
   * @param tenant
   *          租户
   * @param uuid
   *          job id
   * @param operateInfo
   *          操作人信息
   */
  void finish(String tenant, String uuid, OperateInfo operateInfo) throws BaasException;

  /**
   * 根据门店和任务id查询
   * 
   * @param tenant
   * @param shop
   * @param taskId
   */
  ShopPriceJob getByShopAndTask(String tenant, String shop, String taskId);

  /**
   * 是否全部完成
   * 
   * @param tenant
   *          租户
   * @param taskId
   *          任务id
   * @return 结果
   */
  long count(String tenant, String taskId, ShopPriceJobState state);

  /**
   * 自定义查询
   */
  QueryResult<ShopPriceJob> query(String tenant, QueryDefinition qd);

  /**
   * 更新状态
   */
  void logError(String tenant, String uuid, String title, Exception ex, OperateInfo operateInfo) throws BaasException;

}
