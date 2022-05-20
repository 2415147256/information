package com.hd123.baas.sop.remote.rsias;

import com.hd123.baas.sop.remote.rsias.inv.*;
import com.qianfan123.baas.common.http.QueryRequest;
import io.swagger.annotations.ApiParam;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.List;

@FeignClient(name = "ias-service", url = "${ias-service.url:}", configuration = RsIasConfiguration.class)
@RequestMapping(produces = "application/json;charset=utf-8", consumes = "application/json;charset=utf-8")
public interface RsIasClient {

  /**
   * 库存查询-V1
   *
   * @param tenant  租户
   * @param request 查询请求QR
   * @return IAS库存信息列表
   */
  @RequestMapping(value = "v1/{tenant}/inv/query", method = RequestMethod.POST)
  RsIasPageResponse<List<RsInv>> invQuery(@ApiParam(required = true) @PathVariable("tenant") String tenant,
                                          @ApiParam(required = true) @RequestBody QueryRequest request);

  /**
   * 库存调整-V1
   *
   * @param tenant
   * @param invAdjustReq 库存调整请求
   * @return RsIasResponse
   */
  @RequestMapping(value = "v1/{tenant}/inv/adjust", method = RequestMethod.POST)
  RsIasResponse<Void> invAdjust(@ApiParam(required = true) @PathVariable("tenant") String tenant,
                                @ApiParam(required = true) @RequestBody RsInvAdjustReq invAdjustReq);

  /**
   * 库存售罄-V1
   *
   * @param tenant          租户
   * @param invStockoutReqs 库存售罄请求
   * @return RsIasResponse
   */
  @RequestMapping(value = "v1/{tenant}/inv/stockout", method = RequestMethod.POST)
  RsIasResponse<Void> invStockout(@ApiParam(required = true) @PathVariable("tenant") String tenant,
                                  @ApiParam(required = true) @RequestBody List<RsInvStockoutReq> invStockoutReqs);

  /**
   * 库存同步-V1
   *
   * @param tenant     租户
   * @param invSyncReq 库存同步请求
   * @return RsIasResponse
   */
  @RequestMapping(value = "v1/{tenant}/inv/sync", method = RequestMethod.POST)
  RsIasResponse<Void> invSync(@ApiParam(required = true) @PathVariable("tenant") String tenant,
                              @ApiParam(required = true) @RequestBody RsInvSyncReq invSyncReq);

  /**
   * 渠道履约方库存查询-V2
   *
   * @param tenant       租户
   * @param invWrhSkuReq 渠道履约方库存查询请求
   * @return IAS库存信息列表
   */
  @RequestMapping(value = "v2/{tenant}/inv/queryWrhSkuInv", method = RequestMethod.POST)
  RsIasPageResponse<List<RsInv>> invQueryV2(@ApiParam(required = true) @PathVariable("tenant") String tenant,
                                            @ApiParam(required = true) @RequestBody RsInvWrhSkuReq invWrhSkuReq);

  /**
   * 库存调整-V2
   *
   * @param tenant       租户
   * @param invAdjustReq 库存调整请求
   * @return RsIasResponse
   */
  @RequestMapping(value = "v2/{tenant}/inv/adjust", method = RequestMethod.POST)
  RsIasResponse<Void> invAdjustV2(@ApiParam(required = true) @PathVariable("tenant") String tenant,
                                  @ApiParam(required = true) @RequestBody RsInvAdjustReq invAdjustReq);

  /**
   * 库存售罄-V2
   *
   * @param tenant          租户
   * @param invStockoutReqs 库存售罄请求
   * @return RsIasResponse
   */
  @RequestMapping(value = "v2/{tenant}/inv/stockout", method = RequestMethod.POST)
  RsIasResponse<Void> invStockoutV2(@ApiParam(required = true) @PathVariable("tenant") String tenant,
                                    @ApiParam(required = true) @RequestBody List<RsInvStockoutReq> invStockoutReqs);

  /**
   * 库存同步-V2
   *
   * @param tenant     租户
   * @param invSyncReq 库存同步请求
   * @return RsIasResponse
   */
  @RequestMapping(value = "v2/{tenant}/inv/sync", method = RequestMethod.POST)
  RsIasResponse<Void> invSyncV2(@ApiParam(required = true) @PathVariable("tenant") String tenant,
                                @ApiParam(required = true) @RequestBody RsInvSyncReq invSyncReq);
}
