package com.hd123.baas.sop.remote.rsmas2;

import com.hd123.baas.sop.remote.rsmas.cat.MasRequest;
import com.hd123.baas.sop.remote.rsmas2.aspect.RsAspectDto;
import com.hd123.baas.sop.remote.rsmas2.aspect.RsAspectUpsertDto;
import com.hd123.baas.sop.remote.rsmas2.aspect.RsFieldOptionsDto;
import com.hd123.baas.sop.remote.rsmas2.catalog.RsCatalogApply;
import com.hd123.baas.sop.remote.rsmas2.catalog.RsCatalogCreateDto;
import com.hd123.baas.sop.remote.rsmas2.category.RsCategoryCreateDto;
import com.hd123.baas.sop.remote.rsmas2.category.RsCategoryDto;
import com.hd123.baas.sop.remote.rsmas2.category.RsCategoryRemoveDto;
import com.hd123.baas.sop.remote.rsmas2.category.RsCategoryUpdateDto;
import com.hd123.baas.sop.remote.rsmas2.shop.RsPShopDto;
import com.hd123.baas.sop.remote.rsmas2.shopsku.RsRecreateBySkuDto;
import com.hd123.baas.sop.remote.rsmas2.shopsku.RsShopSkuDto;
import com.hd123.baas.sop.remote.rsmas2.shopsku.RsShopSkuOpDto;
import com.hd123.baas.sop.remote.rsmas2.shopsku.RsShopSkuRespItemDto;
import com.hd123.baas.sop.remote.rsmas2.sku.RsSkuCreateDto;
import com.hd123.baas.sop.remote.rsmas2.sku.RsSkuDto;
import com.hd123.baas.sop.remote.rsmas2.sku.RsSkuRespItemDto;
import com.hd123.baas.sop.remote.rsmas2.sku.RsSkuUpdateDto;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * mas2接口服务
 *
 * @author zhangweigang
 */
@FeignClient(name = "mas2", url = "${mas2-service.url:}", configuration = Mas2Configuration.class)
@RequestMapping(produces = "application/json;charset=utf-8", consumes = "application/json;charset=utf-8")
public interface RsMas2Client {

  @ApiOperation(value = "侧写新增修改")
  @PostMapping("/aspect/upsert")
  Mas2Response<Void> upsertAspect(@RequestBody RsAspectUpsertDto body);

  @ApiOperation("新增侧写可选值")
  @PostMapping("/{tenant}/aspect/options/upsert")
  Mas2Response<Void> upsertFieldOptions(@PathVariable("tenant") String tenant, @RequestBody RsFieldOptionsDto request);

  @ApiOperation("获取侧写可选值")
  @GetMapping("/{tenant}/aspect/options/retrieve")
  Mas2Response<Object> retrieveFieldOptions(@PathVariable("tenant") String tenant,
      @RequestParam("aspectName") String aspectName, @RequestParam("fieldName") String fieldName);

  @ApiOperation(value = "类目新增")
  @PostMapping("/{tenant}/catalog/create")
  Mas2Response<Void> createCataLog(@PathVariable("tenant") String tenant, @RequestBody RsCatalogCreateDto body);

  @PostMapping("/{tenant}/catalog/applyTo/add")
  Mas2Response<Void> applyCatalogToAdd(@PathVariable("tenant") String tenant, @RequestBody RsCatalogApply body);

  @ApiOperation(value = "分类新增")
  @PostMapping("/{tenant}/category/create")
  Mas2Response<Void> createCategory(@PathVariable("tenant") String tenant, @RequestBody RsCategoryCreateDto body);

  @ApiOperation(value = "分类查询")
  @PostMapping("/{tenant}/category/find")
  Mas2Response<List<RsCategoryDto>> findCategory(@PathVariable("tenant") String tenant,
      @RequestBody RsFindRequest request);

  @ApiOperation(value = "分类更新")
  @PostMapping("/{tenant}/category/update")
  Mas2Response<Void> updateCategory(@PathVariable("tenant") String tenant, @RequestBody RsCategoryUpdateDto body);

  @ApiOperation(value = "分类移除")
  @PostMapping("/{tenant}/category/remove")
  Mas2Response<Void> removeCategory(@PathVariable("tenant") String tenant, @RequestBody RsCategoryRemoveDto body);

  @ApiOperation(value = "门店查询")
  @PostMapping("/{tenant}/shop/pshop/find")
  Mas2Response<List<RsPShopDto>> findShop(@PathVariable("tenant") String tenant, @RequestBody RsFindRequest request);

  @ApiOperation(value = "查询门店商品")
  @PostMapping("/{tenant}/shopsku/find")
  Mas2Response<List<RsShopSkuDto>> findShopSku(@PathVariable("tenant") String tenant,
      @RequestBody RsFindRequest request);

  @ApiOperation(value = "门店商品修复")
  @PostMapping("/{tenant}/shopsku/recreateBySku")
  Mas2Response<List<RsShopSkuRespItemDto>> recreateBySku(@PathVariable("tenant") String tenant,
      @RequestBody RsRecreateBySkuDto body);

  @ApiOperation("商品查询")
  @PostMapping("/{tenant}/sku/find")
  Mas2Response<List<RsSkuDto>> findSku(@PathVariable("tenant") String tenant, @RequestBody RsFindRequest request);

  @ApiOperation("商品更新")
  @PostMapping("/{tenant}/sku/update")
  Mas2Response<Void> updateSku(@PathVariable("tenant") String tenant, @RequestBody RsSkuUpdateDto body);
}
