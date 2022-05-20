package com.hd123.baas.sop.remote.tas;

import com.hd123.baas.sop.remote.tas.task.PriceAdjustTaskCsvReq;
import com.hd123.baas.sop.remote.tas.task.PriceAdjustTaskReq;
import com.hd123.baas.sop.remote.tas.task.SkuRetailTerminateTaskReq;
import com.hd123.baas.sop.remote.uas.BShop;
import com.hd123.baas.sop.remote.uas.BasicAuthConfiguration;
import com.qianfan123.baas.common.http.QueryRequest;
import com.qianfan123.baas.common.http2.BaasResponse;
import com.qianfan123.baas.common.validation.RequestTenantIgnoreValid;
import io.swagger.annotations.ApiParam;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

/**
 * @Author shenmin
 * @Since
 */
@FeignClient(name = "tas-service", configuration = BasicAuthConfiguration.class)
@RequestMapping(produces = "application/json;charset=utf-8", consumes = "application/json;charset=utf-8")
public interface TasClient {
  @RequestMapping(value = "/v1/{tenant}/employee/{id}/shops", method = RequestMethod.POST)
  @ResponseBody
  BaasResponse<List<BShop>> queryShop(@PathVariable("tenant") String tenant,
      @RequestHeader("orgId") String orgId,
      @PathVariable("id") String id,
      @RequestBody QueryRequest request);

  @RequestMapping(value = "/isv/h6/v1/{tenant}/task", method = RequestMethod.POST)
  @RequestTenantIgnoreValid
  @ResponseBody
  BaasResponse<Void> priceAdjustTaskSaveNew(@ApiParam(value = "租户ID", required = true) @PathVariable("tenant") String tenant,
      @ApiParam(value = "组织ID", required = true) @RequestHeader("orgId") String orgId,
      @RequestBody PriceAdjustTaskReq request);

  @RequestMapping(value = "/isv/h6/v1/{tenant}/task/createByCsv", method = RequestMethod.POST)
  @RequestTenantIgnoreValid
  @ResponseBody
  BaasResponse<Void> priceAdjustTaskCreateByCsv(@ApiParam(value = "租户ID", required = true) @PathVariable("tenant") String tenant,
      @ApiParam(value = "组织ID", required = true) @RequestHeader("orgId") String orgId,
      @RequestBody PriceAdjustTaskCsvReq request);

  @RequestMapping(value = "/isv/h6/v1/{tenant}/task/skuRetailTerminate", method = RequestMethod.POST)
  @RequestTenantIgnoreValid
  @ResponseBody
  BaasResponse<Void> skuRetailTerminate(@ApiParam(value = "租户ID", required = true) @PathVariable("tenant") String tenant,
      @ApiParam(value = "组织ID", required = true) @RequestHeader("orgId") String orgId,
      @RequestBody SkuRetailTerminateTaskReq request);

}
