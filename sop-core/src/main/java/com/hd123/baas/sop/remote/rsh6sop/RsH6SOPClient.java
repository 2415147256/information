/**
 * 版权所有(C)，上海海鼎信息工程股份有限公司，2020，所有权利保留。
 * <p>
 * 项目名：	sos_idea
 * 文件名：	RsStoreInvXFReqClient.java
 * 模块说明：
 * 修改历史：
 * 2020/11/2 - Leo - 创建。
 */

package com.hd123.baas.sop.remote.rsh6sop;

import java.util.List;

import com.hd123.baas.sop.remote.rsh6sop.explosivev2.HotActivity;
import com.hd123.baas.sop.remote.rsh6sop.feedback.RsH6Feedback;
import com.hd123.baas.sop.remote.rsh6sop.sku.publishplan.ShelveSchemeOff;
import org.springframework.cloud.openfeign.FeignClient;

import com.hd123.baas.sop.remote.rsh6sop.activity.H6SopActivity;
import com.hd123.baas.sop.remote.rsh6sop.activity.H6SopActivityAbort;
import com.hd123.baas.sop.remote.rsh6sop.explosive.StoreExplosive;
import com.hd123.baas.sop.remote.rsh6sop.explosive.StoreExplosiveId;
import com.hd123.baas.sop.remote.rsh6sop.fcf.H6ProcessOrder;
import com.hd123.baas.sop.remote.rsh6sop.feedback.RsH6FeedbackFeeGenerateTime;
import com.hd123.baas.sop.remote.rsh6sop.fineRule.FineRule;
import com.hd123.baas.sop.remote.rsh6sop.goods.H6GoodsAlcQpc;
import com.hd123.baas.sop.remote.rsh6sop.goods.H6GoodsQueryFilter;
import com.hd123.baas.sop.remote.rsh6sop.inv.AvailableInv;
import com.hd123.baas.sop.remote.rsh6sop.inv.AvailableInvFilter;
import com.hd123.baas.sop.remote.rsh6sop.invxfapply.RsInvXFApply;
import com.hd123.baas.sop.remote.rsh6sop.invxfapply.RsInvXFApplyAuditor;
import com.hd123.baas.sop.remote.rsh6sop.invxfapply.RsInvXFApplyAutoAudit;
import com.hd123.baas.sop.remote.rsh6sop.invxfapply.RsInvXFApplyQueryFilter;
import com.hd123.baas.sop.remote.rsh6sop.invxfapply.RsInvXFApplyRejection;
import com.hd123.baas.sop.remote.rsh6sop.ordadvlmtdate.OrdAdvLmtDate;
import com.hd123.baas.sop.remote.rsh6sop.price.RSGoodsOrderQty;
import com.hd123.baas.sop.remote.rsh6sop.price.RSGoodsOrderQueryFilter;
import com.hd123.baas.sop.remote.rsh6sop.price.RSGoodsPriceTaskCreation;
import com.hd123.baas.sop.remote.rsh6sop.price.adj.RSSalePriceTmpAdjCreation;
import com.hd123.baas.sop.remote.rsh6sop.prom.RSPromotionBill;
import com.hd123.baas.sop.remote.rsh6sop.rprcgrp.RprcGrp;
import com.hd123.baas.sop.remote.rsh6sop.rprcgrp.RprcGrpQueryFilter;
import com.hd123.baas.sop.remote.rsh6sop.rprcgrp.StoreRprcGrpModification;
import com.hd123.baas.sop.remote.rsh6sop.sku.publishplan.AvgReqQty;
import com.hd123.baas.sop.remote.rsh6sop.sku.publishplan.AvgReqQtyFilter;
import com.hd123.baas.sop.remote.rsh6sop.sku.publishplan.ShelveScheme;
import com.hd123.baas.sop.remote.rsh6sop.skutag.GdTag;
import com.hd123.baas.sop.remote.rsh6sop.skutag.StoreGdTag;
import com.hd123.baas.sop.remote.rsh6sop.store.SimpleStore;
import com.hd123.baas.sop.remote.rsh6sop.store.StoreFilter;
import com.hd123.baas.sop.remote.rsh6sop.storeprom.StorePromAlcPrcBill;
import com.hd123.baas.sop.remote.rsh6sop.subsidyplan.StorePromPlan;
import com.hd123.baas.sop.remote.rsh6sop.subsidyplan.StorePromPlanRelate;
import com.hd123.baas.sop.remote.rsh6sop.systemconfig.H6SystemConfig;
import com.hd123.baas.sop.remote.rsh6sop.user.RsLoginPwdModification;
import com.hd123.baas.sop.remote.rsh6sop.user.RsLoginPwdModifyResponse;
import com.hd123.baas.sop.remote.rsh6sop.wrh.SimpleWarehouse;
import com.hd123.baas.sop.remote.rsh6sop.wrh.WarehouseFilter;
import com.qianfan123.baas.common.http2.BaasResponse;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * H6-SOP接口服务
 *
 * @author Leo
 */
