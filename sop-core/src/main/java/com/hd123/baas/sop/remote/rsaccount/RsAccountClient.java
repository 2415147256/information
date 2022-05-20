package com.hd123.baas.sop.remote.rsaccount;

import com.hd123.baas.sop.remote.rsaccount.store.RSDailyClear;

import com.qianfan123.baas.common.http2.BaasResponse;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;


@FeignClient(name = "account", configuration = RsAccountConfiguration.class)
@RequestMapping(produces = "application/json;charset=utf-8", consumes = "application/json;charset=utf-8")
public interface RsAccountClient {


  @ApiOperation(value = "门店日清接口")
  @PostMapping(value = "/v1/store/dailyclear")
  BaasResponse<Object> dailyclear(@RequestBody RSDailyClear rsDailyClear);
}
