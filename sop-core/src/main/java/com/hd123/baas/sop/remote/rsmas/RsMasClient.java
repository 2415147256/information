/**
 * 版权所有(C)，上海海鼎信息工程股份有限公司，2020，所有权利保留。
 * <p>
 * 项目名：	sos_idea 文件名：	RsStoreInvXFReqClient.java 模块说明： 修改历史： 2020/11/2 - Leo - 创建。
 */

package com.hd123.baas.sop.remote.rsmas;

import com.hd123.baas.sop.remote.rsmas.cat.MasRequest;
import com.hd123.baas.sop.remote.rsmas.groupTag.RsSKUGroupTagItem;
import com.hd123.baas.sop.service.api.basedata.task.TaskExecuteRecord;
import com.hd123.baas.sop.qcy.controller.shopsku.RsShopSkuIdKey;
import com.hd123.baas.sop.remote.rsmas.area.RsArea;
import com.hd123.baas.sop.remote.rsmas.area.RsAreaFilter;
import com.hd123.baas.sop.remote.rsmas.brand.RsBrand;
import com.hd123.baas.sop.remote.rsmas.brand.RsBrandFilter;
import com.hd123.baas.sop.remote.rsmas.category.RsCategory;
import com.hd123.baas.sop.remote.rsmas.category.RsCategoryFilter;
import com.hd123.baas.sop.remote.rsmas.collocation.RsCollocationGroup;
import com.hd123.baas.sop.remote.rsmas.collocation.RsCollocationGroupCreation;
import com.hd123.baas.sop.remote.rsmas.collocation.RsCollocationGroupFilter;
import com.hd123.baas.sop.remote.rsmas.collocation.RsCollocationGroupUpdate;
import com.hd123.baas.sop.remote.rsmas.collocation.RsSkuCollocationGroupLine;
import com.hd123.baas.sop.remote.rsmas.department.RsDepartment;
import com.hd123.baas.sop.remote.rsmas.department.RsDepartmentFilter;
import com.hd123.baas.sop.remote.rsmas.employee.RsEmployee;
import com.hd123.baas.sop.remote.rsmas.employee.RsEmployeeFilter;
import com.hd123.baas.sop.remote.rsmas.follow.RsFollow;
import com.hd123.baas.sop.remote.rsmas.follow.RsFollowFilter;
import com.hd123.baas.sop.remote.rsmas.goods.RsGoods;
import com.hd123.baas.sop.remote.rsmas.goods.RsGoodsFilter;
import com.hd123.baas.sop.remote.rsmas.index.RsDocument;
import com.hd123.baas.sop.remote.rsmas.index.RsSearchRequest;
import com.hd123.baas.sop.remote.rsmas.index.RsSearchResponse;
import com.hd123.baas.sop.remote.rsmas.options.RsOptions;
import com.hd123.baas.sop.remote.rsmas.options.RsOptionsBatchInit;
import com.hd123.baas.sop.remote.rsmas.options.RsOptionsCreation;
import com.hd123.baas.sop.remote.rsmas.options.RsOptionsFilter;
import com.hd123.baas.sop.remote.rsmas.platformcategory.RsCategorySkuKey;
import com.hd123.baas.sop.remote.rsmas.platformcategory.RsPlatformCategory;
import com.hd123.baas.sop.remote.rsmas.platformcategory.RsPlatformCategoryCreation;
import com.hd123.baas.sop.remote.rsmas.platformcategory.RsPlatformCategoryFilter;
import com.hd123.baas.sop.remote.rsmas.platformcategory.RsPlatformCategorySku;
import com.hd123.baas.sop.remote.rsmas.platformcategory.RsPlatformCategorySkuFilter;
import com.hd123.baas.sop.remote.rsmas.platformcategory.RsPlatformCategoryUpdate;
import com.hd123.baas.sop.remote.rsmas.platformcategory.RsPlatformCategoryUpdates;
import com.hd123.baas.sop.remote.rsmas.platformshop.RsPlatformShop;
import com.hd123.baas.sop.remote.rsmas.platformshop.RsPlatformShopBatchUpdateBusinessHour;
import com.hd123.baas.sop.remote.rsmas.platformshop.RsPlatformShopBatchUpdateBusinessState;
import com.hd123.baas.sop.remote.rsmas.platformshop.RsPlatformShopFilter;
import com.hd123.baas.sop.remote.rsmas.platformshop.RsYcPlatformShopUpdate;
import com.hd123.baas.sop.remote.rsmas.platshopcategory.RsBatchRemovePlatShopCategorySkuKey;
import com.hd123.baas.sop.remote.rsmas.platshopcategory.RsPlatShopCategory;
import com.hd123.baas.sop.remote.rsmas.platshopcategory.RsPlatShopCategoryBatchUpdate;
import com.hd123.baas.sop.remote.rsmas.platshopcategory.RsPlatShopCategoryCopyRequest;
import com.hd123.baas.sop.remote.rsmas.platshopcategory.RsPlatShopCategoryCreation;
import com.hd123.baas.sop.remote.rsmas.platshopcategory.RsPlatShopCategoryFilter;
import com.hd123.baas.sop.remote.rsmas.platshopcategory.RsPlatShopCategorySku;
import com.hd123.baas.sop.remote.rsmas.platshopcategory.RsPlatShopCategorySkuFilter;
import com.hd123.baas.sop.remote.rsmas.platshopcategory.RsPlatShopCategorySkuKey;
import com.hd123.baas.sop.remote.rsmas.platshopcategory.RsPlatShopCategorySkuUpdate;
import com.hd123.baas.sop.remote.rsmas.platshopcategory.RsPlatShopCategoryUpdate;
import com.hd123.baas.sop.remote.rsmas.platshopcategory.RsSkuPlatShopCategoryKey;
import com.hd123.baas.sop.remote.rsmas.pos.RsBatchSetDefaultRequest;
import com.hd123.baas.sop.remote.rsmas.pos.RsBatchSetSerialNumRequest;
import com.hd123.baas.sop.remote.rsmas.pos.RsPos;
import com.hd123.baas.sop.remote.rsmas.pos.RsPosFilter;
import com.hd123.baas.sop.remote.rsmas.shopsku.RsCollocationGroupShopSkuBind;
import com.hd123.baas.sop.remote.rsmas.shopsku.RsCollocationGroupShopSkuUnbind;
import com.hd123.baas.sop.remote.rsmas.shopsku.RsShopSKUBatchCreate;
import com.hd123.baas.sop.remote.rsmas.shopsku.RsShopSku;
import com.hd123.baas.sop.remote.rsmas.shopsku.RsShopSkuFilter;
import com.hd123.baas.sop.remote.rsmas.shopsku.RsShopSkuSellingTimeBatchUpdate;
import com.hd123.baas.sop.remote.rsmas.shopsku.RsShopSkuTagBatchUpdate;
import com.hd123.baas.sop.remote.rsmas.shopsku.RsShopSkuTasteGroupLine;
import com.hd123.baas.sop.remote.rsmas.shopsku.RsSkuShopKey;
import com.hd123.baas.sop.remote.rsmas.shopskuinvcrule.RsBatchSaveShopSkuInvRule;
import com.hd123.baas.sop.remote.rsmas.shopskuinvcrule.RsShopSkuInvRule;
import com.hd123.baas.sop.remote.rsmas.shopskuinvcrule.RsShopSkuInvRuleFilter;
import com.hd123.baas.sop.remote.rsmas.sku.RsSku;
import com.hd123.baas.sop.remote.rsmas.sku.RsSkuFilter;
import com.hd123.baas.sop.remote.rsmas.sku.RsSkuUpdate;
import com.hd123.baas.sop.remote.rsmas.spu.RsSpu;
import com.hd123.baas.sop.remote.rsmas.spu.RsSpuBatchInit;
import com.hd123.baas.sop.remote.rsmas.spu.RsSpuFilter;
import com.hd123.baas.sop.remote.rsmas.stall.RsPosToStallBind;
import com.hd123.baas.sop.remote.rsmas.stall.RsSkuToStallBind;
import com.hd123.baas.sop.remote.rsmas.stall.RsStall;
import com.hd123.baas.sop.remote.rsmas.stall.RsStallCreation;
import com.hd123.baas.sop.remote.rsmas.stall.RsStallFilter;
import com.hd123.baas.sop.remote.rsmas.stall.RsStallModification;
import com.hd123.baas.sop.remote.rsmas.stall.RsStallPos;
import com.hd123.baas.sop.remote.rsmas.stall.RsStallPosFilter;
import com.hd123.baas.sop.remote.rsmas.stall.RsStallShopSkuDelete;
import com.hd123.baas.sop.remote.rsmas.store.RsShopBatchUpdateBusinessHours;
import com.hd123.baas.sop.remote.rsmas.store.RsShopBatchUpdateBusinessState;
import com.hd123.baas.sop.remote.rsmas.store.RsStore;
import com.hd123.baas.sop.remote.rsmas.store.RsStoreFilter;
import com.hd123.baas.sop.remote.rsmas.task.RsTask;
import com.hd123.baas.sop.remote.rsmas.task.RsTaskExecuteRecordFilter;
import com.hd123.baas.sop.remote.rsmas.task.RsTaskExecuteReport;
import com.hd123.baas.sop.remote.rsmas.task.RsTaskFilter;
import com.hd123.baas.sop.remote.rsmas.task.RsTaskSubmit;
import com.hd123.baas.sop.remote.rsmas.tastegroup.RsSKUTasteGroupLine;
import com.hd123.baas.sop.remote.rsmas.tastegroup.RsTasteGroup;
import com.hd123.baas.sop.remote.rsmas.tastegroup.RsTasteGroupCreation;
import com.hd123.baas.sop.remote.rsmas.tastegroup.RsTasteGroupFilter;
import com.hd123.baas.sop.remote.rsmas.tastegroup.RsTasteGroupSKUUnbind;
import com.hd123.baas.sop.remote.rsmas.tastegroup.RsTasteGroupUpdate;
import com.hd123.baas.sop.remote.rsmas.tenant.RsTenant;
import com.hd123.baas.sop.remote.rsmas.tenant.RsTenantFilter;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

/**
 * H6-SOP接口服务
 *
 * @author Leo
 */
@FeignClient(name = "mas-openapi-service", url = "${mas-openapi-service.url:}", configuration = RsMasConfiguration.class)
@RequestMapping(produces = "application/json;charset=utf-8", consumes = "application/json;charset=utf-8")
public interface RsMasClient {

  // 对应资料中台OpenAPI的接口

  /**
   * 获取指定用户被授权访问的所有资源门店信息
   *
   * @param tenant
   *     租户
   * @param userId
   *     用户ID
   */
  @ApiOperation(value = "获取指定用户被授权访问的所有资源门店信息")
  @RequestMapping(value = "/v2/{tenant}/service/access/{orgType}/{orgId}/getAssignedResourcesByUserId/{userId}", method = RequestMethod.POST)
  RsMasResponse<List<RsStore>> accessQuery(
      @ApiParam(value = "租户标识", required = true) @PathVariable("tenant") String tenant,
      @ApiParam(required = true) @PathVariable("orgType") String orgType,
      @ApiParam(required = true) @PathVariable("orgId") String orgId,
      @ApiParam(required = true) @PathVariable("userId") String userId);

  /**
   * 查询品牌"
   *
   * @param tenant
   *     租户id
   * @param queryFilter
   *     查询条件
   * @return 品牌列表
   */
  @ApiOperation(value = "查询品牌")
  @PostMapping(value = "/v2/{tenant}/service/brand/query")
  RsMasPageResponse<List<RsBrand>> brandQuery(
      @ApiParam(value = "租户标识", required = true) @PathVariable("tenant") String tenant,
      @ApiParam(value = "查询定义", required = true) @RequestBody RsBrandFilter queryFilter);

