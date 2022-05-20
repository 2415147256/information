package com.hd123.baas.sop.remote.sku;

import com.hd123.baas.sop.service.api.basedata.category.Category;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.hd123.baas.sop.remote.rsdemo.RsDemoConfiguration;
import com.qianfan123.baas.common.http2.BaasResponse;

import java.util.List;

/**
 * @author zhengzewang on 2020/10/28.
 */
@FeignClient(name = "category", configuration = RsDemoConfiguration.class)
@RequestMapping(produces = "application/json;charset=utf-8", consumes = "application/json;charset=utf-8")
public interface SkuCategoryClient {

  @RequestMapping(value = "v1/{tenant}/remote/category/list", method = RequestMethod.POST)
  BaasResponse<List<Category>> query(@PathVariable("tenant") String tenant, @RequestParam("categoryIds") List<String> categoryIds);

  @RequestMapping(value = "v1/{tenant}/remote/category/parent/list", method = RequestMethod.POST)
  BaasResponse<List<Category>> queryParentCategory(@PathVariable("tenant") String tenant, @RequestParam("categoryId") String categoryId);


}