@FeignClient(name = "h6-sop", configuration = RsH6SOPConfiguration.class)
@RequestMapping(produces = "application/json;charset=utf-8", consumes = "application/json;charset=utf-8")
public interface RsH6SOPClient {

  @ApiOperation(value = "用户修改密码")
  @PostMapping(value = "h6-sop/v1/sop/user/password/modify")
  BaasResponse<RsLoginPwdModifyResponse> modifyPwd(
      @ApiParam(value = "租户标识", required = true) @RequestHeader("tenant") String tenant,
      @ApiParam(value = "密码修改对象", required = true) @RequestBody RsLoginPwdModification modification);

  @ApiOperation(value = "查询门店调拨申请单单据")
  @PostMapping(value = "h6-sop/v1/sop/invxf/apply/query")
  BaasResponse<List<RsInvXFApply>> invXFQuery(
      @ApiParam(value = "租户标识", required = true) @RequestHeader("tenant") String tenant,
      @ApiParam(value = "查询定义", required = true) @RequestBody RsInvXFApplyQueryFilter queryFilter);

  @ApiOperation(value = "获取指定门店调拨申请单单据")
  @GetMapping(value = "h6-sop/v1/sop/invxf/apply/{num}")
  BaasResponse<RsInvXFApply> invXFGet(@ApiParam(value = "租户标识", required = true) @RequestHeader("tenant") String tenant,
      @ApiParam(value = "单据标识", required = true) @PathVariable("num") String num);

  @ApiOperation(value = "门店调拨申请单审核不通过")
  @PostMapping(value = "h6-sop/v1/sop/invxf/apply/reject")
  BaasResponse<Void> invXFReject(@ApiParam(value = "租户标识", required = true) @RequestHeader("tenant") String tenant,
      @ApiParam(value = "拒绝对象", required = true) @RequestBody RsInvXFApplyRejection rejection);

  @ApiOperation(value = "门店调拨申请单审核通过")
  @PostMapping(value = "h6-sop/v1/sop/invxf/apply/audit")
  BaasResponse<Void> invXFAudit(@ApiParam(value = "租户标识", required = true) @RequestHeader("tenant") String tenant,
      @ApiParam(value = "审批对象", required = true) @RequestBody RsInvXFApplyAuditor auditor);

  @ApiOperation(value = "门店调拨申请单保存自动审批配置")
  @PostMapping(value = "h6-sop/v1/sop/invxf/apply/autoaudit/save")
  BaasResponse<Void> saveAutoAudit(@ApiParam(value = "租户标识", required = true) @RequestHeader("tenant") String tenant,
      @ApiParam(value = "调拨申请自动审批配置", required = true) @RequestBody RsInvXFApplyAutoAudit autoAudit);

  @ApiOperation(value = "收货质量反馈保存费用生成时间配置")
  @PostMapping(value = "h6-sop/v1/sop/receipt/feedback/feegeneratetime/save")
  BaasResponse<Void> feedbackSaveFeeGenerateTime(
      @ApiParam(value = "租户标识", required = true) @RequestHeader("tenant") String tenant,
      @ApiParam(value = "费用生成时间配置对象", required = true) @RequestBody RsH6FeedbackFeeGenerateTime generateTime);

  @ApiOperation(value = "同步已审核收货质量反馈单")
  @PostMapping(value = "h6-sop/v1/sop/receipt/feedback/accept")
  BaasResponse<Void> feedbackAccept(@ApiParam(value = "租户标识", required = true) @RequestHeader("tenant") String tenant,
      @ApiParam(value = "收货质量反馈单", required = true) @RequestBody RsH6Feedback feedback);