  /**
   * 查询分类
   *
   * @param tenant
   *     租户id
   * @param queryFilter
   *     查询条件
   * @return 分类列表
   */
  @ApiOperation(value = "查询分类")
  @RequestMapping(value = "/v2/{tenant}/service/category/query", method = RequestMethod.POST)
  RsMasPageResponse<List<RsCategory>> categoryQuery(
      @ApiParam(value = "租户标识", required = true) @PathVariable("tenant") String tenant,
      @ApiParam(value = "查询定义", required = true) @RequestBody RsCategoryFilter queryFilter);

  /**
   * 查询部门分页列表
   *
   * @param tenant
   *     商户id
   * @param queryFilter
   *     查询条件
   * @return 部门分页列表
   */
  @ApiOperation(value = "查询部门分页列表")
  @RequestMapping(value = "/v2/{tenant}/service/department/query", method = RequestMethod.POST)
  RsMasPageResponse<List<RsDepartment>> departmentQuery(
      @ApiParam(value = "租户标识", required = true) @PathVariable("tenant") String tenant,
      @ApiParam(value = "查询定义", required = true) @RequestBody RsDepartmentFilter queryFilter);

  /**
   * 查询员工
   */
  @ApiOperation(value = "查询员工")
  @RequestMapping(value = "/v2/{tenant}/service/employee/query", method = RequestMethod.POST)
  @ResponseBody
  RsMasPageResponse<List<RsEmployee>> employeeQuery(
      @ApiParam(required = true) @PathVariable("tenant") String tenant,
      @ApiParam(required = true, value = "查询条件") @RequestBody RsEmployeeFilter filter);

  /**
   * 查询商品主档列表
   *
   * @param tenant
   *     租户id
   * @param filter
   *     查询条件
   * @return 商品主档列表
   */
  @ApiOperation(value = "查询商品主档列表")
  @RequestMapping(value = "/v2/{tenant}/service/goods/query", method = RequestMethod.POST)
  RsMasPageResponse<List<RsGoods>> goodsQuery(
      @ApiParam(required = true) @PathVariable("tenant") String tenant,
      @ApiParam(required = true, value = "查询条件") @RequestBody RsGoodsFilter filter);

  /**
   * 查询商品SKU列表
   *
   * @param tenant
   *     租户id
   * @param filter
   *     查询条件
   */
  @ApiOperation(value = "查询商品SKU列表")
  @RequestMapping(value = "/v2/{tenant}/service/sku/query", method = RequestMethod.POST)
  RsMasPageResponse<List<RsSku>> skuQuery(
      @ApiParam(required = true) @PathVariable("tenant") String tenant,
      @ApiParam(required = true, value = "查询条件") @RequestBody RsSkuFilter filter);

  @ApiOperation(value = "查询SPU列表")
  @RequestMapping(value = "/v2/{tenant}/service/spu/query", method = RequestMethod.POST)
  RsMasPageResponse<List<RsSpu>> spuQuery(
      @ApiParam(required = true) @PathVariable("tenant") String tenant,
      @ApiParam(required = true, value = "查询条件") @RequestBody RsSpuFilter filter);

  /**
   * SPU初始化
   *
   * @param tenantId
   *     租户id
   * @param batchInit
   *     初始化SPU请求
   * @param operator
   *     操作人
   * @return 初始化结果
   */
  @ApiOperation(value = "SPU初始化")
  @RequestMapping(value = "/v2/{tenant}/service/spu/{orgType}/{orgId}/batchInit", method = RequestMethod.POST)
  RsMasResponse<List<String>> spuBatchInit(//
      @ApiParam(required = true) @PathVariable("tenant") String tenantId,
      @ApiParam(required = true) @PathVariable("orgType") String orgType,
      @ApiParam(required = true) @PathVariable("orgId") String orgId,
      @ApiParam(required = true, value = "初始化SPU请求") @RequestBody RsSpuBatchInit batchInit,
      @ApiParam(required = true, value = "操作人") @RequestParam("operator") String operator);

  /**
   * 以sku维度删除spu和sku的关联关系
   *
   * @param tenantId
   *     租户id
   * @param skuIdsRequest
   *     skuId列表
   * @param operator
   *     操作人
   * @return 删除结果
   */
  @ApiOperation(value = "根据sku维度删除spu和sku的关联关系")
  @RequestMapping(value = "/v2/{tenant}/service/spu/{orgType}/{orgId}/batchDeleteSpuSku", method = RequestMethod.POST)
  RsMasResponse spuBatchDeleteSpuSku(
      @ApiParam(required = true) @PathVariable("tenant") String tenantId,
      @ApiParam(required = true) @PathVariable("orgType") String orgType,
      @ApiParam(required = true) @PathVariable("orgId") String orgId,
      @ApiParam(required = true) @RequestBody RsMasRequest<List<String>> skuIdsRequest,
      @ApiParam(required = true, value = "操作人") @RequestParam("operator") String operator);

  /**
   * 更新sku信息
   */
  @ApiOperation(value = "更新sku信息")
  @RequestMapping(value = "/v2/{tenant}/service/sku/{orgType}/{orgId}/update", method = RequestMethod.POST)
  RsMasResponse skuUpdate(@ApiParam(required = true) @PathVariable("tenant") String tenantId,
      @ApiParam(required = true) @PathVariable("orgType") String orgType,
      @ApiParam(required = true) @PathVariable("orgId") String orgId,
      @ApiParam(required = true, value = " sku更新信息") @RequestBody RsMasRequest<List<RsSkuUpdate>> request,
      @ApiParam(required = true, value = "操作人") @RequestParam("operator") String operator);

  @ApiOperation(value = "批量设置是否标签打印")
  @RequestMapping(value = "/v2/{tenant}/service/sku/{orgType}/{orgId}/batchLabelPrinting", method = RequestMethod.POST)
  RsMasResponse skuBatchLabelPrinting(@ApiParam(required = true) @PathVariable("tenant") String tenantId,
      @ApiParam(required = true) @PathVariable("orgType") String orgType,
      @ApiParam(required = true) @PathVariable("orgId") String orgId,
      @ApiParam(required = true, value = "是否标签打印") @RequestParam("labelPrinting") Boolean labelPrinting,
      @ApiParam(required = true, value = " skuID列表") @RequestBody RsMasRequest<List<String>> request,
      @ApiParam(required = true, value = "操作人") @RequestParam("operator") String operator);


  /**
   * 搜索
   */
  @ApiOperation(value = "索引搜索")
  @RequestMapping(value = "/v2/{tenant}/service/index/search", method = RequestMethod.POST)
  @ResponseBody
  RsSearchResponse indexSearch(//
      @ApiParam(required = true) @PathVariable("tenant") String tenant, //
      @ApiParam(required = true, value = "搜索条件") @RequestBody RsSearchRequest request);

  /**
   * 更新文档
   */
  @ApiOperation(value = "更新文档")
  @RequestMapping(value = "/v2/{tenant}/service/index/doc/update", method = RequestMethod.POST)
  @ResponseBody
  RsMasResponse updateDoc(//
      @ApiParam(required = true) @PathVariable("tenant") String tenant, //
      @ApiParam(required = true, value = "文档") @RequestBody RsMasRequest<List<RsDocument>> request);

  /**
   * 获取指定门店商品
   */
  @ApiOperation(value = "获取指定门店商品")
  @RequestMapping(value = "/v2/{tenant}/service/shopSku/{orgType}/{orgId}/{id}", method = RequestMethod.GET)
  RsMasResponse<RsShopSku> getShopSku(//
      @ApiParam(required = true) @PathVariable("tenant") String tenantId,
      @ApiParam(required = true) @PathVariable("orgType") String orgType,
      @ApiParam(required = true) @PathVariable("orgId") String orgId,
      @ApiParam(required = true) @PathVariable("id") String id);

  /**
   * 查询门店商品列表
   *
   * @param tenant
   *     租户id
   * @param filter
   *     查询条件
   */
  @ApiOperation(value = "查询门店商品列表")
  @RequestMapping(value = "/v2/{tenant}/service/shopSku/query", method = RequestMethod.POST)
  @ResponseBody
  RsMasPageResponse<List<RsShopSku>> shopSkuQuery(
      @ApiParam(required = true) @PathVariable("tenant") String tenant,
      @ApiParam(required = true, value = "查询条件") @RequestBody RsShopSkuFilter filter);

  /**
   * 批量新建
   */
  @ApiOperation(value = "批量新建门店商品")
  @RequestMapping(value = "/v2/{tenant}/service/shopSku/{orgType}/{orgId}/batchcreate/yc", method = RequestMethod.GET)
  RsMasResponse<Void> batchCreateAsYc(
      @ApiParam(required = true) @PathVariable("tenant") String tenantId,
      @ApiParam(required = true) @PathVariable("orgType") String orgType,
      @ApiParam(required = true) @PathVariable("orgId") String orgId,
      @ApiParam(required = true, value = " sku更新信息") @RequestBody RsShopSKUBatchCreate request,
      @ApiParam(required = true, value = "操作人") @RequestParam(value = "operator") String operator);

  /**
   * 批量上架
   */
  @ApiOperation(value = "批量上架")
  @RequestMapping(value = "/v2/{tenant}/service/shopSku/{orgType}/{orgId}/batchEnable", method = RequestMethod.POST)
  RsMasResponse<Void> shopSkuBatchEnable(
      @ApiParam(required = true) @PathVariable("tenant") String tenant,
      @ApiParam(required = true) @PathVariable("orgType") String orgType,
      @ApiParam(required = true) @PathVariable("orgId") String orgId,
      @ApiParam(required = true) @RequestBody RsMasRequest<List<RsSkuShopKey>> keyRequest,
      @ApiParam(required = true, value = "操作人") @RequestParam("operator") String operator);

  /**
   * 批量下架
   */
  @ApiOperation(value = "批量下架")
  @RequestMapping(value = "/v2/{tenant}/service/shopSku/{orgType}/{orgId}/batchDisable", method = RequestMethod.POST)
  RsMasResponse<Void> shopSkuBatchDisable(
      @ApiParam(required = true) @PathVariable("tenant") String tenant,
      @ApiParam(required = true) @PathVariable("orgType") String orgType,
      @ApiParam(required = true) @PathVariable("orgId") String orgId,
      @ApiParam(required = true) @RequestBody RsMasRequest<List<RsSkuShopKey>> keyRequest,
      @ApiParam(required = true, value = "操作人") @RequestParam("operator") String operator);

  /**
   * 批量删除
   */
  @ApiOperation(value = "批量删除")
  @RequestMapping(value = "/v2/{tenant}/service/shopSku/{orgType}/{orgId}/batchRemove", method = RequestMethod.POST)
  RsMasResponse<Void> shopSkuBatchRemove(
      @ApiParam(required = true) @PathVariable("tenant") String tenant,
      @ApiParam(required = true) @PathVariable("orgType") String orgType,
      @ApiParam(required = true) @PathVariable("orgId") String orgId,
      @ApiParam(required = true) @RequestBody RsMasRequest<List<RsSkuShopKey>> keyRequest,
      @ApiParam(required = true, value = "操作人") @RequestParam("operator") String operator);

