package com.hd123.baas.sop.remote.rsmas.cat;

import com.hd123.baas.sop.remote.rsmas.RsMasConfiguration;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @Author maodapeng
 * @Since
 */

@FeignClient(name = "mas-openapi-service", url = "${mas-openapi-service.url:}",
    configuration = RsMasConfiguration.class)
@RequestMapping(produces = "application/json;charset=utf-8", consumes = "application/json;charset=utf-8")
public interface RsMasCatClient {
  @ApiOperation("查询目录列表")
  @RequestMapping(value = {
      "/v2/{tenant}/service/customer/cat/query" }, method = {
          RequestMethod.POST })
  MasPageResponse<List<Cat>> query(@ApiParam(required = true) @PathVariable("tenant") String var1,
      @ApiParam(required = true, value = " 查询条件") @RequestBody CatFilter var2);

  @ApiOperation("查询目录商品关系列表")
  @RequestMapping(value = {
      "/v2/{tenant}/service/customer/cat/catsku/query" }, method = {
          RequestMethod.POST })
  MasPageResponse<List<CatSKURelation>> query(@ApiParam(required = true) @PathVariable("tenant") String var1,
      @ApiParam(required = true, value = " 查询条件") @RequestBody CatSKURelationFilter var2);

  @ApiOperation("查询指定目录的客户列表")
  @RequestMapping(value = {
      "/v2/{tenant}/service/customer/cat/{catId}/customer/query" }, method = {
          RequestMethod.POST })
  MasPageResponse<List<CatCustomer>> query(@ApiParam(required = true) @PathVariable("tenant") String var1,
      @ApiParam(required = true) @PathVariable("catId") String var2,
      @ApiParam(required = true, value = " 查询条件") @RequestBody CatCustomerFilter var3);

  @ApiOperation("查询指定目录的门店列表")
  @RequestMapping(value = {
      "/v2/{tenant}/service/customer/cat/{catId}/shop/query" }, method = {
          RequestMethod.POST })
  MasPageResponse<List<CatShop>> query(@ApiParam(required = true) @PathVariable("tenant") String var1,
      @ApiParam(required = true) @PathVariable("catId") String var2,
      @ApiParam(required = true, value = " 查询条件") @RequestBody CatShopFilter var3);

  @ApiOperation("新增目录")
  @RequestMapping(value = {
      "/v2/{tenant}/service/customer/cat/{orgType}/{orgId}/create" }, method = {
          RequestMethod.POST })
  MasResponse<String> create(@ApiParam(required = true) @PathVariable("tenant") String var1,
      @ApiParam(required = true) @PathVariable("orgType") String orgType,
      @ApiParam(required = true) @PathVariable("orgId") String orgId,
      @ApiParam(required = true, value = "新增目录信息") @RequestBody CatCreation var2,
      @ApiParam(required = true, value = "操作人") @RequestParam("operator") String var3) throws Exception;
  @ApiOperation("删除指定目录")
  @RequestMapping(value = {
      "/v2/{tenant}/service/customer/cat/{orgType}/{orgId}/{id}/delete" }, method = {
          RequestMethod.POST })
  MasResponse<Void> delete(@ApiParam(required = true) @PathVariable("tenant") String var1,
    @ApiParam(required = true) @PathVariable("orgType") String orgType,
    @ApiParam(required = true) @PathVariable("orgId") String orgId,
      @ApiParam(required = true) @PathVariable("id") @RequestBody String var2,
      @ApiParam(required = true, value = "操作人") @RequestParam("operator") String var3);

  @ApiOperation("往目录中添加门店")
  @RequestMapping(value = {
      "/v2/{tenant}/service/customer/cat/{orgType}/{orgId}/{id}/shops/save" }, method = {
          RequestMethod.POST })
  MasResponse<Void> saveShops(@ApiParam(required = true) @PathVariable("tenant") String var1,
      @ApiParam(required = true) @PathVariable("orgType") String orgType,
      @ApiParam(required = true) @PathVariable("orgId") String orgId,
      @ApiParam(required = true) @PathVariable("id")  String var2,
      @ApiParam(required = true, value = "门店ID列表") @RequestBody MasRequest<List<CatShop>> var3,
      @ApiParam(required = true, value = "操作人") @RequestParam("operator") String var4);

  @ApiOperation("从目录中删除门店")
  @RequestMapping(value = {
      "/v2/{tenant}/service/customer/cat/{orgType}/{orgId}/{id}/shops/remove" }, method = {
          RequestMethod.POST })
  MasResponse<Void> removeShops(@ApiParam(required = true) @PathVariable("tenant") String var1,
      @ApiParam(required = true) @PathVariable("orgType") String orgType,
      @ApiParam(required = true) @PathVariable("orgId") String orgId,
      @ApiParam(required = true) @PathVariable("id") @RequestBody String var2,
      @ApiParam(required = true, value = "门店ID列表") @RequestBody MasRequest<List<String>> var3,
      @ApiParam(required = true, value = "操作人") @RequestParam("operator") String var4);

  @ApiOperation("往目录中添加SKU")
  @RequestMapping(value = {
      "/v2/{tenant}/service/customer/cat/{orgType}/{orgId}/{id}/skus/save" }, method = {
          RequestMethod.POST })
  MasResponse<Void> saveSkus(@ApiParam(required = true) @PathVariable("tenant") String var1,
      @ApiParam(required = true) @PathVariable("orgType") String orgType,
      @ApiParam(required = true) @PathVariable("orgId") String orgId,
      @ApiParam(required = true) @PathVariable("id")  String var2,
      @ApiParam(required = true, value = "SKU列表") @RequestBody MasRequest<List<CatSKUCreation>> var3,
      @ApiParam(required = true, value = "操作人") @RequestParam("operator") String var4) throws Exception;

  @ApiOperation("从目录中删除SKU")
  @RequestMapping(value = {
    "/v2/{tenant}/service/customer/cat/{orgType}/{orgId}/{id}/skus/remove" }, method = {
    RequestMethod.POST })
  MasResponse<Void> removeSkus(@ApiParam(required = true) @PathVariable("tenant") String var1,
    @ApiParam(required = true) @PathVariable("orgType") String orgType,
    @ApiParam(required = true) @PathVariable("orgId") String orgId,
    @ApiParam(required = true) @PathVariable("id")  String var2,
    @ApiParam(required = true, value = "SKUID列表") @RequestBody MasRequest<List<String>> var3,
    @ApiParam(required = true, value = "操作人") @RequestParam("operator") String var4);

  @ApiOperation("修改指定目录的指定商品")
  @RequestMapping(value = {
    "/v2/{tenant}/service/customer/cat/{orgType}/{orgId}/{catId}/sku/{skuId}/mod" }, method = {
    RequestMethod.POST })
  MasResponse modSKU(@ApiParam(required = true) @PathVariable("tenant") String var1,
    @ApiParam(required = true) @PathVariable("orgType") String orgType,
    @ApiParam(required = true) @PathVariable("orgId") String orgId,
    @ApiParam(required = true) @PathVariable("catId") String var2,
    @ApiParam(required = true) @PathVariable("skuId") String var3,
    @ApiParam(required = true, value = " 修改信息") @RequestBody CatSKUMod var4);
}