  @ApiOperation(value = "获取单品单店指定时间的订货数")
  @PostMapping(value = "h6-sop/v1/sop/goods/price/order/qty/list")
  BaasResponse<List<RSGoodsOrderQty>> listOrderQty(
      @ApiParam(value = "租户标识", required = true) @RequestHeader("tenant") String tenant,
      @ApiParam(value = "查询过滤器", required = true) @RequestBody RSGoodsOrderQueryFilter queryFilter);

  @ApiOperation(value = "创建价格下发任务")
  @PostMapping(value = "h6-sop/v1/sop/goods/price/task/create")
  BaasResponse<Void> createTask(@ApiParam(value = "租户标识", required = true) @RequestHeader("tenant") String tenant,
      @ApiParam(value = "查询过滤器", required = true) @RequestBody RSGoodsPriceTaskCreation creation);

  @ApiOperation(value = "创建售价临时调价单")
  @PostMapping(value = "h6-sop/v1/sop/goods/price/sale/tmp/adj/create")
  BaasResponse<Void> createTmpAdj(@ApiParam(value = "租户标识", required = true) @RequestHeader("tenant") String tenant,
      @ApiParam(value = "查询过滤器", required = true) @RequestBody RSSalePriceTmpAdjCreation creation);

  @ApiOperation(value = "查询默认配货规格")
  @PostMapping(value = "h6-sop/v1/goods/defAlcQpc/list")
  BaasResponse<List<H6GoodsAlcQpc>> queryDefAlcQpc(
      @ApiParam(value = "租户标识", required = true) @RequestHeader("tenant") String tenant,
      @ApiParam(value = "查询过滤器", required = true) @RequestBody H6GoodsQueryFilter queryFilter);

  @ApiOperation(value = "保存已审核或已作废的促销单")
  @PostMapping(value = "h6-sop/v1/sop/prom/bill/save")
  BaasResponse<Void> savePromBill(@ApiParam(value = "租户标识", required = true) @RequestHeader("tenant") String tenant,
      @ApiParam(value = "促销单", required = true) @RequestBody RSPromotionBill bill);

  @ApiOperation(value = "创建门店爆品预订")
  @PostMapping(value = "h6-sop/v1/explosive/store/create")
  BaasResponse<Void> createStoreExplosive(
      @ApiParam(value = "租户标识", required = true) @RequestHeader("tenant") String tenant,
      @ApiParam(value = "促销单", required = true) @RequestBody StoreExplosive storeExplosive);

  @ApiOperation(value = "创建门店爆品预订")
  @PostMapping(value = "h6-sop/v1/explosive/abort")
  BaasResponse<Void> abortExplosive(@ApiParam(value = "租户标识", required = true) @RequestHeader("tenant") String tenant,
      @ApiParam(value = "促销单", required = true) @RequestBody StoreExplosiveId explosiveId);

  @ApiOperation(value = "保存促销活动")
  @PostMapping(value = "h6-sop/v1/sop/prom/activity/save")
  BaasResponse<Void> saveActivity(@ApiParam(value = "租户标识", required = true) @RequestHeader("tenant") String tenant,
      @ApiParam(value = "促销活动", required = true) @RequestBody H6SopActivity activity);

  @ApiOperation(value = "作废促销活动")
  @PostMapping(value = "h6-sop/v1/sop/prom/activity/abort")
  BaasResponse<Void> abortActivity(@ApiParam(value = "租户标识", required = true) @RequestHeader("tenant") String tenant,
      @ApiParam(value = "促销活动", required = true) @RequestBody H6SopActivityAbort abortData);

  @ApiOperation(value = "查询价格组")
  @PostMapping(value = "h6-sop/v1/sop/rprcgrp/query")
  BaasResponse<List<RprcGrp>> queryRPriceGroup(
      @ApiParam(value = "租户标识", required = true) @RequestHeader("tenant") String tenant,
      @ApiParam(value = "查询过滤器", required = true) @RequestBody RprcGrpQueryFilter queryFilter);

  @ApiOperation(value = "获取指定门店价格组")
  @GetMapping(value = "h6-sop/v1/sop/store/rprcgrp/get/{storeUuid}")
  BaasResponse<RprcGrp> getRPriceGroupByStore(
      @ApiParam(value = "租户标识", required = true) @RequestHeader("tenant") String tenant,
      @ApiParam(value = "门店标识", required = true) @PathVariable("storeUuid") String num);