  /**
   * 批量设置售罄标识
   */
  @ApiOperation(value = "批量设置售罄标识")
  @RequestMapping(value = "/v2/{tenant}/service/shopSku/{orgType}/{orgId}/batchSetSaleOut", method = RequestMethod.POST)
  RsMasResponse shopSkuBatchSetSaleOut(@ApiParam(required = true) @PathVariable("tenant") String tenantId,
      @ApiParam(required = true) @PathVariable("orgType") String orgType,
      @ApiParam(required = true) @PathVariable("orgId") String orgId,
      @ApiParam(required = true, value = "是否售罄") @RequestParam("saleOut") Boolean saleOut,
      @ApiParam(required = true) @RequestBody RsMasRequest<List<RsShopSkuIdKey>> keyRequest,
      @ApiParam(required = true, value = "操作人") @RequestParam("operator") String operator);


  /**
   * 批量删除加料组关联关系
   *
   * @param tenantId
   *     租户id
   * @param unbindRequest
   *     解除搭配和商品的关系请求
   * @param operator
   *     操作人
   * @return 删除结果
   */
  @ApiOperation(value = "批量删除门店商品加料组关联关系")
  @RequestMapping(value = "/v2/{tenant}/service/shopSku/{orgType}/{orgId}/collocationGroup/unbind", method = RequestMethod.POST)
  RsMasResponse shopSkuUnbindCollocationGroup(
      @ApiParam(required = true) @PathVariable("tenant") String tenantId,
      @ApiParam(required = true) @PathVariable("orgType") String orgType,
      @ApiParam(required = true) @PathVariable("orgId") String orgId,
      @ApiParam(required = true, value = "解除搭配和商品的关系请求") @RequestBody RsCollocationGroupShopSkuUnbind unbindRequest,
      @ApiParam(required = true, value = "操作人") @RequestParam("operator") String operator);

  /**
   * 批量保存加料组关联关系
   *
   * @param tenantId
   *     租户id
   * @param bindRequest
   *     sku加料组批量修改请求
   * @param operator
   *     操作人
   */
  @ApiOperation(value = "批量保存门店商品加料组关联关系")
  @RequestMapping(value = "/v2/{tenant}/service/shopSku/{orgType}/{orgId}/collocationGroup/bind", method = RequestMethod.POST)
  RsMasResponse shopSkuBindCollocationGroup(
      @ApiParam(required = true) @PathVariable("tenant") String tenantId,
      @ApiParam(required = true) @PathVariable("orgType") String orgType,
      @ApiParam(required = true) @PathVariable("orgId") String orgId,
      @ApiParam(required = true, value = "建立搭配和商品的关系请求") @RequestBody RsCollocationGroupShopSkuBind bindRequest,
      @ApiParam(required = true, value = "操作人") @RequestParam("operator") String operator);

  /**
   * 批量保存ShopSKU口味组
   *
   * @param tenantId
   *     租户id
   * @param request
   *     批量保存ShopSKU口味组
   */
  @ApiOperation(value = "批量保存ShopSKU口味组")
  @RequestMapping(value = "/v2/{tenant}/service/shopSku/{orgType}/{orgId}/taste-group/batch-save", method = RequestMethod.POST)
  RsMasResponse shopSkuBatchSaveTasteGroup(
      @ApiParam(required = true) @PathVariable("tenant") String tenantId,
      @ApiParam(required = true) @PathVariable("orgType") String orgType,
      @ApiParam(required = true) @PathVariable("orgId") String orgId,
      @ApiParam(required = true) @RequestBody RsMasRequest<List<RsShopSkuTasteGroupLine>> request);


  /**
   * 批量保存ShopSku标签（全覆盖）
   *
   * @param tenantId
   *     租户id
   * @param request
   *     shopSku标签批量保存请求
   */
  @ApiOperation(value = "批量保存ShopSku标签（全覆盖）")
  @RequestMapping(value = "/v2/{tenant}/service/shopSku/{orgType}/{orgId}/tag/batch-save", method = RequestMethod.POST)
  RsMasResponse batchSaveTag(@ApiParam(required = true) @PathVariable("tenant") String tenantId,
      @ApiParam(required = true) @PathVariable("orgType") String orgType,
      @ApiParam(required = true) @PathVariable("orgId") String orgId,
      @ApiParam(required = true) @RequestBody RsMasRequest<RsShopSkuTagBatchUpdate> request);

  /**
   * 批量设置 Sku 分组标签（全覆盖）(多组织)
   *
   * @param tenantId
   *          租户id
   * @param request
   *          Sku分组标签批量保存请求
   * @return
   */
  @ApiOperation(value = "批量保存 Sku 分组标签（全覆盖）(多组织)")
  @RequestMapping(value = "/v2/{tenant}/service/sku/{orgType}/{orgId}/groupTag/batchSave", method = RequestMethod.POST)
  RsMasResponse batchSaveGroupTag(
      @ApiParam(required = true) @PathVariable("tenant") String tenantId,
      @ApiParam(required = true) @PathVariable("orgType") String orgType,
      @ApiParam(required = true) @PathVariable("orgId") String orgId,
      @ApiParam(required = true) @RequestBody MasRequest<RsSKUGroupTagItem> request);


  /**
   * 批量保存ShopSku销售时间（全覆盖）
   *
   * @param tenantId
   *     租户id
   * @param request
   *     shopSku销售时间批量保存请求
   */
  @ApiOperation(value = "批量保存ShopSku销售时间（全覆盖）")
  @RequestMapping(value = "/v2/{tenant}/service/shopSku/{orgType}/{orgId}/sellingtime/batch-save", method = RequestMethod.POST)
  RsMasResponse batchSaveSellingtime(
      @ApiParam(required = true) @PathVariable("tenant") String tenantId,
      @ApiParam(required = true) @PathVariable("orgType") String orgType,
      @ApiParam(required = true) @PathVariable("orgId") String orgId,
      @ApiParam(required = true) @RequestBody RsMasRequest<RsShopSkuSellingTimeBatchUpdate> request);

  /**
   * 查询POS收银机分页列表
   *
   * @param tenantId
   *     租户id
   * @param filter
   *     查询条件
   * @return POS收银机分页列表
   */
  @ApiOperation(value = "查询POS收银机分页列表")
  @RequestMapping(value = "/v2/{tenant}/service/pos/query", method = RequestMethod.POST)
  RsMasPageResponse<List<RsPos>> posQuery(
      @ApiParam(required = true) @PathVariable("tenant") String tenantId,
      @ApiParam(required = true, value = "查询条件") @RequestBody RsPosFilter filter);

  /**
   * 获取指定POS收银机
   *
   * @param tenantId
   *     租户id
   * @param id
   *     POS收银机id
   * @return POS收银机信息
   */
  @ApiOperation(value = "获取指定的收银机")
  @RequestMapping(value = "/v2/{tenant}/service/pos/{orgType}/{orgId}/{id}/get", method = RequestMethod.GET)
  RsMasResponse<RsPos> posGet(@ApiParam(required = true) @PathVariable("tenant") String tenantId,
      @ApiParam(required = true) @PathVariable("orgType") String orgType,
      @ApiParam(required = true) @PathVariable("orgId") String orgId,
      @ApiParam(required = true) @PathVariable("id") String id);

  /**
   * 批量设置默认收银机
   *
   * @param tenantId
   *     租户id
   * @param request
   *     POS收银机批量设置默认请求
   * @param operator
   *     操作人
   * @return 初始化结果
   */
  @ApiOperation(value = "批量设置默认收银机")
  @RequestMapping(value = "/v2/{tenant}/service/pos/{orgType}/{orgId}/batchSetDefault", method = RequestMethod.POST)
  RsMasResponse<List<String>> posBatchSetDefault(
      @ApiParam(required = true) @PathVariable("tenant") String tenantId,
      @ApiParam(required = true) @PathVariable("orgType") String orgType,
      @ApiParam(required = true) @PathVariable("orgId") String orgId,
      @ApiParam(required = true, value = "POS收银机批量设置默认请求") @RequestBody RsBatchSetDefaultRequest request,
      @ApiParam(required = true, value = "操作人") @RequestParam("operator") String operator);

  /**
   * 批量设置收银机序号
   *
   * @param tenantId
   *     租户id
   * @param request
   *     POS收银机批量设置序号请求
   * @param operator
   *     操作人
   * @return 初始化结果
   */
  @ApiOperation(value = "批量设置收银机序号")
  @RequestMapping(value = "/v2/{tenant}/service/pos/{orgType}/{orgId}/batchSetSerialNum", method = RequestMethod.POST)
  RsMasResponse<List<String>> posBatchSetSerialNum(
      @ApiParam(required = true) @PathVariable("tenant") String tenantId,
      @ApiParam(required = true) @PathVariable("orgType") String orgType,
      @ApiParam(required = true) @PathVariable("orgId") String orgId,
      @ApiParam(required = true, value = "POS收银机批量设置默认请求") @RequestBody RsBatchSetSerialNumRequest request,
      @ApiParam(required = true, value = "操作人") @RequestParam("operator") String operator);

  /**
   * 查询POS收银机分页列表
   *
   * @param tenant
   *     租户id
   * @param filter
   *     查询条件
   */
  @ApiOperation(value = "查询出品部门与收银机关联关系分页列表")
  @RequestMapping(value = "/v2/{tenant}/service/stall/stallPos/query", method = RequestMethod.POST)
  RsMasPageResponse<List<RsStallPos>> stallPosQuery(
      @ApiParam(required = true) @PathVariable("tenant") String tenant,
      @ApiParam(required = true, value = "查询条件") @RequestBody RsStallPosFilter filter);

  /**
   * 添加绑定SKU到指定出品部门
   *
   * @param tenantId
   *     租户id
   * @param skuToStallBind
   *     出品部门与门店商品的绑定关系
   */
  @ApiOperation(value = "添加绑定SKU到指定出品部门")
  @RequestMapping(value = "/v2/{tenant}/service/stall/{orgType}/{orgId}/addBindSkuToStall", method = RequestMethod.POST)
  RsMasPageResponse addBindSkuToStall(
      @ApiParam(required = true) @PathVariable("tenant") String tenantId,
      @ApiParam(required = true) @PathVariable("orgType") String orgType,
      @ApiParam(required = true) @PathVariable("orgId") String orgId,
      @ApiParam(required = true, value = "出品部门创建信息") @RequestBody RsSkuToStallBind skuToStallBind);

  /**
   * 解除出品部门与门店商品的绑定关系
   *
   * @param tenantId
   *     租户id
   * @param stallShopSkuDelete
   *     移除出品部门与门店商品关系请求
   */
  @ApiOperation(value = "解除出品部门与门店商品的绑定关系")
  @RequestMapping(value = "/v2/{tenant}/service/stall/{orgType}/{orgId}/shopSkuDeleted", method = RequestMethod.POST)
  RsMasPageResponse shopSkuDeleted(
      @ApiParam(required = true) @PathVariable("tenant") String tenantId,
      @ApiParam(required = true) @PathVariable("orgType") String orgType,
      @ApiParam(required = true) @PathVariable("orgId") String orgId,
      @ApiParam(required = true, value = "移除出品部门与门店商品关系请求") @RequestBody RsStallShopSkuDelete stallShopSkuDelete);

  /**
   * 查询门店分页列表
   *
   * @param tenantId
   *     商户id
   * @param filter
   *     查询条件
   * @return 门店分页列表
   */
  @ApiOperation(value = "查询门店分页列表")
  @RequestMapping(value = "/v2/{tenant}/service/shop/query", method = RequestMethod.POST)
  RsMasPageResponse<List<RsStore>> storeQuery(
      @ApiParam(required = true) @PathVariable("tenant") String tenantId,
      @ApiParam(required = true, value = "查询条件") @RequestBody RsStoreFilter filter);

