/**
 * 版权所有(C)，上海海鼎信息工程股份有限公司，2020，所有权利保留。
 * <p>
 * 项目名：	sos_idea
 * 文件名：	RsStoreInvXFReqClient.java
 * 模块说明：
 * 修改历史：
 * 2020/11/2 - Leo - 创建。
 */

package com.hd123.baas.sop.remote.rssos;

import com.hd123.baas.sop.remote.rssos.feedback.BSOPFeedbackEndTimeSaver;
import com.hd123.baas.sop.remote.rssos.feedback.BSOPFeedbackGradeSaver;
import com.hd123.baas.sop.remote.rssos.feedback.BSOPFeedbackReasonSaver;
import com.hd123.baas.sop.remote.rssos.feedback.RsFeedback;
import com.hd123.baas.sop.remote.rssos.invxf.RsInvXFDayCheck;
import com.hd123.baas.sop.remote.rssos.receipt.ReceiptOption;
import com.hd123.baas.sop.remote.rssos.receipt.RsReceiptDayCheck;
import com.hd123.baas.sop.remote.rssos.require.RequirePeriodOption;
import com.hd123.baas.sop.service.api.feedback.FeedbackApproval;
import com.hd123.baas.sop.service.api.feedback.FeedbackCreation;
import com.hd123.baas.sop.service.api.feedback.FeedbackGdSearchResult;
import com.hd123.baas.sop.service.api.feedback.FeedbackOption;
import com.hd123.baas.sop.service.api.feedback.FeedbackRejection;
import com.qianfan123.baas.common.http.QueryRequest;
import com.qianfan123.baas.common.http2.BaasResponse;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * SOS接口服务
 *
 * @author Leo
 */
@FeignClient(name = "sos-service", configuration = RsSOSConfiguration.class)
@RequestMapping(produces = "application/json;charset=utf-8", consumes = "application/json;charset=utf-8")
public interface RsSOSClient {

  @ApiOperation(value = "收货检查")
  @PostMapping(value = "/v1/sop/receipt/check")
  BaasResponse<Boolean> receiptCheck(@ApiParam(value = "租户标识", required = true) @RequestHeader("tenant") String tenant,
                                     @ApiParam(value = "组织id", required = false) @RequestHeader("orgUuid") String orgId,
                                     @ApiParam(value = "门店标识", required = true) @RequestHeader("shop") String shop,
                                     @ApiParam(value = "请求体", required = true) @RequestBody RsReceiptDayCheck dayCheck);

  @ApiOperation(value = "调拨检查")
  @PostMapping(value = "/v1/sop/invxf/check")
  BaasResponse<Boolean> invxfCheck(@ApiParam(value = "租户标识", required = true) @RequestHeader("tenant") String tenant,
                                   @ApiParam(value = "组织id", required = false) @RequestHeader("orgUuid") String orgId,
                                   @ApiParam(value = "门店标识", required = true) @RequestHeader("shop") String shop,
                                   @ApiParam(value = "请求体", required = true) @RequestBody RsInvXFDayCheck dayCheck);

  @ApiOperation(value = "获取指定质量反馈单")
  @PostMapping(value = "/v1/sop/feedback/get")
  BaasResponse<RsFeedback> feedbackGet(
      @ApiParam(value = "租户标识", required = true) @RequestHeader("tenant") String tenant,
      @ApiParam(value = "门店标识", required = true) @RequestHeader("shop") String shop,
      @ApiParam(value = "单据标识", required = true) @RequestBody String id);

  @ApiOperation(value = "可质量反馈商品查询")
  @PostMapping(value = "/v1/sop/feedback/search")
  BaasResponse<List<FeedbackGdSearchResult>> feedbackSearch(
      @ApiParam(value = "租户标识", required = true) @RequestHeader("tenant") String tenant,
      @ApiParam(value = "门店标识", required = true) @RequestHeader("shop") String shop,
      @ApiParam(value = "查询定义", required = true) @RequestBody QueryRequest request);

  @ApiOperation(value = "质量反馈单审核通过")
  @PostMapping(value = "/v1/sop/feedback/audit")
  BaasResponse<Void> feedbackAudit(@ApiParam(value = "租户标识", required = true) @RequestHeader("tenant") String tenant,
                                   @ApiParam(value = "门店标识", required = true) @RequestHeader("shop") String shop,
                                   @ApiParam(value = "APP标识", required = true) @RequestHeader("appId") String appId,
                                   @ApiParam(value = "反馈通过对象", required = true) @RequestBody FeedbackApproval approval);

  @ApiOperation(value = "质量反馈单审核拒绝")
  @PostMapping(value = "/v1/sop/feedback/reject")
  BaasResponse<Void> feedbackReject(@ApiParam(value = "租户标识", required = true) @RequestHeader("tenant") String tenant,
                                    @ApiParam(value = "门店标识", required = true) @RequestHeader("shop") String shop,
                                    @ApiParam(value = "APP标识", required = true) @RequestHeader("appId") String appId,
                                    @ApiParam(value = "反馈拒绝对象", required = true) @RequestBody FeedbackRejection rejection);

  @ApiOperation(value = "质量反馈单创建提交")
  @PostMapping(value = "/v1/sop/feedback/createAndSubmit")
  BaasResponse<String> feedbackCreateAndSubmit(
      @ApiParam(value = "租户标识", required = true) @RequestHeader("tenant") String tenant,
      @ApiParam(value = "门店标识", required = true) @RequestHeader("shop") String shop,
      @ApiParam(value = "门店代码", required = true) @RequestHeader("shopNo") String shopNo,
      @ApiParam(value = "门店名称", required = true) @RequestHeader("shopName") String shopName,
      @ApiParam(value = "APP标识", required = true) @RequestHeader("appId") String appId,
      @ApiParam(value = "创建对象", required = true) @RequestBody FeedbackCreation creation);

