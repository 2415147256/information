package com.hd123.baas.sop.remote.rsIwms;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author W.J.H.7
 */
@FeignClient(name = "rsiwms", configuration = RsIwmsConfiguration.class)
@RequestMapping(produces = "application/json;charset=utf-8", consumes = "application/json;charset=utf-8")
public interface RsIwmsClient {

  /**
   * ⻔⻔店容器出库查询接⼝⼝
   */
  @RequestMapping(value = "/v1/containerasset/queryStoreOutContainer", method = RequestMethod.POST)
  RsQueryStoreOutContainerRes queryStoreOutContainer(@RequestParam("companyUuid") String companyUuid,
      @RequestBody RsQueryStoreOutContainerReq req);

  /**
   * ⻔店回收容器反馈接⼝
   */
  @RequestMapping(value = "/v1/containerasset/recycleStoreContainer", method = RequestMethod.POST)
  RsrecycleStoreContainerRes recycleStoreContainer(@RequestParam("companyUuid") String companyUuid,
      @RequestBody RsrecycleStoreContainerReq re);

}