  /**
   * 更新门店信息
   *
   * @param tenantId
   *     商户id
   * @param id
   *     门店id
   * @param shop
   *     门店信息
   */
  @ApiOperation(value = "更新门店信息")
  @RequestMapping(value = "/v2/{tenant}/service/shop/{orgType}/{orgId}/{id}/update", method = RequestMethod.POST)
  RsMasResponse storeUpdate(
      @ApiParam(required = true) @PathVariable("tenant") String tenantId,
      @ApiParam(required = true) @PathVariable("orgType") String orgType,
      @ApiParam(required = true) @PathVariable("orgId") String orgId,
      @ApiParam(required = true) @PathVariable("id") String id,
      @ApiParam(required = true, value = "门店") @RequestBody RsStore shop,
      @ApiParam(required = true, value = "操作人") @RequestParam("operator") String operator);

  /**
   * 获取指定ID的门店资料
   *
   * @param tenantId
   *     租户id
   * @param id
   *     门店id
   * @return 门店信息
   */
  @ApiOperation(value = "获取指定ID的门店资料")
  @RequestMapping(value = "/v2/{tenant}/service/shop/{orgType}/{orgId}/{id}/get", method = RequestMethod.GET)
  RsMasResponse<RsStore> storeGet(
      @ApiParam(required = true) @PathVariable("tenant") String tenantId,
      @ApiParam(required = true) @PathVariable("orgType") String orgType,
      @ApiParam(required = true) @PathVariable("orgId") String orgId,
      @ApiParam(required = true) @PathVariable("id") String id);


  /**
   * 批量更新门店营业时间
   *
   * @param tenantId
   *     商户id
   * @param requestBody
   *     批量更新门店营业时间对象
   */
  @ApiOperation(value = "批量更新门店营业时间")
  @RequestMapping(value = "/v2/{tenant}/service/shop/{orgType}/{orgId}/batchUpdateBusinessHours", method = RequestMethod.POST)
  RsMasResponse storeBatchUpdateBusinessHours(@ApiParam(required = true) @PathVariable("tenant") String tenantId,
      @ApiParam(required = true) @PathVariable("orgType") String orgType,
      @ApiParam(required = true) @PathVariable("orgId") String orgId,
      @ApiParam(required = true, value = "批量更新门店营业时间对象") @RequestBody RsShopBatchUpdateBusinessHours requestBody,
      @ApiParam(required = true, value = "操作上下文") @RequestParam("operator") String operator);


  /**
   * 批量更新门店营业状态
   *
   * @param tenantId
   *     商户id
   * @param requestBody
   *     批量更新平台门店营业时间对象
   */
  @ApiOperation(value = "批量更新平台门店营业状态")
  @RequestMapping(value = "/v2/{tenant}/service/shop/{orgType}/{orgId}/batchUpdateBusinessState", method = RequestMethod.POST)
  RsMasResponse storeBatchUpdateBusinessState(@ApiParam(required = true) @PathVariable("tenant") String tenantId,
      @ApiParam(required = true) @PathVariable("orgType") String orgType,
      @ApiParam(required = true) @PathVariable("orgId") String orgId,
      @ApiParam(required = true, value = "批量更新平台门店营业时间对象") @RequestBody RsShopBatchUpdateBusinessState requestBody,
      @ApiParam(required = true, value = "操作上下文") @RequestParam("operator") String operator);


  /**
   * 查询出品部门分页列表
   *
   * @param tenantId
   *     租户id
   * @param filter
   *     查询条件
   * @return 出品部门列表
   */
  @ApiOperation(value = "查询出品部门分页列表")
  @RequestMapping(value = "/v2/{tenant}/service/stall/query", method = RequestMethod.POST)
  RsMasPageResponse<List<RsStall>> stallQuery(
      @ApiParam(required = true) @PathVariable("tenant") String tenantId,
      @ApiParam(required = true, value = "查询条件") @RequestBody RsStallFilter filter);

  /**
   * 创建出品部门
   *
   * @param tenantId
   *     租户id
   * @param creation
   *     出品部门创建信息
   * @param operator
   *     操作上下文
   */
  @ApiOperation(value = "创建出品部门")
  @RequestMapping(value = "/v2/{tenant}/service/stall/{orgType}/{orgId}/create", method = RequestMethod.POST)
  RsMasResponse stallCreate(@ApiParam(required = true) @PathVariable("tenant") String tenantId,
      @ApiParam(required = true) @PathVariable("orgType") String orgType,
      @ApiParam(required = true) @PathVariable("orgId") String orgId,
      @ApiParam(required = true, value = "出品部门创建信息") @RequestBody RsStallCreation creation,
      @ApiParam(required = true, value = "操作上下文") @RequestParam("operator") String operator);

  /**
   * 更新出品部门
   *
   * @param tenantId
   *     租户id
   * @param id
   *     出品部门id
   * @param modification
   *     出品部门修改信息
   * @param operator
   *     操作上下文
   */
  @ApiOperation(value = "更新出品部门")
  @RequestMapping(value = "/v2/{tenant}/service/stall/{orgType}/{orgId}/{id}/update", method = RequestMethod.POST)
  RsMasResponse stallUpdate(@ApiParam(required = true) @PathVariable("tenant") String tenantId,
      @ApiParam(required = true) @PathVariable("orgType") String orgType,
      @ApiParam(required = true) @PathVariable("orgId") String orgId,
      @PathVariable("id") String id,
      @ApiParam(required = true, value = "出品部门修改信息") @RequestBody RsStallModification modification,
      @ApiParam(required = true, value = "操作上下文") @RequestParam("operator") String operator);

  /**
   * 启用出品部门
   *
   * @param tenantId
   *     租户id
   * @param id
   *     出品部门id
   */
  @ApiOperation(value = "启用出品部门")
  @RequestMapping(value = "/v2/{tenant}/service/stall/{orgType}/{orgId}/{id}/enable", method = RequestMethod.POST)
  RsMasResponse stallEnable(@ApiParam(required = true) @PathVariable("tenant") String tenantId,
      @ApiParam(required = true) @PathVariable("orgType") String orgType,
      @ApiParam(required = true) @PathVariable("orgId") String orgId,
      @ApiParam(required = true, value = "出品部门id") @PathVariable("id") String id,
      @ApiParam(required = true, value = "操作上下文") @RequestParam("operator") String operator);

  /**
   * 停用出品部门
   *
   * @param tenantId
   *     租户id
   * @param id
   *     出品部门id
   */
  @ApiOperation(value = "停用出品部门")
  @RequestMapping(value = "/v2/{tenant}/service/stall/{orgType}/{orgId}/{id}/disable", method = RequestMethod.POST)
  RsMasResponse stallDisable(@ApiParam(required = true) @PathVariable("tenant") String tenantId,
      @ApiParam(required = true) @PathVariable("orgType") String orgType,
      @ApiParam(required = true) @PathVariable("orgId") String orgId,
      @ApiParam(required = true, value = "出品部门id") @PathVariable("id") String id,
      @ApiParam(required = true, value = "操作上下文") @RequestParam("operator") String operator);

  /**
   * 批量保存出品部门与收银机关联关系
   *
   * @param tenant
   *     租户
   * @param rsPosToStallBind
   *     POS机与出品部门绑定关系
   */
  @ApiOperation(value = "批量保存出品部门与收银机关联关系")
  @RequestMapping(value = "/v2/{tenant}/service/stall/{orgType}/{orgId}/bindPosToStall", method = RequestMethod.POST)
  RsMasResponse bindPosToStall(@ApiParam(required = true) @PathVariable("tenant") String tenant,
      @ApiParam(required = true) @PathVariable("orgType") String orgType,
      @ApiParam(required = true) @PathVariable("orgId") String orgId,
      @ApiParam(required = true, value = "POS机与出品部门绑定关系") @RequestBody RsPosToStallBind rsPosToStallBind);

  /**
   * 平台分类查询
   *
   * @param tenant
   *     租户
   * @param rsPlatformCategoryFilter
   *     平台分类查询条件
   * @return 平台分类查询结果
   */
  @ApiOperation(value = "平台分类查询")
  @RequestMapping(value = "/v2/{tenant}/service/platformcategory/query", method = RequestMethod.POST)
  RsMasPageResponse<List<RsPlatformCategory>> platformCategoryQuery(
      @ApiParam(required = true) @PathVariable("tenant") String tenant,
      @ApiParam(required = true, value = "平台分类查询条件") @RequestBody RsPlatformCategoryFilter rsPlatformCategoryFilter);

  /**
   * 获取指定的类目
   *
   * @param tenant
   *     租户
   * @param orgType
   *     组织类型
   * @param orgId
   *     组织id
   * @param id
   *     分类id
   * @return 平台分类
   */
  @ApiOperation(value = "获取指定的类目")
  @RequestMapping(value = "/v2/{tenant}/service/platformcategory/{org_type}/{org_id}/{platform_id}/{type}/{id}/get", method = RequestMethod.GET)
  @ResponseBody
  RsMasResponse<RsPlatformCategory> platformCategoryGet(
      @ApiParam(required = true) @PathVariable("tenant") String tenant,
      @ApiParam(required = true) @PathVariable("org_type") String orgType,
      @ApiParam(required = true) @PathVariable("org_id") String orgId,
      @ApiParam(required = true) @PathVariable("platform_id") String platformId,
      @ApiParam(required = true) @PathVariable("type") String type,
      @ApiParam(required = true) @PathVariable("id") String id);

  /**
   * 新建类目
   */
  @ApiOperation(value = "新建类目")
  @RequestMapping(value = "/v2/{tenant}/service/platformcategory/{org_type}/{org_id}/create", method = RequestMethod.POST)
  @ResponseBody
  RsMasResponse<String> platformCategoryCreate(
      @ApiParam(required = true) @PathVariable("tenant") String tenant,
      @ApiParam(required = true) @PathVariable("org_type") String orgType,
      @ApiParam(required = true) @PathVariable("org_id") String orgId,
      @ApiParam(required = true, value = "新建类目") @RequestBody RsPlatformCategoryCreation creation,
      @ApiParam(required = true, value = "操作上下文") @RequestParam("operator") String operator);

  /**
   * 更新类目
   */
  @ApiOperation(value = "更新类目")
  @RequestMapping(value = "/v2/{tenant}/service/platformcategory/{org_type}/{org_id}/{platform_id}/{type}/{id}/update", method = RequestMethod.POST)
  @ResponseBody
  RsMasResponse platformCategoryUpdate(
      @ApiParam(required = true) @PathVariable("tenant") String tenant,
      @ApiParam(required = true) @PathVariable("org_type") String orgType,
      @ApiParam(required = true) @PathVariable("org_id") String orgId,
      @ApiParam(required = true) @PathVariable("platform_id") String platformId,
      @ApiParam(required = true) @PathVariable("type") String type,
      @ApiParam(required = true) @PathVariable("id") String id,
      @ApiParam(required = true, value = "编辑用户") @RequestBody RsPlatformCategoryUpdate update,
      @ApiParam(required = true, value = "操作上下文") @RequestParam("operator") String operator);

  /**
   * 批量更新类目
   */
  @ApiOperation(value = "批量更新类目")
  @RequestMapping(value = "/v2/{tenant}/service/platformcategory/{org_type}/{org_id}/batchUpdate", method = RequestMethod.POST)
  @ResponseBody
  RsMasResponse platformCategoryBatchUpdate(
      @ApiParam(required = true) @PathVariable("tenant") String tenant,
      @ApiParam(required = true) @PathVariable("org_type") String orgType,
      @ApiParam(required = true) @PathVariable("org_id") String orgId,
      @ApiParam(required = true, value = "新建类目") @RequestBody RsMasRequest<List<RsPlatformCategoryUpdates>> updates,
      @ApiParam(required = true, value = "操作上下文") @RequestParam("operator") String operator);

