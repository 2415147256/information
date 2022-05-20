package com.hd123.baas.sop.remote.pms;

import com.hd123.baas.sop.remote.uas.BasicAuthConfiguration;
import com.hd123.spms.commons.bean.ListWrapper;
import com.hd123.spms.commons.exception.BizServiceException;
import com.hd123.spms.engine.v2.PromRetailSingleProductPriceV2;
import com.hd123.spms.engine.v2.PromRetailSingleProductV2;
import com.qianfan123.baas.common.http2.BaasResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;


/**
 * @author wanglunyuan
 */
@FeignClient(name = "spms-engine-server", configuration = BasicAuthConfiguration.class)
@RequestMapping(value = "v3/{tenant_id}/promotionengineservice", produces = "application/json;charset=utf-8")
public interface PmsEngineV2Client {

  /**
   * 批量计算单品促销价
   *
   * @param tenantId
   * @param products
   * @return
   * @throws BizServiceException
   */
  @RequestMapping(value = "batchcalcsingleprice", method = RequestMethod.POST)
  @ResponseBody
  BaasResponse<List<PromRetailSingleProductPriceV2>> batchCalcSinglePrice(
          @PathVariable("tenant_id") @RequestHeader("tenant_id") String tenantId,
          @RequestBody ListWrapper<PromRetailSingleProductV2> products) throws BizServiceException;


}
