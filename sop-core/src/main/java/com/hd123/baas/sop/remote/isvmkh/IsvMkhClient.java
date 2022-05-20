package com.hd123.baas.sop.remote.isvmkh;

import com.hd123.baas.sop.remote.isvmkh.entity.OrderListResponse;
import com.qianfan123.baas.common.http2.BaasResponse;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "baas-gateway", configuration = BaseRemoteConfiguration.class)
@Component
@RequestMapping(produces = "application/json;charset=utf-8", consumes = "application/json;charset=utf-8")
public interface IsvMkhClient {

  @ApiOperation(value = "订单列表查询接口")
  @GetMapping("/mkhds/order-app/thirdparty/order/list")
  BaasResponse<OrderListResponse> orderList(@RequestHeader("Authorization") String authorization,
      @ApiParam("当前页码") @RequestParam("current") int current, @ApiParam("每页条数，默认20") @RequestParam("size") int size,
      @ApiParam("订单状态") @RequestParam("orderState") int orderState,
      @ApiParam("提货人手机号") @RequestParam("phone") String phone,
      @ApiParam("门店编码") @RequestParam("shopCode") String shopCode,
      @ApiParam("提货日期") @RequestParam("takeDate") String takeDate);

  @ApiOperation(value = "核销接口")
  @GetMapping("/mkhds/order-app/thirdparty/order/verification")
  BaasResponse<Void> verification(@RequestHeader("Authorization") String authorization,
      @ApiParam(value = "订单编号") @RequestParam("orderId") long orderId,
      @ApiParam("门店编码") @RequestParam("shopCode") String shopCode);

}