  /**
   * 删除指定的类目
   */
  @ApiOperation(value = "删除指定的类目")
  @RequestMapping(value = "/v2/{tenant}/service/platformcategory/{org_type}/{org_id}/{platform_id}/{type}/{id}/remove", method = RequestMethod.GET)
  @ResponseBody
  RsMasResponse platformCategoryRemove(
      @ApiParam(required = true) @PathVariable("tenant") String tenant,
      @ApiParam(required = true) @PathVariable("org_type") String orgType,
      @ApiParam(required = true) @PathVariable("org_id") String orgId,
      @ApiParam(required = true) @PathVariable("platform_id") String platformId,
      @ApiParam(required = true) @PathVariable("type") String type,
      @ApiParam(required = true) @PathVariable("id") String id);

  /**
   * 批量显示分类
   *
   * @param tenant
   *     租户id
   * @param idsRequest
   *     分类Id列表
   * @param operator
   *     操作人
   * @return 操作结果
   */
  @ApiOperation(value = "批量显示分类")
  @RequestMapping(value = "/v2/{tenant}/service/platformcategory/{org_type}/{org_id}/batchShow", method = RequestMethod.POST)
  RsMasResponse platformCategoryBatchShow(
      @ApiParam(required = true) @PathVariable("tenant") String tenant,
      @ApiParam(required = true) @PathVariable("org_type") String orgType,
      @ApiParam(required = true) @PathVariable("org_id") String orgId,
      @ApiParam(required = true) @RequestBody RsMasRequest<List<String>> idsRequest,
      @ApiParam(required = true, value = "操作人") @RequestParam("operator") String operator);

  /**
   * 批量不显示分类
   *
   * @param tenant
   *     租户id
   * @param idsRequest
   *     分类Id列表
   * @param operator
   *     操作人
   * @return 操作结果
   */
  @ApiOperation(value = "批量不显示分类")
  @RequestMapping(value = "/v2/{tenant}/service/platformcategory/{org_type}/{org_id}/batchHide", method = RequestMethod.POST)
  RsMasResponse platformCategoryBatchHide(
      @ApiParam(required = true) @PathVariable("tenant") String tenant,
      @ApiParam(required = true) @PathVariable("org_type") String orgType,
      @ApiParam(required = true) @PathVariable("org_id") String orgId,
      @ApiParam(required = true) @RequestBody RsMasRequest<List<String>> idsRequest,
      @ApiParam(required = true, value = "操作人") @RequestParam("operator") String operator);

  /**
   * 查询平台类目商品
   */
  @ApiOperation(value = "查询平台类目商品")
  @RequestMapping(value = "/v2/{tenant}/service/platformcategorysku/query", method = RequestMethod.POST)
  RsMasPageResponse<List<RsPlatformCategorySku>> platformCategorySkuQuery(
      @ApiParam(required = true) @PathVariable("tenant") String tenant,
      @ApiParam(required = true, value = "查询条件") @RequestBody RsPlatformCategorySkuFilter filter);

  /**
   * 添加平台类目商品
   */
  @ApiOperation(value = "添加平台类目商品")
  @RequestMapping(value = "/v2/{tenant}/service/platformcategorysku/{org_type}/{org_id}/{platform_id}/{platform_category_type}/add", method = RequestMethod.POST)
  @ResponseBody
  RsMasResponse platformCategorySkuAdd(
      @ApiParam(required = true) @PathVariable("tenant") String tenant,
      @ApiParam(required = true) @PathVariable("org_type") String orgType,
      @ApiParam(required = true) @PathVariable("org_id") String orgId,
      @ApiParam(required = true) @PathVariable("platform_id") String platformId,
      @ApiParam(required = true) @PathVariable("platform_category_type") String platformCategoryType,
      @ApiParam(required = true) @RequestBody RsMasRequest<List<RsCategorySkuKey>> request,
      @ApiParam(required = false) @RequestParam(name = "operator", required = false) String operator);

  /**
   * 删除平台类目商品
   */
  @ApiOperation(value = "删除平台类目商品")
  @RequestMapping(value = "/v2/{tenant}/service/platformcategorysku/{org_type}/{org_id}/{platform_id}/{platform_category_type}/remove", method = RequestMethod.POST)
  @ResponseBody
  RsMasResponse platformCategorySkuRemove(
      @ApiParam(required = true) @PathVariable("tenant") String tenant,
      @ApiParam(required = true) @PathVariable("org_type") String orgType,
      @ApiParam(required = true) @PathVariable("org_id") String orgId,
      @ApiParam(required = true) @PathVariable("platform_id") String platformId,
      @ApiParam(required = true) @PathVariable("platform_category_type") String platformCategoryType,
      @ApiParam(required = true) @RequestBody RsMasRequest<List<RsCategorySkuKey>> request,
      @ApiParam @RequestParam(name = "operator", required = false) String operator);

  /**
   * 获取指定平台门店
   *
   * @param tenantId
   *     商户id
   * @param id
   *     门店id
   * @return 平台门店
   */
  @ApiOperation(value = "获取指定平台门店")
  @RequestMapping(value = "/v2/{tenant}/service/platformShop/{orgType}/{orgId}/{id}", method = RequestMethod.GET)
  RsMasResponse<RsPlatformShop> platformShopGet(
      @ApiParam(required = true) @PathVariable("tenant") String tenantId,
      @ApiParam(required = true) @PathVariable("orgType") String orgType,
      @ApiParam(required = true) @PathVariable("orgId") String orgId,
      @ApiParam(required = true) @PathVariable("id") String id);

  /**
   * 查询平台门店分页列表
   *
   * @param tenantId
   *     商户id
   * @param filter
   *     查询条件
   * @return 门店分页列表
   */
  @ApiOperation(value = "查询平台门店分页列表")
  @RequestMapping(value = "/v2/{tenant}/service/platformShop/query", method = RequestMethod.POST)
  RsMasPageResponse<List<RsPlatformShop>> platformShopQuery(
      @ApiParam(required = true) @PathVariable("tenant") String tenantId,
      @ApiParam(required = true, value = "查询条件") @RequestBody RsPlatformShopFilter filter);

  /**
   * 更新指定ID的门店资料 （元初）
   *
   * @param tenantId
   *     商户id
   * @param id
   *     门店id
   * @param update
   *     门店更新信息
   * @param operator
   *     操作人
   * @return 操作结果
   */
  @ApiOperation(value = "更新平台门店信息")
  @RequestMapping(value = "/v2/{tenant}/service/platformShop/{orgType}/{orgId}/{id}/update/yc", method = RequestMethod.POST)
  RsMasResponse<String> platformShopUpdateAsYc(
      @ApiParam(required = true) @PathVariable("tenant") String tenantId,
      @ApiParam(required = true) @PathVariable("orgType") String orgType,
      @ApiParam(required = true) @PathVariable("orgId") String orgId,
      @ApiParam(required = true) @PathVariable("id") String id,
      @ApiParam(required = true, value = "平台门店资料更新") @RequestBody RsYcPlatformShopUpdate update,
      @ApiParam(required = true, value = "操作人") @RequestParam("operator") String operator);

  /**
   * 更新指定ID的平台门店资料 （元初）
   *
   * @param tenantId
   *     商户id
   * @param id
   *     门店id
   * @param newState
   *     门店更新信息
   * @param operator
   *     操作人
   * @return 操作结果
   */
  @ApiOperation(value = "更新指定ID的平台门店营业状态")
  @RequestMapping(value = "/v2/{tenant}/service/platformShop/{orgType}/{orgId}/{id}/businessState/update/yc", method = RequestMethod.POST)
  RsMasResponse<String> platformShopUpdateBusinessStateAsYc(
      @ApiParam(required = true) @PathVariable("tenant") String tenantId,
      @ApiParam(required = true) @PathVariable("orgType") String orgType,
      @ApiParam(required = true) @PathVariable("orgId") String orgId,
      @ApiParam(required = true) @PathVariable("id") String id,
      @ApiParam(required = true, value = "变更后的平台门店营业状态") @RequestBody RsMasRequest<String> newState,
      @ApiParam(required = true, value = "操作人") @RequestParam("operator") String operator);

  /**
   * 批量更新平台门店营业状态
   *
   * @param tenantId
   *     商户id
   * @param operator
   *     操作人
   */
  @ApiOperation(value = "批量更新平台门店营业状态")
  @RequestMapping(value = "/v2/{tenant}/service/platformShop/{orgType}/{orgId}/batchUpdateBusinessState", method = RequestMethod.POST)
  RsMasResponse platformShopBatchUpdateBusinessState(
      @ApiParam(required = true) @PathVariable("tenant") String tenantId,
      @ApiParam(required = true) @PathVariable("orgType") String orgType,
      @ApiParam(required = true) @PathVariable("orgId") String orgId,
      @ApiParam(required = true, value = "批量更新平台门店营业时间对象") @RequestBody RsPlatformShopBatchUpdateBusinessState requestBody,
      @ApiParam(required = true, value = "操作上下文") @RequestParam("operator") String operator);

  /**
   * 批量更新平台门店营业时间
   *
   * @param tenantId
   *     商户id
   * @param operator
   *     操作人
   */
  @ApiOperation(value = "批量更新平台门店营业时间")
  @RequestMapping(value = "/v2/{tenant}/service/platformShop/{orgType}/{orgId}/batchUpdateBusinessHour", method = RequestMethod.POST)
  RsMasResponse platformShopBatchUpdateBusinessHour(
      @ApiParam(required = true) @PathVariable("tenant") String tenantId,
      @ApiParam(required = true) @PathVariable("orgType") String orgType,
      @ApiParam(required = true) @PathVariable("orgId") String orgId,
      @ApiParam(required = true, value = "批量更新平台门店营业时间对象") @RequestBody RsPlatformShopBatchUpdateBusinessHour requestBody,
      @ApiParam(required = true, value = "操作上下文") @RequestParam("operator") String operator);

  /**
   * 查询搭配列表
   *
   * @param tenantId
   *     租户id
   * @param filter
   *     查询条件
   * @return 查询搭配列表
   */
  @ApiOperation(value = "查询搭配列表")
  @RequestMapping(value = "/v2/{tenant}/service/collocation/query", method = RequestMethod.POST)
  RsMasPageResponse<List<RsCollocationGroup>> collocationQuery(
      @ApiParam(required = true) @PathVariable("tenant") String tenantId,
      @ApiParam(required = true, value = "查询条件") @RequestBody RsCollocationGroupFilter filter);

  /**
   * 获取指定搭配
   *
   * @param tenantId
   *     租户id
   * @param id
   *     搭配id
   * @return 搭配信息
   */
  @ApiOperation(value = "获取指定搭配")
  @RequestMapping(value = "/v2/{tenant}/service/collocation/{orgType}/{orgId}/{id}", method = RequestMethod.GET)
  RsMasResponse<RsCollocationGroup> collocationGet(
      @ApiParam(required = true) @PathVariable("tenant") String tenantId,
      @ApiParam(required = true) @PathVariable("orgType") String orgType,
      @ApiParam(required = true) @PathVariable("orgId") String orgId,
      @ApiParam(required = true) @PathVariable("id") String id);

