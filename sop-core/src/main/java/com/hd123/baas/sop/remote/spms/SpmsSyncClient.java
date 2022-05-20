package com.hd123.baas.sop.remote.spms;

import com.hd123.spms.commons.bean.IdWrapper;
import com.hd123.spms.commons.bean.Response;
import com.hd123.spms.commons.exception.BizServiceException;
import com.hd123.spms.console.service.task.transfer.SpmsTransferTask;
import com.hd123.spms.sync.PromPlanJoinData;
import com.hd123.spms.sync.PromTransferDataV4;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Created by fangzhipeng on 2017/4/6.
 */
@FeignClient(name = "spms-sync-server", configuration = SpmsFeignConfig.class)
@RequestMapping(value = "/v4/{tenant_id}/promotionsyncservice/", produces = "application/json;charset=utf-8")
public interface SpmsSyncClient {

  @RequestMapping(value = "echo", method = RequestMethod.GET)
  @ResponseBody
  IdWrapper echo(@ApiParam("租户标识") @PathVariable("tenant_id") String tenantId) throws BizServiceException;

  @ApiOperation(value = "保存一个租户数据同步任务")
  @RequestMapping(value = "saveTask", method = RequestMethod.POST)
  @ResponseBody
  IdWrapper saveTask(
          @ApiParam("租户标识") @PathVariable("tenant_id") String tenantId,
          @RequestBody SpmsTransferTask transferTask) throws BizServiceException;

  @ApiOperation(value = "开始全量同步指定租户促销数据", notes = "启动后，会清空该租户对应来源的全部促销单，以待同步；全量更新期间，同步不会调用促销引擎缓存更新")
  @RequestMapping(value = "beginfull", method = RequestMethod.POST)
  @ResponseBody
  SpmsTransferTask beginFullSync(
          @ApiParam("租户标识") @PathVariable("tenant_id") String tenantId,
          @RequestBody SpmsTransferTask transferTask) throws BizServiceException;

  @ApiOperation(value = "指定租户促销数据全量同步结束", notes = "并发送促销引擎环境全量更新")
  @RequestMapping(value = "overfull", method = RequestMethod.POST)
  @ResponseBody
  Response overFullSync(
          @ApiParam("租户标识") @PathVariable("tenant_id") String tenantId,
          @RequestBody SpmsTransferTask transferTask) throws BizServiceException;

  @ApiOperation(value = "同步促销单")
  @RequestMapping(value = "sync", method = RequestMethod.POST)
  @ResponseBody
  Response sync(
          @ApiParam("租户标识") @PathVariable("tenant_id") String tenantId,
          @RequestBody PromTransferDataV4 transferData) throws BizServiceException;

  @ApiOperation(value = "同步促销执行", notes = "同步门店在指定促销条目下的执行计划")
  @RequestMapping(value = "plan/join", method = RequestMethod.POST)
  Response planJoin(
          @ApiParam("租户标识") @PathVariable("tenant_id") String tenantId,
          @RequestBody PromPlanJoinData data) throws BizServiceException;

  @ApiOperation(value = "删除/作废促销单", notes = "若促销单未审核，则删除；若促销单已审核则作废")
  @RequestMapping(value = "delete", method = RequestMethod.POST)
  @ResponseBody
  Response delete(
          @ApiParam("租户标识") @PathVariable("tenant_id") String tenantId,
          @ApiParam("数据来源") @RequestParam("transfersource") String transferSource,
          @ApiParam("待删除/作废的促销单") @RequestParam("id") String id);
}
