package com.hd123.baas.sop.remote.rsdemo;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.qianfan123.baas.common.http2.BaasResponse;

/**
 * @author zhengzewang on 2020/10/28.
 */
@FeignClient(name = "demo", configuration = RsDemoConfiguration.class)
@RequestMapping(produces = "application/json;charset=utf-8", consumes = "application/json;charset=utf-8")
public interface RsDemoClient {

  @RequestMapping(value = "v1/{tenant}/remote/demo/test", method = RequestMethod.POST)
  BaasResponse<String> test(@PathVariable("tenant") String tenant, @RequestParam("userId") String userId);

}