  @ApiOperation(value = "质量反馈单保存选项配置")
  @PostMapping(value = "/v1/sop/feedback/option/save")
  BaasResponse<String> feedbackSaveOption(
      @ApiParam(value = "租户标识", required = true) @RequestHeader("tenant") String tenant,
      @ApiParam(value = "组织id", required = false) @RequestHeader("orgUuid") String orgId,
      @ApiParam(value = "选项", required = true) @RequestBody FeedbackOption option);

  @ApiOperation(value = "质量反馈截止时间保存")
  @PostMapping(value = "/v1/sop/feedback/endTime/save")
  BaasResponse<Void> feedbackSaveEndTime(
      @ApiParam(value = "租户标识", required = true) @RequestHeader("tenant") String tenant,
      @ApiParam(value = "组织id", required = false) @RequestHeader("orgUuid") String orgId,
      @ApiParam(value = "保存对象", required = true) @RequestBody BSOPFeedbackEndTimeSaver saver);

  @ApiOperation(value = "质量反馈原因保存")
  @PostMapping(value = "/v1/sop/feedback/applyReason/save")
  BaasResponse<Void> feedbackSaveApplyReason(
      @ApiParam(value = "租户标识", required = true) @RequestHeader("tenant") String tenant,
      @ApiParam(value = "原因列表", required = true) @RequestBody List<String> reasons);

  // todo mdp
  @ApiOperation(value = "按类别代码保存申请原因")
  @PostMapping(value = "v1/sop/feedback/applyReasonWithSort/save")
  BaasResponse<Void> feedbackSaveApplyReasonWithSort(
      @ApiParam(value = "租户标识", required = true) @RequestHeader("tenant") String tenant,
      @ApiParam(value = "组织id", required = true) @RequestHeader("orgUuid") String orgUuid,
      @ApiParam(value = "反馈原因", required = true) @RequestBody BSOPFeedbackReasonSaver saver);

  @ApiOperation(value = "按类别代码保存申请原因")
  @GetMapping(value = "v1/{tenant}/feedback/applyReason/listBySort")
  BaasResponse<List<String>> feedbackApplyReasonList(
      @ApiParam(value = "租户标识", required = true) @PathVariable("tenant") String tenant,
      @ApiParam(value = "组织标识", required = true) @RequestHeader("orgUuid") String orgUuid,
      @ApiParam(value = "原因列表,0-申请原因 1-同意原因 2-拒绝原因", required = true) @RequestParam("type") Integer type,
      @ApiParam(value = "类别代码", required = true) @RequestParam("sortCode") String sortCode);

  @ApiOperation(value = "要货时间范围选项保存接口")
  @PostMapping(value = "/v1/sop/require/apply/periodOption/save")
  BaasResponse<Void> requireSavePeriodOption(@ApiParam(value = "租户标识", required = true) @RequestHeader("tenant") String tenant,
                                             @ApiParam(value = "组织标识", required = true) @RequestHeader("orgUuid") String orgUuid,
                                             @ApiParam(value = "选项", required = true) @RequestBody RequirePeriodOption option);

  @ApiOperation(value = "门店加单要货原因保存接口")
  @PostMapping(value = "/v1/sop/require/apply/applyAddReason/save")
  BaasResponse<Void> requireSaveApplyAddReason(
      @ApiParam(value = "租户标识", required = true) @RequestHeader("tenant") String tenant,
      @ApiParam(value = "组织标识", required = true) @RequestHeader("orgUuid") String orgUuid,
      @ApiParam(value = "原因列表", required = true) @RequestBody List<String> reasons);

  @ApiOperation(value = "收货单实收是否允许大于实配数保存选项配置")
  @PostMapping(value = "/v1/sop/receipt/option/save")
  BaasResponse<Void> receiptSaveOption(
      @ApiParam(value = "租户标识", required = true) @RequestHeader("tenant") String tenant,
      @ApiParam(value = "组织标识", required = true) @RequestHeader("orgUuid") String orgUuid,
      @ApiParam(value = "选项", required = true) @RequestBody ReceiptOption option);

  @ApiOperation(value = "门店要货报损原因保存接口")
  @PostMapping(value = "/v1/sop/loss/apply/applyReason/save")
  BaasResponse<Void> requireSaveApplyLossReason(
      @ApiParam(value = "租户标识", required = true) @RequestHeader("tenant") String tenant,
      @ApiParam(value = "组织标识", required = true) @RequestHeader("orgUuid") String orgUuid,
      @ApiParam(value = "原因列表", required = true) @RequestBody List<String> reasons);

  @ApiOperation(value = "门店要货报溢原因保存接口")
  @PostMapping(value = "/v1/sop/over/apply/applyReason/save")
  BaasResponse<Void> requireSaveApplyOverReason(
      @ApiParam(value = "租户标识", required = true) @RequestHeader("tenant") String tenant,
      @ApiParam(value = "组织标识", required = true) @RequestHeader("orgUuid") String orgUuid,
      @ApiParam(value = "原因列表", required = true) @RequestBody List<String> reasons);

  @ApiOperation(value = "批量保存质量反馈等级")
  @PostMapping(value = "/v1/sop/feedback/grade/save")
  BaasResponse<Void> feedbackGradeSave(
      @ApiParam(value = "租户标识", required = true) @RequestHeader("tenant") String tenant, @RequestHeader("orgUuid") String orgUuid,
      @ApiParam(value = "原因列表", required = true) @RequestBody BSOPFeedbackGradeSaver gradesSaver);
}