  /**
   * 更新搭配
   *
   * @param tenantId
   *     租户id
   * @param id
   *     出品部门id
   * @param modification
   *     搭配更新信息
   * @param operator
   *     操作上下文
   */
  @ApiOperation(value = "更新搭配")
  @RequestMapping(value = "/v2/{tenant}/service/collocation/{orgType}/{orgId}/{id}/update", method = RequestMethod.POST)
  RsMasResponse collocationUpdate(
      @ApiParam(required = true) @PathVariable("tenant") String tenantId,
      @ApiParam(required = true) @PathVariable("orgType") String orgType,
      @ApiParam(required = true) @PathVariable("orgId") String orgId,
      @PathVariable("id") String id,
      @ApiParam(required = true, value = "搭配更新信息") @RequestBody RsCollocationGroupUpdate modification,
      @ApiParam(required = true, value = "操作上下文") @RequestParam("operator") String operator);

  /**
   * 新建搭配
   *
   * @param tenantId
   *     租户id
   * @param collocationGroupCreation
   *     搭配新建请求
   * @param operator
   *     操作人
   * @return 新建结果
   */
  @ApiOperation(value = "新建搭配")
  @RequestMapping(value = "/v2/{tenant}/service/collocation/{orgType}/{orgId}/create", method = RequestMethod.POST)
  RsMasResponse collocationCreate(
      @ApiParam(required = true) @PathVariable("tenant") String tenantId,
      @ApiParam(required = true) @PathVariable("orgType") String orgType,
      @ApiParam(required = true) @PathVariable("orgId") String orgId,
      @ApiParam(required = true, value = "搭配新建请求") @RequestBody RsCollocationGroupCreation collocationGroupCreation,
      @ApiParam(required = true, value = "操作人") @RequestParam("operator") String operator);

  /**
   * 查询口味组
   *
   * @param tenantId
   *     租户id
   * @param filter
   *     查询条件
   * @return 口味组列表
   */
  @ApiOperation(value = "查询口味组")
  @RequestMapping(value = "/v2/{tenant}/service/tastegroup/query", method = RequestMethod.POST)
  RsMasPageResponse<List<RsTasteGroup>> tasteQuery(
      @ApiParam(required = true) @PathVariable("tenant") String tenantId,
      @ApiParam(required = true, value = "查询条件") @RequestBody RsTasteGroupFilter filter);

  /**
   * 新建口味组
   *
   * @param tenantId
   *     租户id
   * @param tasteGroupCreation
   *     口味组新建请求
   * @param operator
   *     操作人
   * @return 新建结果
   */
  @ApiOperation(value = "新建口味组")
  @RequestMapping(value = "/v2/{tenant}/service/tastegroup/{orgType}/{orgId}/create", method = RequestMethod.POST)
  RsMasResponse tasteCreate(@ApiParam(required = true) @PathVariable("tenant") String tenantId,
      @ApiParam(required = true) @PathVariable("orgType") String orgType,
      @ApiParam(required = true) @PathVariable("orgId") String orgId,
      @ApiParam(required = true, value = "口味组新建请求") @RequestBody RsTasteGroupCreation tasteGroupCreation,
      @ApiParam(required = true, value = "操作人") @RequestParam("operator") String operator);

  /**
   * 更新口味组
   *
   * @param tenantId
   *     租户id
   * @param id
   *     口味组id
   * @param tasteGroupUpdate
   *     口味组更新信息
   * @param operator
   *     操作人
   * @return 更新结果
   */
  @ApiOperation(value = "更新口味组")
  @RequestMapping(value = "/v2/{tenant}/service/tastegroup/{orgType}/{orgId}/{id}/update", method = RequestMethod.POST)
  RsMasResponse tasteUpdate(@ApiParam(required = true) @PathVariable("tenant") String tenantId,
      @ApiParam(required = true) @PathVariable("orgType") String orgType,
      @ApiParam(required = true) @PathVariable("orgId") String orgId,
      @ApiParam(required = true) @PathVariable("id") String id,
      @ApiParam(required = true, value = "口味组更新信息") @RequestBody RsTasteGroupUpdate tasteGroupUpdate,
      @ApiParam(required = true, value = "操作人") @RequestParam("operator") String operator);

  /**
   * 获取指定口味组
   *
   * @param tenantId
   *     租户id
   * @param id
   *     口味组id
   * @return 口味组信息
   */
  @ApiOperation(value = "获取指定口味组")
  @RequestMapping(value = "/v2/{tenant}/service/tastegroup/{orgType}/{orgId}/{id}", method = RequestMethod.GET)
  RsMasResponse<RsTasteGroup> tasteGet(
      @ApiParam(required = true) @PathVariable("tenant") String tenantId,
      @ApiParam(required = true) @PathVariable("orgType") String orgType,
      @ApiParam(required = true) @PathVariable("orgId") String orgId,
      @ApiParam(required = true) @PathVariable("id") String id);

  /**
   * 解除指定口味组和商品的关系
   *
   * @param tenantId
   *     租户id
   * @param id
   *     口味组id
   * @param unbind
   *     口味组id
   * @param operator
   *     操作人
   * @return 删除结果
   */
  @ApiOperation(value = "删除口味组")
  @RequestMapping(value = "/v2/{tenant}/service/tastegroup/{orgType}/{orgId}/{id}/sku/unbind", method = RequestMethod.POST)
  RsMasResponse tasteUnbindSKU(@ApiParam(required = true) @PathVariable("tenant") String tenantId,
      @ApiParam(required = true) @PathVariable("orgType") String orgType,
      @ApiParam(required = true) @PathVariable("orgId") String orgId,
      @ApiParam(required = true) @PathVariable("id") String id,
      @ApiParam(required = true, value = "解除口味组和商品的关系请求") @RequestBody RsTasteGroupSKUUnbind unbind,
      @ApiParam(required = true, value = "操作人") @RequestParam("operator") String operator);

  /**
   * 批量保存SKU口味组
   *
   * @param tenantId
   *     租户id
   * @param request
   *     批量保存SKU口味组
   */
  @ApiOperation(value = "批量保存SKU口味组")
  @RequestMapping(value = "/v2/{tenant}/service/sku/{orgType}/{orgId}/taste-group/batch-save", method = RequestMethod.POST)
  RsMasResponse batchSaveTasteGroup(
      @ApiParam(required = true) @PathVariable("tenant") String tenantId,
      @ApiParam(required = true) @PathVariable("orgType") String orgType,
      @ApiParam(required = true) @PathVariable("orgId") String orgId,
      @ApiParam(required = true) @RequestBody RsMasRequest<List<RsSKUTasteGroupLine>> request);

  /**
   * 批量保存Sku加料组
   *
   * @param tenantId
   *     租户id
   * @param request
   *     sku加料组批量修改请求
   */
  @ApiOperation(value = "批量保存Sku加料组")
  @RequestMapping(value = "/v2/{tenant}/service/sku/{orgType}/{orgId}/collocation-group/batch-save", method = RequestMethod.POST)
  RsMasResponse skuBatchSaveCollocationGroup(
      @ApiParam(required = true) @PathVariable("tenant") String tenantId,
      @ApiParam(required = true) @PathVariable("orgType") String orgType,
      @ApiParam(required = true) @PathVariable("orgId") String orgId,
      @ApiParam(required = true) @RequestBody RsMasRequest<List<RsSkuCollocationGroupLine>> request);

  /**
   * 查询指定类型的选项
   *
   * @param tenant
   *     租户id
   * @param type
   *     类型
   * @param filter
   *     查询条件
   * @return 指定的选项
   */
  @ApiOperation(value = "查询指定的选项")
  @RequestMapping(value = "/v2/{tenant}/service/options/{type}/query", method = RequestMethod.POST)
  @ResponseBody
  RsMasResponse<List<RsOptions>> optionsQuery(
      @ApiParam(required = true) @PathVariable("tenant") String tenant,
      @ApiParam(required = true) @PathVariable("type") String type,
      @ApiParam(required = true, value = "查询条件") @RequestBody RsOptionsFilter filter);

  /**
   * 新建指定类型的选项
   *
   * @param creation
   *     新建选项
   * @param operator
   *     操作上下文
   */
  @ApiOperation(value = "新建指定类型的选项")
  @RequestMapping(value = "/v2/{tenant}/service/options/{orgType}/{orgId}/{type}/create", method = RequestMethod.POST)
  @ResponseBody
  RsMasResponse optionsCreation(@ApiParam(required = true) @PathVariable("tenant") String tenant,
      @ApiParam(required = true) @PathVariable("orgType") String orgType,
      @ApiParam(required = true) @PathVariable("orgId") String orgId,
      @ApiParam(required = true) @PathVariable("type") String type,
      @ApiParam(required = true, value = "新建选项") @RequestBody RsOptionsCreation creation,
      @ApiParam(required = true, value = "操作上下文") @RequestParam("operator") String operator);

  /**
   * 初始化选项
   *
   * @param tenantId
   *     租户id
   * @param batchInit
   *     初始化选项请求
   * @param operator
   *     操作人
   * @return 初始化结果
   */
  @ApiParam(value = "初始化选项")
  @RequestMapping(value = "/v2/{tenant}/service/options/{orgType}/{orgId}/batchInit", method = RequestMethod.POST)
  @ResponseBody
  RsMasResponse optionsBatchInit(@ApiParam(required = true) @PathVariable("tenant") String tenantId,
      @ApiParam(required = true) @PathVariable("orgType") String orgType,
      @ApiParam(required = true) @PathVariable("orgId") String orgId,
      @ApiParam(required = true, value = "初始化选项请求") @RequestBody RsOptionsBatchInit batchInit,
      @ApiParam(required = true, value = "操作人") @RequestParam("operator") String operator);

  /**
   * 查询作业
   */
  @ApiOperation(value = "查询作业")
  @RequestMapping(value = "/v2/{tenant}/commons/service/task/query", method = RequestMethod.POST)
  RsMasPageResponse<List<RsTask>> taskQuery(
      @ApiParam(required = true) @PathVariable("tenant") String tenantId,
      @ApiParam(required = true, value = "查询条件") @RequestBody RsTaskFilter filter);

  /**
   * 获取作业
   */
  @ApiOperation(value = "获取作业")
  @RequestMapping(value = "/v2/{tenant}/commons/service/task/{orgType}/{orgId}/get/{id}", method = RequestMethod.GET)
  RsMasResponse<RsTask> taskGet(@ApiParam(required = true) @PathVariable("tenant") String tenant,
      @ApiParam(required = true) @PathVariable("orgType") String orgType,
      @ApiParam(required = true) @PathVariable("orgId") String orgId,
      @ApiParam(required = true) @PathVariable("id") String id) throws Exception;

  /**
   * 提交新作业
   */
  @ApiOperation(value = "提交新作业")
  @RequestMapping(value = "/v2/{tenant}/commons/service/task/{orgType}/{orgId}/submit", method = RequestMethod.POST)
  RsMasResponse<String> taskSubmit(@ApiParam(required = true) @PathVariable("tenant") String tenant,
      @ApiParam(required = true) @PathVariable("orgType") String orgType,
      @ApiParam(required = true) @PathVariable("orgId") String orgId,
      @ApiParam(required = true, value = "新建选项") @RequestBody RsTaskSubmit submit,
      @ApiParam(required = true, value = "操作上下文") @RequestParam("operator") String operator)
      throws Exception;

  /**
   * 生效作业
   */
  @ApiOperation(value = "生效作业")
  @RequestMapping(value = "/v2/{tenant}/commons/service/task/{orgType}/{orgId}/effect/{id}", method = RequestMethod.POST)
  RsMasResponse taskEffect(@ApiParam(required = true) @PathVariable("tenant") String tenant,
      @ApiParam(required = true) @PathVariable("orgType") String orgType,
      @ApiParam(required = true) @PathVariable("orgId") String orgId,
      @ApiParam(required = true) @PathVariable("id") String id,
      @ApiParam(required = true, value = "操作上下文") @RequestParam("operator") String operator)
      throws Exception;

