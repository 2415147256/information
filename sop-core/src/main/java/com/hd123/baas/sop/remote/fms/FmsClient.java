package com.hd123.baas.sop.remote.fms;

import com.hd123.baas.sop.configuration.BaseConfiguration;
import com.hd123.baas.sop.remote.fms.bean.AppMessageSaveNewReq;
import com.hd123.baas.sop.remote.fms.bean.AppMessageUnReadCheckReq;
import com.hd123.baas.sop.remote.fms.bean.AppMsgSNReq;
import com.hd123.baas.sop.remote.fms.bean.BAppMessage;
import com.hd123.baas.sop.remote.fms.bean.BAppMessageSummary;
import com.hd123.baas.sop.remote.fms.bean.FmsMsg;
import com.qianfan123.baas.common.http2.BaasResponse;
import com.qianfan123.baas.common.http.QueryRequest;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * @author zhengzewang
 */
@FeignClient(name = "fms-service", configuration = BaseConfiguration.class)
@RequestMapping(produces = "application/json;charset=utf-8", consumes = "application/json;charset=utf-8")
public interface FmsClient {

  @ApiOperation("模板消息发送")
  @PostMapping("v1/{tenant}/template/message/send")
  BaasResponse<Void> send(@PathVariable("tenant") String tenant, @RequestBody FmsMsg msg);

  @ApiOperation("模板消息保存并发送")
  @PostMapping("v1/{tenant}/template/message/sendAndSave")
  BaasResponse<Void> sendAndSave(@PathVariable("tenant") String tenant, @RequestParam("topic") String topic,
      @RequestBody FmsMsg msg);

  @ApiOperation("钉钉工作台消息发送")
  @PostMapping("v2/{tenant}/message/dingtalk/corpConversation/singlePushByTemplate")
  BaasResponse<Void> dingtalkSend(@PathVariable("tenant") String tenant, @RequestBody FmsMsg msg);

  @ApiOperation(value = "创建消息")
  @PostMapping(value = "v1/{tenant}/app/message/batchSave")
  BaasResponse<Void> batchSave(@PathVariable("tenant") String tenant,
      @RequestHeader("orgId") String orgId, @RequestBody List<AppMessageSaveNewReq> messageReq);

  @ApiOperation(value = "未读消息校验", notes = "是否有未读消息")
  @PostMapping(value = "v1/{tenant}/app/message/unread/check")
  BaasResponse<Boolean> check(@PathVariable("tenant") String tenant,
      @RequestBody AppMessageUnReadCheckReq req);

  @ApiOperation(value = "消息未读数", notes = "支持的查询条件："//
      + "\ncreated:[,]")
  @PostMapping(value = "v1/{tenant}/app/message/summary")
  BaasResponse<BAppMessageSummary> summary(
      @PathVariable("tenant") String tenant,
      @RequestParam("shop") String shop,
      @RequestParam("appId") String appId,
      @RequestParam("loginId") String loginId,
      @RequestBody QueryRequest request);

  @ApiOperation(value = "消息查询，默认倒叙排序", notes = "支持的查询条件：" + //
      "\nshop:=门店等于" + //
      "\ntype:=类型等于" //
      + "\nsource:=来源等于" //
      + "\ncreated:[,]" //
      + "\nuserId:=用户ID等于" //
      + "\nownership:[,]消息归属[shopId, loginId, appId] 右边值为空传null\n")
  @PostMapping(value = "v1/{tenant}/app/message/query")
  BaasResponse<List<BAppMessage>> query(
      @PathVariable("tenant") String tenant,
      @RequestBody QueryRequest request);

  @ApiOperation(value = "查询")
  @GetMapping(value = "v1/{tenant}/app/message/{uuid}")
  BaasResponse<BAppMessage> get(@PathVariable("tenant") String tenant, @PathVariable("uuid") String uuid);

  @ApiOperation(value = "阅读")
  @PostMapping(value = "v1/{tenant}/app/message/read")
  BaasResponse<Void> read(
      @PathVariable("tenant") String tenant,
      @RequestParam(value = "appId", required = false) String appId,
      @RequestParam(value = "userName", required = false) String userName,
      @RequestParam("userId") String userId,
      @RequestParam("uuid") String uuid);

  @ApiOperation(value = "阅读全部")
  @PostMapping(value = "v1/{tenant}/app/message/readAll")
  BaasResponse<Void> readAll(@PathVariable("tenant") String tenant,
      @RequestParam(value = "appId", required = false) String appId,
      @RequestParam(value = "userName", required = false) String userName,
      @RequestParam("shop") String shopId,
      @RequestParam("userId") String userId,
      @RequestParam("type") String type);

  @ApiOperation(value = "创建消息-v2")
  @PostMapping(value = "/rest/v2/{tenant}/app/message/batchSave")
  BaasResponse<Void> batchSaveV2(@PathVariable("tenant") String tenant,
      @RequestHeader("orgId") String orgId,
      @RequestBody List<AppMsgSNReq> reqs);
}
