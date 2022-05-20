package com.hd123.baas.sop.remote.uas;

import com.hd123.baas.sop.excel.job.userposition.PositionBean;
import com.qianfan123.baas.common.BaasException;
import com.qianfan123.baas.common.http.QueryRequest;
import com.qianfan123.baas.common.http2.BaasResponse;
import io.swagger.annotations.ApiParam;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

/**
 * @Author maodapeng
 * @Since
 */
@FeignClient(name = "uas-service", configuration = BasicAuthConfiguration.class)
@RequestMapping(produces = "application/json;charset=utf-8", consumes = "application/json;charset=utf-8")
public interface UasClient {
  @RequestMapping(value = "/v1/{tenant}/user/query", method = RequestMethod.POST)
  @ResponseBody
  BaasResponse<List<BUser>> query(@ApiParam(value = "tenant,租户id") @PathVariable("tenant") String tenant,
      @ApiParam(value = "appId", required = true) @RequestParam("appId") String appId,
      @ApiParam(value = "查询条件") @RequestBody QueryRequest request) throws BaasException;

  @RequestMapping(value = "/v1/{tenant}/rest/service/user/query", method = RequestMethod.POST)
  @ResponseBody
  BaasResponse<List<BUser>> queryByLoginIds(@ApiParam(value = "tenant,租户id") @PathVariable("tenant") String tenant,
      @ApiParam(value = "查询条件") @RequestBody QueryRequest request) throws BaasException;

  /**
   * 岗位列表查询
   */
  @RequestMapping(value = "/v1/{tenant}/position/query", method = RequestMethod.POST)
  @ResponseBody
  BaasResponse<List<BPosition>> queryByCode(@ApiParam(value = "tenant,租户id") @PathVariable("tenant") String tenant,
      @RequestHeader(value = "orgId", defaultValue = "*") String orgId,
      @RequestHeader(value = "user_id") String userId,
      @ApiParam(value = "查询条件") @RequestBody QueryRequest request) throws BaasException;

  /**
   * 岗位批量插入
   */
  @RequestMapping(value = "/v1/{tenant}/position/batch/save", method = RequestMethod.POST)
  @ResponseBody
  BaasResponse<Void> batchSave(@PathVariable("tenant") String tenant,
      @RequestHeader(value = "orgId", defaultValue = "*") String orgId,
      @ApiParam(value = "岗位信息") @RequestBody List<PositionBean> req);

  /**
   * 查询用户岗位门店列表 查询用户岗位门店列表,支持查询条件，" + "keyword:%=%账号或名称" + "shopId:in门店id," + "positionId:in岗位id
   */
  @PostMapping("/v1/{tenant}/position/queryAll")
  @ResponseBody
  BaasResponse<List<BPositionUserShop>> queryAll(@PathVariable("tenant") String tenant,
      @RequestHeader(value = "orgId") String orgId,
      @RequestBody QueryRequest request);

  @PostMapping("/v1/{tenant}/position/shop/list")
  @ResponseBody
  BaasResponse<List<BShop>> shopList(@PathVariable("tenant") String tenant,
      @ApiParam(value = "uuid") @RequestParam(value = "uuid") String uuid,
      @ApiParam(value = "查询条件") @RequestBody QueryRequest request);

  /**
   * 设置某个岗位的用户
   */
  @PostMapping("/v1/{tenant}/position/user/assign")
  @ResponseBody
  BaasResponse<Void> userAssign(@PathVariable("tenant") String tenant, @RequestHeader("orgId") String orgId,
      @ApiParam(value = "岗位信息") @RequestBody BAssignPositionReq req) throws BaasException;


  /**
   * 设置某个用户的门店设置某个用户的门店
   */
  @PostMapping("/v1/{tenant}/position/shop/assign")
  @ResponseBody
  BaasResponse<Void> shopAssign(@PathVariable("tenant") String tenant, @RequestHeader("orgId") String orgId,
      @ApiParam(value = "门店信息") @RequestBody BUserShopReq req) throws BaasException;

  /**
   * 用户授权组织列表
   */
  @GetMapping("/v1/{tenant}/rest/service/org/view/list")
  @ResponseBody
  BaasResponse<BUserOrg> orgViewList(@ApiParam(value = "租户id") @PathVariable("tenant") String tenant, @RequestParam("orgId") String orgId,
      @RequestParam("userId") String userId, @RequestParam("appId") String appId);

  /**
   * 根据loginId查询门店信息
   */
  @PostMapping("/v1/{tenant}/wholesale/service/user/shop/list")
  @ResponseBody
  BaasResponse<List<BShop>> listShopV2(@ApiParam(value = "租户id") @PathVariable("tenant") String tenant,
      @ApiParam(value = "用户id", required = true) @RequestParam(value = "uuid") String uuid)
      throws Exception;

  /**
   * 根据区域id查询督导电话
   */
  @PostMapping("/v1/{tenant}/wholesale/service/user/mobile/getByAreaId")
  @ResponseBody
  BaasResponse<String> getMobileByAreaId(@PathVariable("tenant") String tenant,
      @RequestParam(value = "areaId") String areaId)
      throws Exception;

  @RequestMapping(value = "/v1/{tenant}/rest/service/user/queryEs", method = RequestMethod.POST)
  @ResponseBody
  BaasResponse<List<BUser>> queryEs(@ApiParam(value = "租户id") @PathVariable("tenant") String tenant,
      @RequestHeader(value = "orgId") String orgId,
      @RequestBody QueryRequest request) throws BaasException;
}
