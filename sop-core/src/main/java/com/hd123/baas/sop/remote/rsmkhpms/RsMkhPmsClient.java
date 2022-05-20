package com.hd123.baas.sop.remote.rsmkhpms;

import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.hd123.baas.sop.remote.rsmkhpms.entity.BBasePricePromListReq;
import com.hd123.baas.sop.remote.rsmkhpms.entity.BBasePricePromRes;
import com.hd123.baas.sop.remote.rsmkhpms.entity.BStateReason;
import com.qianfan123.baas.common.BaasException;
import com.qianfan123.baas.common.http2.BaasResponse;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

/**
 * @author liuhaoxin
 */
@FeignClient(name = "mkhpms-service")
@RequestMapping(produces = "application/json;charset=utf-8", consumes = "application/json;charset=utf-8")
public interface RsMkhPmsClient {

  /**
   * TODO 查詢促銷模型,終止促銷模型
   */
  @ApiOperation(value = "促销单列表", notes = "根据uuids查列表")
  @PostMapping("rest/v1/{tenant}/prom/list")
  BaasResponse<List<BBasePricePromRes>> list(@PathVariable("tenant") String tenant,
      @RequestBody BBasePricePromListReq req) throws BaasException;

  @ApiOperation(value = "终止", notes = "终止门店促销功能")
  @PostMapping(value = "rest/v1/{tenant}/prom/termination")
  BaasResponse<Boolean> terminate(@ApiParam(value = "租户标识", required = true) @RequestHeader("tenant") String tenant,
      @RequestBody BStateReason req) throws BaasException;

  @ApiOperation(value = "作废", notes = "作废促销规则")
  @PostMapping("rest/v1/{tenant}/prom/canceled")
  BaasResponse<Boolean> canceled(@PathVariable("tenant") String tenant, @RequestBody BStateReason req)
      throws BaasException;

  @ApiOperation(value = "详情", notes = "根据uuid查详情")
  @GetMapping("rest/v1/{tenant}/prom/get")
  BaasResponse<BBasePricePromRes> get(@PathVariable("tenant") String tenant, @RequestParam("uuid") String uuid)
      throws BaasException;
}
