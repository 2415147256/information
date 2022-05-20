package com.hd123.baas.sop.fcf.service.api.process;

import com.hd123.baas.sop.fcf.controller.process.BProcessPlanOrder;
import com.hd123.baas.sop.fcf.controller.process.ConfirmedGoodsData;
import com.hd123.baas.sop.fcf.controller.process.ProcessPlanExecuteRequest;
import com.hd123.baas.sop.fcf.controller.process.ProcessPlanListConfirmedRequest;
import com.hd123.baas.sop.fcf.controller.process.ProcessPlanOverViewRequest;
import com.qianfan123.baas.common.BaasException;

import java.text.ParseException;
import java.util.Date;
import java.util.List;

/**
 * @author zhangweigang
 */
public interface ProcessPlanOrderService {

  /**
   * 门店当日智能制作计划概览
   *
   * @param tenant
   *          租户
   * @param storeCode
   *          门店代码
   * @return 制作计划概览
   */
  ProcessPlanOverView queryOverView(String tenant,Date date,  String storeCode);

  BProcessPlanOrder getDetail(String tenant, ProcessPlanOverViewRequest request)throws BaasException;

  ProcessPlanOrder queryOrderByStoreId(String tenant, String storeId);


  /**
   * 执行制作计划
   *
   * @param tenant
   *          租户
   * @param request
   *          执行计划请求
   * @return 执行后的制作单数据
   * @throws BaasException
   *           抛异常交由全局处理
   */
  String execute(String tenant, ProcessPlanExecuteRequest request) throws BaasException, ParseException;

  void batchSave(String tenant, List<ProcessPlanOrder> processPlanOrders, String operator) throws BaasException;

  /**
   * 列出已确认的商品信息
   *
   * @param tenant
   *          租户
   * @param request
   *          前段请求
   * @return 加工计划单
   * @throws BaasException
   *           异常
   */
  ConfirmedGoodsData queryConfirmedGoods(String tenant, ProcessPlanListConfirmedRequest request) throws BaasException;

  /**
   * 查询计划外商品
   *
   * @param tenant
   *          租户
   * @param uuid
   *          skuId
   * @return 计划外商品
   */
  List<UnPlanGoods> queryUnPlanGoods(String tenant, String uuid) throws BaasException;

  List<FreshMealTime> queryMealTimes(String tenant);

  void updateMealTime(String tenant, FreshMealTime update) throws BaasException;

  void insertMealTime(String tenant, FreshMealTime insert) throws BaasException;

  void deleteMealTime(String tenant, String delete) throws BaasException;

  List<ProcessPlanOrderLine> getProcessOrderLine(String tenant, String planId, String mealTimeId, String state);
}
