
package com.hd123.baas.sop.remote.screen;

import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import com.qianfan123.baas.common.http2.BaasResponse;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

/**
 * 运营平台价格屏对接服务。
 *
 * @author huangjunxian
 * @since 1.0
 */
@Api(tags = "v1-运营平台价格屏对接服务")
@FeignClient(name = "mkh-screen-service", url = "${mkh-screen-service.url:}",
    configuration = MkhScreenConfiguration.class)
@RequestMapping(produces = "application/json;charset=utf-8", consumes = "application/json;charset=utf-8")
public interface MkhScreenClient {
  @ApiOperation(value = "查询商品")
  @PostMapping(value = "/sop/v1/screen/goods/query")
  BaasResponse<List<RSGoods>> queryGoods(
      @ApiParam(value = "租户标识", required = true) @RequestHeader("tenant") String tenant,
      @ApiParam("查询过滤器") @RequestBody RSGoodsQueryFilter filter);

  @ApiOperation(value = "保存轮播间隔时间配置")
  @PostMapping(value = "/sop/v1/screen/seconds/save")
  BaasResponse<Void> savePriceOption(@ApiParam(value = "租户标识", required = true) @RequestHeader("tenant") String tenant,
      @ApiParam("配置") @RequestBody RSSecondsOption option);

  @ApiOperation(value = "保存轮播图配置")
  @PostMapping(value = "/sop/v1/screen/banner/save")
  BaasResponse<Void> saveBannerOption(@ApiParam(value = "租户标识", required = true) @RequestHeader("tenant") String tenant,
      @ApiParam("配置") @RequestBody RSBannerOption option);

  @ApiOperation(value = "保存广告语配置")
  @PostMapping(value = "/sop/v1/screen/tip/save")
  BaasResponse<Void> saveTipOption(@ApiParam(value = "租户标识", required = true) @RequestHeader("tenant") String tenant,
      @ApiParam("配置") @RequestBody RSTipOption option);

  @ApiOperation(value = "保存方案")
  @PostMapping(value = "/sop/v1/screen/scheme/save")
  BaasResponse<Void> saveScheme(@ApiParam(value = "租户标识", required = true) @RequestHeader("tenant") String tenant,
      @ApiParam("配置") @RequestBody RSScheme options);

  @ApiOperation(value = "终止方案")
  @PostMapping(value = "/sop/v1/screen/scheme/stop")
  BaasResponse<Void> stopScheme(@ApiParam(value = "租户标识", required = true) @RequestHeader("tenant") String tenant,
      @ApiParam("方案id") @RequestParam("id") String id);

  @ApiOperation(value = "保存轮播间隔时间配置V2")
  @PostMapping(value = "/sop/v1/screen/v2/seconds/save")
  BaasResponse<Void> saveSecondOption2(
      @ApiParam(value = "租户标识", required = true) @RequestHeader("tenant") String tenant,
      @ApiParam("配置") @RequestBody RSSecondsOptions options);
}