  @ApiOperation(value = "修改门店价格组")
  @PostMapping(value = "h6-sop/v1/sop/store/rprcgrp/modify")
  BaasResponse<RsLoginPwdModifyResponse> modifyRPriceGroup(
      @ApiParam(value = "租户标识", required = true) @RequestHeader("tenant") String tenant,
      @ApiParam(value = "修改对象", required = true) @RequestBody StoreRprcGrpModification modification);

  @ApiOperation(value = "罚款规则保存")
  @PostMapping(value = "h6-sop/v1/sop/fineRule/save")
  BaasResponse<Void> fineRuleSave(@ApiParam(value = "租户标识", required = true) @RequestHeader("tenant") String tenant,
      @ApiParam(value = "罚息规则", required = true) @RequestBody FineRule fineRule);

  @ApiOperation(value = "保存建议叫货单开关")
  @PostMapping(value = "h6-sop/v1/sop/receipt/ordadvlmtdate/accept")
  BaasResponse<Void> ordAdvLmtDateAccept(
      @ApiParam(value = "租户标识", required = true) @RequestHeader("tenant") String tenant,
      @ApiParam(value = "建议叫货", required = true) @RequestBody OrdAdvLmtDate ordAdvLmtDate);

  @ApiOperation(value = "修改门店价格组")
  @PostMapping(value = "h6-sop/v1/sop/freshfood/pscp/upload")
  BaasResponse<RsLoginPwdModifyResponse> uploadFreshFood(
      @ApiParam(value = "租户标识", required = true) @RequestHeader("tenant") String tenant,
      @ApiParam(value = "加工单", required = true) @RequestBody H6ProcessOrder processOrder);

  @ApiOperation(value = "到店价促销保存")
  @PostMapping(value = "h6-sop/v1/sop/storepromalcprcbill/save")
  BaasResponse<Void> promBillSave(@ApiParam(value = "租户标识", required = true) @RequestHeader("tenant") String tenant,
      @ApiParam(value = "到店价促销", required = true) @RequestBody StorePromAlcPrcBill bill);

  @ApiOperation(value = "到店价促销终止")
  @PostMapping(value = "h6-sop/v1/sop/storepromalcprcbill/abort")
  BaasResponse<Void> billAbort(@ApiParam(value = "租户标识", required = true) @RequestHeader("tenant") String tenant,
      @ApiParam(value = "到店价促销Id", required = true) @RequestBody String promId);

  @ApiOperation(value = "保存补贴计划")
  @PostMapping(value = "h6-sop/v1/sop/storepromplan/save")
  BaasResponse<Void> promPlanSave(@ApiParam(value = "租户标识", required = true) @RequestHeader("tenant") String tenant,
      @ApiParam(value = "门店补贴计划", required = true) @RequestBody StorePromPlan plan);

  @ApiOperation(value = "终止补贴计划")
  @GetMapping(value = "h6-sop/v1/sop/storepromplan/abort/{planId}")
  BaasResponse<Void> planabort(@ApiParam(value = "租户标识", required = true) @RequestHeader("tenant") String tenant,
      @ApiParam(value = "门店补贴计划Id", required = true) @PathVariable("planId") String planId);

  @ApiOperation(value = "清空活动关联")
  @PostMapping(value = "h6-sop/v1/sop/storepromplanrelate/clear")
  BaasResponse<Void> planClear(@ApiParam(value = "租户标识", required = true) @RequestHeader("tenant") String tenant,
      @ApiParam(value = "补贴计划id", required = true) @RequestBody String planId);

  @ApiOperation(value = "保存活动关联")
  @PostMapping(value = "h6-sop/v1/sop/storepromplanrelate/upload")
  BaasResponse<Void> upload(@ApiParam(value = "租户标识", required = true) @RequestHeader("tenant") String tenant,
      @ApiParam(value = "关联关系", required = true) @RequestBody List<StorePromPlanRelate> relates);

  @ApiOperation(value = "保存一条活动关联")
  @PostMapping(value = "h6-sop/v1/sop/storepromplanrelate/uploadOne")
  BaasResponse<Void> uploadOne(@ApiParam(value = "租户标识", required = true) @RequestHeader("tenant") String tenant,
      @ApiParam(value = "关联关系", required = true) @RequestBody StorePromPlanRelate relate);