  /**
   * 查询作业执行记录
   */
  @ApiOperation(value = "查询作业执行记录")
  @RequestMapping(value = "/v2/{tenant}/commons/service/task/executeRecord/query", method = RequestMethod.POST)
  @ResponseBody
  RsMasPageResponse<List<TaskExecuteRecord>> taskQueryExecuteRecord(
      @ApiParam(required = true) @PathVariable("tenant") String tenantId,
      @ApiParam(required = true, value = "查询条件") @RequestBody RsTaskExecuteRecordFilter filter);

  /**
   * 报告作业执行情况
   */
  @ApiOperation(value = "报告作业执行情况")
  @RequestMapping(value = "/v2/{tenant}/commons/service/task/{orgType}/{orgId}/executeRecord/report/{id}", method = RequestMethod.POST)
  RsMasResponse taskReport(@ApiParam(required = true) @PathVariable("tenant") String tenantId,
      @ApiParam(required = true) @PathVariable("orgType") String orgType,
      @ApiParam(required = true) @PathVariable("orgId") String orgId,
      @ApiParam(required = true) @PathVariable("id") String id,
      @ApiParam(required = true, value = "作业执行报告") @RequestBody RsTaskExecuteReport report,
      @ApiParam(required = true, value = "操作上下文") @RequestParam("operator") String operator)
      throws Exception;

  /**
   * 查询类目
   */
  @ApiOperation(value = "查询类目")
  @RequestMapping(value = "/v2/{tenant}/service/platshopcategory/query", method = RequestMethod.POST)
  @ResponseBody
  RsMasPageResponse<List<RsPlatShopCategory>> platShopCategoryQuery(
      @ApiParam(required = true) @PathVariable("tenant") String tenant,
      @ApiParam(required = true, value = "查询条件") @RequestBody RsPlatShopCategoryFilter filter);

  /**
   * 获取指定的类目
   */
  @ApiOperation(value = "获取指定的类目")
  @RequestMapping(value = "/v2/{tenant}/service/platshopcategory/{org_type}/{org_id}/{shop_id}/{platform_id}/{type}/{id}/get", method = RequestMethod.GET)
  @ResponseBody
  RsMasResponse<RsPlatShopCategory> platShopCategoryGet(
      @ApiParam(required = true) @PathVariable("tenant") String tenant,
      @ApiParam(required = true) @PathVariable("org_type") String orgType,
      @ApiParam(required = true) @PathVariable("org_id") String orgId,
      @ApiParam(required = true) @PathVariable("shop_id") String shopId,
      @ApiParam(required = true) @PathVariable("platform_id") String platformId,
      @ApiParam(required = true) @PathVariable("type") String type,
      @ApiParam(required = true) @PathVariable("id") String id);

  /**
   * 更新类目
   */
  @ApiOperation(value = "更新类目")
  @RequestMapping(value = "/v2/{tenant}/service/platshopcategory/{org_type}/{org_id}/{shop_id}/{platform_id}/{type}/{id}/update", method = RequestMethod.POST)
  @ResponseBody
  RsMasResponse<String> platShopCategoryUpdate(
      @ApiParam(required = true) @PathVariable("tenant") String tenant,
      @ApiParam(required = true) @PathVariable("org_type") String orgType,
      @ApiParam(required = true) @PathVariable("org_id") String orgId,
      @ApiParam(required = true) @PathVariable("shop_id") String shopId,
      @ApiParam(required = true) @PathVariable("platform_id") String platformId,
      @ApiParam(required = true) @PathVariable("type") String type,
      @ApiParam(required = true) @PathVariable("id") String id,
      @ApiParam(required = true, value = "编辑用户") @RequestBody RsPlatShopCategoryUpdate update,
      @ApiParam(required = true, value = "操作上下文") @RequestParam("operator") String operator);

  @ApiOperation(value = "批量更新类目")
  @RequestMapping(value = "/v2/{tenant}/service/platshopcategory/{orgType}/{orgId}/batchUpdate", method = RequestMethod.POST)
  @ResponseBody
  RsMasResponse platShopCategoryBatchUpdate(
      @ApiParam(required = true) @PathVariable("tenant") String tenant,
      @ApiParam(required = true) @PathVariable("orgType") String orgType,
      @ApiParam(required = true) @PathVariable("orgId") String orgId,
      @ApiParam(required = true, value = "新建类目") @RequestBody RsMasRequest<List<RsPlatShopCategoryBatchUpdate>> updates,
      @ApiParam(required = true, value = "操作上下文") @RequestParam("operator") String operator);

  @ApiOperation(value = "批量显示分类")
  @RequestMapping(value = "/v2/{tenant}/service/platshopcategory/{org_type}/{org_id}/{shop_id}/{platform_id}/{type}/batchShow", method = RequestMethod.POST)
  RsMasResponse platShopCategoryBatchShow(
      @ApiParam(required = true) @PathVariable("tenant") String tenantId,
      @ApiParam(required = true) @PathVariable("org_type") String orgType,
      @ApiParam(required = true) @PathVariable("org_id") String orgId,
      @ApiParam(required = true) @PathVariable("shop_id") String shopId,
      @ApiParam(required = true) @PathVariable("platform_id") String platformId,
      @ApiParam(required = true) @PathVariable("type") String type,
      @ApiParam(required = true) @RequestBody RsMasRequest<List<String>> idsRequest,
      @ApiParam(required = true, value = "操作人") @RequestParam("operator") String operator);

  @ApiOperation(value = "批量不显示分类")
  @RequestMapping(value = "/v2/{tenant}/service/platshopcategory/{org_type}/{org_id}/{shop_id}/{platform_id}/{type}/batchNotShow", method = RequestMethod.POST)
  RsMasResponse platShopCategoryBatchNotShow(
      @ApiParam(required = true) @PathVariable("tenant") String tenantId,
      @ApiParam(required = true) @PathVariable("org_type") String orgType,
      @ApiParam(required = true) @PathVariable("org_id") String orgId,
      @ApiParam(required = true) @PathVariable("shop_id") String shopId,
      @ApiParam(required = true) @PathVariable("platform_id") String platformId,
      @ApiParam(required = true) @PathVariable("type") String type,
      @ApiParam(required = true) @RequestBody RsMasRequest<List<String>> idsRequest,
      @ApiParam(required = true, value = "操作人") @RequestParam("operator") String operator);

  @ApiOperation(value = "删除指定的类目")
  @RequestMapping(value = "/v2/{tenant}/service/platshopcategory/{org_type}/{org_id}/{shop_id}/{platform_id}/{type}/{id}/remove", method = RequestMethod.GET)
  @ResponseBody
  RsMasResponse platShopCategoryRemove(
      @ApiParam(required = true) @PathVariable("tenant") String tenant,
      @ApiParam(required = true) @PathVariable("org_type") String orgType,
      @ApiParam(required = true) @PathVariable("org_id") String orgId,
      @ApiParam(required = true) @PathVariable("shop_id") String shopId,
      @ApiParam(required = true) @PathVariable("platform_id") String platformId,
      @ApiParam(required = true) @PathVariable("type") String type,
      @ApiParam(required = true) @PathVariable("id") String id,
      @ApiParam(required = true, value = "操作人") @RequestParam("operator") String operator);

  @ApiOperation(value = "新建类目")
  @RequestMapping(value = "/v2/{tenant}/service/platshopcategory/{org_type}/{org_id}/create", method = RequestMethod.POST)
  @ResponseBody
  RsMasResponse platShopCategoryCreate(
      @ApiParam(required = true) @PathVariable("tenant") String tenant,
      @ApiParam(required = true) @PathVariable("org_type") String orgType,
      @ApiParam(required = true) @PathVariable("org_id") String orgId,
      @ApiParam(required = true, value = "新建类目") @RequestBody RsPlatShopCategoryCreation creation,
      @ApiParam(required = true, value = "操作上下文") @RequestParam("operator") String operator);

  @ApiOperation(value = "添加门店平台类目商品")
  @RequestMapping(value = "/v2/{tenant}/service/platshopcategorysku/{org_type}/{org_id}/{shop_id}/{platform_id}/add", method =
      RequestMethod.POST)
  @ResponseBody
  RsMasResponse platShopCategoryAddSku(
      @ApiParam(required = true) @PathVariable("tenant") String tenant,
      @ApiParam(required = true) @PathVariable("org_type") String orgType,
      @ApiParam(required = true) @PathVariable("org_id") String orgId,
      @ApiParam(required = true) @PathVariable("shop_id") String shopId,
      @ApiParam(required = true) @PathVariable("platform_id") String platformId,
      @ApiParam(required = true) @RequestBody RsMasRequest<List<RsPlatShopCategorySkuKey>> request,
      @ApiParam(required = false) @RequestParam(name = "operator", required = false) String operator);

  @ApiOperation(value = "删除门店平台类目商品")
  @RequestMapping(value = "/v2/{tenant}/service/platshopcategorysku/{org_type}/{org_id}/{shop_id}/{platform_id}/remove", method =
      RequestMethod.POST)
  @ResponseBody
  RsMasResponse platShopCategoryRemoveSku(
      @ApiParam(required = true) @PathVariable("tenant") String tenant,
      @ApiParam(required = true) @PathVariable("org_type") String orgType,
      @ApiParam(required = true) @PathVariable("org_id") String orgId,
      @ApiParam(required = true) @PathVariable("shop_id") String shopId,
      @ApiParam(required = true) @PathVariable("platform_id") String platformId,
      @ApiParam(required = true) @RequestBody RsMasRequest<List<RsPlatShopCategorySkuKey>> request);

  @ApiOperation(value = "批量新建类目")
  @RequestMapping(value = "/v2/{tenant}/service/platshopcategory/{org_type}/{org_id}/batchCreate", method = RequestMethod.POST)
  @ResponseBody
  RsMasResponse platShopCategoryBatchCreate(
      @ApiParam(required = true) @PathVariable("tenant") String tenant,
      @ApiParam(required = true) @PathVariable("org_type") String orgType,
      @ApiParam(required = true) @PathVariable("org_id") String orgId,
      @ApiParam(required = true, value = "新建类目") @RequestBody RsMasRequest<List<RsPlatShopCategoryCreation>> creations,
      @ApiParam(required = true, value = "操作上下文") @RequestParam("operator") String operator);

  @ApiOperation(value = "查询门店平台类目商品")
  @RequestMapping(value = "/v2/{tenant}/service/platshopcategorysku/query", method = RequestMethod.POST)
  @ResponseBody
  RsMasResponse<List<RsPlatShopCategorySku>> platShopCategorySkuQuery(
      @ApiParam(required = true) @PathVariable("tenant") String tenant,
      @ApiParam(required = true, value = "查询条件") @RequestBody RsPlatShopCategorySkuFilter filter);

  @ApiOperation(value = "更新商品的门店平台类目")
  @RequestMapping(value = "/v2/{tenant}/service/platshopcategorysku/{org_type}/{org_id}/{shop_id}/{platform_id}/updateCategoryOfSku", method = RequestMethod.POST)
  @ResponseBody
  RsMasResponse updateCategoryOfSku(
      @ApiParam(required = true) @PathVariable("tenant") String tenant,
      @ApiParam(required = true) @PathVariable("org_type") String orgType,
      @ApiParam(required = true) @PathVariable("org_id") String orgId,
      @ApiParam(required = true) @PathVariable("shop_id") String shopId,
      @ApiParam(required = true) @PathVariable("platform_id") String platformId,
      @ApiParam(required = true) @RequestBody RsMasRequest<List<RsSkuPlatShopCategoryKey>> request,
      @ApiParam(required = false) @RequestParam(name = "operator", required = false) String operator);

