package com.hd123.baas.sop.remote.jwt;

import java.util.Map;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.qianfan123.baas.common.http2.BaasResponse;

import io.swagger.annotations.ApiOperation;

@FeignClient(name = "tlsp", url = "${sop-service.tlsp.url}", configuration = TlspAuthConfiguration.class)
@RequestMapping(produces = "application/json;charset=utf-8", consumes = "application/json;charset=utf-8")
public interface TlspClient {

  @ApiOperation("生成jwtToken")
  @PostMapping("service/sign/jwt")
  BaasResponse<String> jwtSign(@RequestParam("tenant") String tenant, @RequestParam("appId") String appId,
      @RequestBody Map<String, Object> tokenValues);
}
