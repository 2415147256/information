package com.hd123.baas.sop.remote.bigdata;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * 大数据接口服务
 *
 * @author shenmin
 */
@FeignClient(name = "bigdata", configuration = BigDataConfiguration.class)
@RequestMapping(produces = "application/json;charset=utf-8", consumes = "application/json;charset=utf-8")
public interface BigDataClient {

  @ApiOperation(value = "首页数据接口")
  @PostMapping(value = "/operation/api/homePage/coreIndex")
  CommonResult<CoreIndexResult> coreIndex(
      @ApiParam(value = "查询参数对象", required = true) @RequestBody CoreIndexDto coreIndexDto);

}