  @ApiOperation(value = "批量新建类目")
  @RequestMapping(value = "/v2/{tenant}/service/platshopcategorysku/{org_type}/{org_id}/{platform_id}/batchRemove", method = RequestMethod.POST)
  @ResponseBody
  RsMasResponse platShopCategoryBatchRemove(
      @ApiParam(required = true) @PathVariable("tenant") String tenant,
      @ApiParam(required = true) @PathVariable("org_type") String orgType,
      @ApiParam(required = true) @PathVariable("org_id") String orgId,
      @ApiParam(required = true) @PathVariable("platform_id") String platformId,
      @ApiParam(required = true) @RequestBody RsMasRequest<List<RsBatchRemovePlatShopCategorySkuKey>> request,
      @ApiParam(required = false) @RequestParam(name = "operator", required = false) String operator);

  /**
   * 更新平台类目的门店商品
   */
  @ApiOperation(value = "更新平台类目的门店商品")
  @RequestMapping(value = "/v2/{tenant}/service/platshopcategorysku/{org_type}/{org_id}/{shop_id}/{platform_id}/{plat_shop_categoryId}/updateSkuOfCategory", method =
      RequestMethod.POST)
  @ResponseBody
  RsMasResponse updateSkuOfCategory(@ApiParam(required = true) @PathVariable("tenant") String tenant,
      @ApiParam(required = true) @PathVariable("org_type") String orgType,
      @ApiParam(required = true) @PathVariable("org_id") String orgId,
      @ApiParam(required = true) @PathVariable("shop_id") String shopId,
      @ApiParam(required = true) @PathVariable("platform_id") String platformId,
      @ApiParam(required = true) @PathVariable("plat_shop_categoryId") String platShopCategoryId,
      @ApiParam(required = true) @RequestBody RsMasRequest<List<String>> request,
      @ApiParam(required = false) @RequestParam(name = "operator", required = false) String operator);

  /**
   * 更新门店平台类目商品
   */
  @ApiOperation(value = "更新门店平台类目商品")
  @RequestMapping(value = "/v2/{tenant}/service/platshopcategorysku/{org_type}/{org_id}/update", method = RequestMethod.POST)
  @ResponseBody
  RsMasResponse shopSkuInvRuleUpdate(@ApiParam(required = true) @PathVariable("tenant") String tenant,
      @ApiParam(required = true) @PathVariable("org_type") String orgType,
      @ApiParam(required = true) @PathVariable("org_id") String orgId,
      @ApiParam(required = true) @RequestBody RsMasRequest<List<RsPlatShopCategorySkuUpdate>> update,
      @ApiParam @RequestParam(name = "operator", required = false) String operator);

  /**
   * 更新门店平台类目商品
   */
  @ApiOperation(value = "根据门店平台类目清除排序值")
  @RequestMapping(value = "/v2/{tenant}/service/platshopcategorysku/{org_type}/{org_id}/{shop_id}/clearSort", method = RequestMethod.POST)
  @ResponseBody
  RsMasResponse shopSkuInvRuleClearSort(@ApiParam(required = true) @PathVariable("tenant") String tenant,
      @ApiParam(required = true) @PathVariable("org_type") String orgType,
      @ApiParam(required = true) @PathVariable("org_id") String orgId,
      @ApiParam(required = true) @PathVariable("shop_id") String shopId,
      @ApiParam(required = true) @RequestBody RsMasRequest<List<String>> platShopCategoryIdsRequest,
      @ApiParam(required = false) @RequestParam(name = "operator", required = false) String operator);


  @ApiOperation(value = "查询规则列表")
  @RequestMapping(value = "/v2/{tenant}/service/shopskuinvrule/query", method = RequestMethod.POST)
  @ResponseBody
  RsMasPageResponse<List<RsShopSkuInvRule>> shopSkuInvRuleQuery(
      @ApiParam(required = true) @PathVariable("tenant") String tenantId,
      @ApiParam(required = true) @RequestBody RsShopSkuInvRuleFilter filter);

  @ApiOperation(value = "批量保存规则")
  @RequestMapping(value = "/v2/{tenant}/service/shopskuinvrule/{orgType}/{orgId}/batchSave", method = RequestMethod.POST)
  @ResponseBody
  RsMasResponse<List<String>> shopSkuInvRuleBatchSave(
      @ApiParam(required = true) @PathVariable("tenant") String tenantId,
      @ApiParam(required = true) @PathVariable("orgType") String orgType,
      @ApiParam(required = true) @PathVariable("orgId") String orgId,
      @ApiParam(required = true) @RequestBody RsMasRequest<RsBatchSaveShopSkuInvRule> rule,
      @ApiParam(required = true) @RequestParam("operator") String operator);

  /**
   * 删除规则(多组织)
   *
   * @param tenant
   *     租户id
   * @param operator
   *     操作人
   */
  @ApiOperation(value = "批量删除规则(多组织)")
  @RequestMapping(value = "/v2/{tenant}/service/shopskuinvrule/{orgType}/{orgId}/bathDelete", method = RequestMethod.POST)
  RsMasResponse<String> shopSkuInvRuleBathDelete(@ApiParam(required = true) @PathVariable("tenant") String tenant,
      @ApiParam(required = true) @PathVariable("orgType") String orgType,
      @ApiParam(required = true) @PathVariable("orgId") String orgId,
      @ApiParam(required = true, value = "门店id商品id") @RequestBody RsMasRequest<List<RsShopSkuIdKey>> request,
      @ApiParam(required = true, value = "操作人") @RequestParam("operator") String operator);


  @ApiOperation(value = "删除规则")
  @RequestMapping(value = "/v2/{tenant}/service/shopskuinvrule/{orgType}/{orgId}/{id}/delete", method = RequestMethod.POST)
  @ResponseBody
  RsMasResponse<String> shopSkuInvRuleDelete(
      @ApiParam(required = true) @PathVariable("tenant") String tenantId,
      @ApiParam(required = true) @PathVariable("orgType") String orgType,
      @ApiParam(required = true) @PathVariable("orgId") String orgId,
      @ApiParam(required = true) @PathVariable("id") String id,
      @ApiParam(required = true) @RequestParam("operator") String operator);

  /**
   * 查询租户
   */
  @ApiOperation(value = "查询租户")
  @RequestMapping(value = "/v2/service/tenant/query", method = RequestMethod.POST)
  @ResponseBody
  RsMasResponse<List<RsTenant>> tenantQuery(
      @ApiParam(required = true, value = "查询条件") @RequestBody RsTenantFilter filter);

  /**
   * 保存（新增或修改）收藏
   */
  @ApiOperation(value = "保存收藏")
  @RequestMapping(value = "/v2/{tenant}/service/follow/save", method = RequestMethod.POST)
  @ResponseBody
  RsMasResponse followSave(
      @ApiParam(required = true) @PathVariable("tenant") String tenant,
      @ApiParam(required = true, value = "收藏列表") @RequestBody RsMasRequest<List<RsFollow>> request)
      throws Exception;

  /**
   * 查询收藏
   */
  @ApiOperation(value = "查询收藏")
  @RequestMapping(value = "/v2/{tenant}/service/follow/query", method = RequestMethod.POST)
  @ResponseBody
  RsMasPageResponse<List<RsFollow>> followQuery(
      @ApiParam(required = true) @PathVariable("tenant") String tenant,
      @ApiParam(required = true, value = "查询条件") @RequestBody RsFollowFilter filter);

  /**
   * 删除收藏
   */
  @ApiOperation(value = "删除收藏")
  @RequestMapping(value = "/v2/{tenant}/service/follow/remove", method = RequestMethod.POST)
  @ResponseBody
  RsMasResponse followRemove(
      @ApiParam(required = true) @PathVariable("tenant") String tenant,
      @ApiParam(required = true) @RequestBody RsMasRequest<List<String>> uuids);

  /**
   * 批量解绑SKU加料组
   *
   * @param tenantId
   *     租户id
   * @param request
   *     根据skuUUID集合批量解绑SKU加料组
   */
  @ApiOperation(value = "根据skuUUID集合批量解绑SKU加料组")
  @RequestMapping(value = "/v2/{tenant}/service/sku/{orgType}/{orgId}/collocation-group/batch-unbind", method = RequestMethod.DELETE)
  RsMasResponse batchUnBindCollocationGroupBySkuUuids(
      @ApiParam(required = true) @PathVariable("tenant") String tenantId,
      @ApiParam(required = true) @PathVariable("orgType") String orgType,
      @ApiParam(required = true) @PathVariable("orgId") String orgId,
      @ApiParam(required = true) @RequestBody RsMasRequest<List<String>> request);


  /**
   * 批量解绑ShopSKU口味组
   *
   * @param tenantId
   *     租户id
   * @param request
   *     根据ShopSkuIdKey集合批量解绑ShopSKU口味组
   */
  @ApiOperation(value = "批量解绑ShopSKU口味组")
  @RequestMapping(value = "/v2/{tenant}/service/shopSku/{orgType}/{orgId}/taste-group/batch-unbind", method = RequestMethod.DELETE)
  RsMasResponse batchUnbindTasteGroup(
      @ApiParam(required = true) @PathVariable("tenant") String tenantId,
      @ApiParam(required = true) @PathVariable("orgType") String orgType,
      @ApiParam(required = true) @PathVariable("orgId") String orgId,
      @ApiParam(required = true) @RequestBody RsMasRequest<List<RsShopSkuIdKey>> request);

  /**
   * 批量解绑ShopSku加料组
   *
   * @param tenantId
   *     租户id
   * @param request
   *     根据ShopSkuIdKey批量解绑ShopSku加料组
   */
  @ApiOperation(value = "批量解绑ShopSKU加料组")
  @RequestMapping(value = "/v2/{tenant}/service/shopSku/{orgType}/{orgId}/collocation-group/batch-unbind", method = RequestMethod.DELETE)
  RsMasResponse batchUnbindCollocationGroup(
      @ApiParam(required = true) @PathVariable("tenant") String tenantId,
      @ApiParam(required = true) @PathVariable("orgType") String orgType,
      @ApiParam(required = true) @PathVariable("orgId") String orgId,
      @ApiParam(required = true) @RequestBody RsMasRequest<List<RsShopSkuIdKey>> request);

  @ApiOperation(value = "门店类目复制", notes = "给当前租户下所有门店初始化门店类目")
  @RequestMapping(value = "/v2/{tenant}/service/platshopcategory/{org_type}/{org_id}/yc/copyInit", method = RequestMethod.POST)
  RsMasResponse platShopCategoryCopyInit(@PathVariable("tenant") String tenant,
      @ApiParam(required = true) @PathVariable("org_type") String orgType,
      @ApiParam(required = true) @PathVariable("org_id") String orgId,
      @RequestBody RsPlatShopCategoryCopyRequest platShopCategoryCopyRequest,
      @ApiParam(required = true, value = "操作人") @RequestParam("operator") String operator);

  @ApiOperation(value = "获取区域信息")
  @RequestMapping(value = "/v2/{tenant}/service/area/query", method = RequestMethod.POST)
  RsMasResponse<List<RsArea>> queryArea(@PathVariable("tenant") String tenant,
      @RequestBody RsAreaFilter areaFilter) throws Exception;
}