  @ApiOperation(value = "接收SOP系统配置")
  @PostMapping(value = "h6-sop/v1/sop/systemconfig/save")
  BaasResponse<Void> systemConfigSave(@ApiParam(value = "租户标识", required = true) @RequestHeader("tenant") String tenant,
      @ApiParam(value = "关联关系", required = true) @RequestBody H6SystemConfig config);

  @ApiOperation(value = "商品标签-保存")
  @PostMapping(value = "h6-sop/v1/gdtag/save")
  BaasResponse<Void> tagSave(@ApiParam(value = "租户标识", required = true) @RequestHeader("tenant") String tenant,
      @ApiParam(value = "商品标签", required = true) @RequestBody GdTag gdTag);

  @ApiOperation(value = "商品标签-删除")
  @PostMapping(value = "h6-sop/v1/gdtag/remove")
  BaasResponse<Void> tagRemove(@ApiParam(value = "租户标识", required = true) @RequestHeader("tenant") String tenant,
      @ApiParam(value = "商品标签ID", required = true) @RequestBody String tagId);

  @ApiOperation(value = "门店商品标签-上传")
  @PostMapping(value = "h6-sop/v1/storegdtag/upload")
  BaasResponse<Void> skuShopTagsUpload(
      @ApiParam(value = "租户标识", required = true) @RequestHeader("tenant") String tenant,
      @ApiParam(value = "门店商品标签", required = true) @RequestBody List<StoreGdTag> storeGdTags);

  @ApiOperation(value = "库存查询")
  @PostMapping(value = "h6-sop/v1/sop/availableinv/query")
  BaasResponse<List<AvailableInv>> invQuery(
      @ApiParam(value = "租户标识", required = true) @RequestHeader("tenant") String tenant,
      @ApiParam(value = "上下架库存", required = true) @RequestBody AvailableInvFilter queryFilter);

  @ApiOperation(value = "仓位查询")
  @PostMapping(value = "h6-sop/v1/sop/warehouse/query")
  BaasResponse<List<SimpleWarehouse>> wrhQuery(
      @ApiParam(value = "租户标识", required = true) @RequestHeader("tenant") String tenant,
      @ApiParam(value = "仓位", required = true) @RequestBody WarehouseFilter queryFilter);

  @ApiOperation(value = "组织/门店查询")
  @PostMapping(value = "h6-sop/v1/sop/store/query")
  BaasResponse<List<SimpleStore>> storeQuery(
      @ApiParam(value = "租户标识", required = true) @RequestHeader("tenant") String tenant,
      @ApiParam(value = "仓位", required = true) @RequestBody StoreFilter queryFilter);

  @ApiOperation(value = "商品方案下架")
  @PostMapping(value = "h6-sop/v1/sop/shelvescheme/off")
  BaasResponse<Void> publishOff(@ApiParam(value = "租户标识", required = true) @RequestHeader("tenant") String tenant,
      @ApiParam(value = "仓位", required = true) @RequestBody ShelveSchemeOff off);

  @ApiOperation(value = "商品方案上架")
  @PostMapping(value = "h6-sop/v1/sop/shelvescheme/on")
  BaasResponse<Void> publishOn(@ApiParam(value = "租户标识", required = true) @RequestHeader("tenant") String tenant,
      @ApiParam(value = "仓位", required = true) @RequestBody ShelveScheme scheme);

  @ApiOperation(value = "日均叫货量查询")
  @PostMapping(value = "h6-sop/v1/sop/requireorder/listAvgReqQty")
  BaasResponse<List<AvgReqQty>> listAvgReqQty(
      @ApiParam(value = "租户标识", required = true) @RequestHeader("tenant") String tenant,
      @ApiParam(value = "门店商品上架", required = true) @RequestBody AvgReqQtyFilter avgReqQtyFilter);

  @ApiOperation(value = "爆品活动(通用)-上架")
  @PostMapping(value = "h6-sop/v1/hotactivity/on")
  BaasResponse<Void> explosiveOn(
      @ApiParam(value = "租户标识", required = true) @RequestHeader("tenant") String tenant,
      @ApiParam(value = "爆品活动对象", required = true) @RequestBody HotActivity hotActivity);
}
