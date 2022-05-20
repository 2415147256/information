package com.hd123.baas.sop.remote.fms;

import com.hd123.baas.sop.configuration.BaseConfiguration;
import com.hd123.baas.sop.remote.fms.bean.AppMsgReadBatchReq;
import com.hd123.baas.sop.remote.fms.bean.AppMsgReadReq;
import com.hd123.baas.sop.remote.fms.bean.BAppMsgV2;
import com.hd123.baas.sop.remote.fms.bean.BMsgPushLog;
import com.hd123.baas.sop.remote.fms.bean.BMsgPushTemplate;
import com.hd123.baas.sop.remote.fms.bean.VoiceMsgPushReq;
import com.qianfan123.baas.common.http.QueryRequest;
import com.qianfan123.baas.common.http2.BaasResponse;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(name = "fms-service", configuration = BaseConfiguration.class)
@RequestMapping(produces = "application/json;charset=utf-8", consumes = "application/json;charset=utf-8")
public interface FmsV2Client {

  @ApiOperation("推送批量语音通知")
  @PostMapping("v2/{tenant}/message/voice/batchPushByTemplate")
  BaasResponse<Void> batchPushVoiceByTemplate(@PathVariable("tenant") String tenant, @RequestBody List<VoiceMsgPushReq> reqList);

  @PostMapping("v2/{tenant}/message/voice/log/query")
  @ApiOperation(value = "语音通知日志结果查询")
  BaasResponse<List<BMsgPushLog>> queryLog(@PathVariable("tenant") String tenant, @RequestBody QueryRequest queryRequest);

  @GetMapping("v2/{tenant}/msgTemplate/getByCode")
  @ApiOperation(value = "根据code查询消息模板")
  BaasResponse<BMsgPushTemplate> getTemplateByCode(@PathVariable("tenant") String tenant, @RequestParam("code") String code);

  @PostMapping(value = "/rest/v2/{tenant}/app/message/query")
  BaasResponse<List<BAppMsgV2>> queryAppMsg(
      @PathVariable("tenant") String tenant,
      @RequestBody QueryRequest request);

  @ApiOperation(value = "阅读全部")
  @PostMapping(value = "/rest/v2/{tenant}/app/message/setAllRead")
  BaasResponse<Void> readAll(
      @PathVariable("tenant") String tenant,
      @RequestBody AppMsgReadBatchReq req);

  @ApiOperation(value = "阅读")
  @PostMapping(value = "/rest/v2/{tenant}/app/message/setRead")
  BaasResponse<Void> read(
      @PathVariable("tenant") String tenant,
      @RequestBody AppMsgReadReq req);

  @ApiOperation(value = "详情")
  @GetMapping(value = "/rest/v2/{tenant}/app/message/{uuid}")
  BaasResponse<BAppMsgV2> get(
      @PathVariable("tenant") String tenant,
      @PathVariable("uuid") String uuid);
}
