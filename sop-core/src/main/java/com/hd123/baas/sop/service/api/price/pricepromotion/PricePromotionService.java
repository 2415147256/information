package com.hd123.baas.sop.service.api.price.pricepromotion;

import com.hd123.rumba.commons.biz.entity.OperateInfo;
import com.hd123.rumba.commons.biz.query.QueryDefinition;
import com.hd123.rumba.commons.biz.query.QueryResult;
import com.qianfan123.baas.common.BaasException;

import java.util.Collection;
import java.util.List;

/**
 * @author zhengzewang on 2020/11/13.
 */
public interface PricePromotionService {

  /**
   * 获取详情
   *
   * @param tenant 租户
   * @param uuid   单据id
   * @return 单据信息
   */
  PricePromotion get(String tenant, String uuid, String... fetchParts);

  /**
   * 分页查询
   *
   * @param tenant 租户
   * @param qd     查询条件
   * @return 查询结果
   */
  QueryResult<PricePromotion> query(String tenant, QueryDefinition qd, String... fetchPart);

  /**
   * 批量获取促销单
   *
   * @param tenant
   * @param uuids
   * @param fetchParts
   * @return
   */
  List<PricePromotion> list(String tenant, List<String> uuids, String... fetchParts);

  /**
   * 单据行分页查询
   *
   * @param tenant 租户
   * @param uuid   单据id
   * @param qd     查询条件
   * @return 查询结果
   */
  QueryResult<PricePromotionLine> queryLine(String tenant, String uuid, QueryDefinition qd);

  /**
   * 创建一个空的到店价促销单。状态为草稿
   *
   * @param tenant      租户
   * @param type
   * @param operateInfo 操作人信息
   * @return 单据
   */
  PricePromotion create(String tenant, String orgId, String type, OperateInfo operateInfo);

  /**
   * 保存。若状态为草稿，则变更为待审核
   *
   * @param tenant
   * @param promotion
   * @param operateInfo
   */
  void saveAndSubmit(String tenant, PricePromotion promotion, OperateInfo operateInfo) throws BaasException;

  /**
   * 添加商品
   *
   * @param tenant       租户
   * @param skuIds       商品ids
   * @param type         促销方式
   * @param rule         促销规则（公式或指定价格ø）
   * @param skuGroup     自定义类别ID
   * @param skuGroupName 自定义类别名称
   * @param operateInfo  操作人信息
   */
  void addLines(String tenant, String uuid, Collection<String> skuIds, PricePromotionLineType type, String rule,
                String skuGroup, String skuGroupName, OperateInfo operateInfo) throws BaasException;

  /**
   * 添加指定促销商品
   *
   * @param tenant
   * @param uuid
   * @param skuIds
   * @param rule
   * @param operateInfo
   */
  void addLines(String tenant, String uuid, Collection<String> skuIds, PricePromotionLineType type, String rule, OperateInfo operateInfo) throws BaasException;

  /**
   * 添加指定类别促销规则
   *
   * @param tenant
   * @param uuid
   * @param rules
   * @param operateInfo
   */
  void addLines(String tenant, String uuid, List<PricePromotionGroupRule> rules, OperateInfo operateInfo) throws BaasException;

  /**
   * 批量保存。根据商品来判重。有则更新，无责插入。不做商品的校验（导入用）
   *
   * @param tenant      租户
   * @param uuid        单据id
   * @param lines       行信息
   * @param operateInfo 操作人信息
   */
  void batchSaveLine(String tenant, String uuid, Collection<PricePromotionLine> lines, OperateInfo operateInfo)
      throws BaasException;

  /**
   * 删除商品
   *
   * @param tenant      租户
   * @param uuid        单据id
   * @param lineIds     单据行ids
   * @param operateInfo 操作人信息
   */
  void deleteLines(String tenant, String uuid, Collection<String> lineIds, OperateInfo operateInfo)
      throws BaasException;

  /**
   * 编辑行
   *
   * @param tenant      租户
   * @param uuid        单据id
   * @param lineId      行id
   * @param type        促销类型
   * @param rule        促销规则
   * @param operateInfo 操作人信息
   */
  void editLine(String tenant, String uuid, String lineId, PricePromotionLineType type, String rule,
                OperateInfo operateInfo) throws BaasException;

  /**
   * 审核单据。只有待审核状态可以审核
   * <p>
   * 若选择全部门店，则此时需要将当前所有门店全部添加到关联表
   * <p>
   * 需要校验至少选择一行商品。
   *
   * @param tenant      租户
   * @param orgId       组织id
   * @param uuid        单据id
   * @param operateInfo 操作人信息
   */
  void audit(String tenant, String orgId, String uuid, OperateInfo operateInfo) throws BaasException;

  /**
   * 取消/作废。待审核和已发布状态均可作废
   *
   * @param tenant      租户
   * @param uuid        单据id
   * @param reason      取消原因
   * @param operateInfo 操作人信息
   */
  void cancel(String tenant, String uuid, String reason, OperateInfo operateInfo) throws BaasException;

  /**
   * 发布
   *
   * @param tenant      租户
   * @param uuid        单据id
   * @param operateInfo 做人信息
   * @throws BaasException
   */
  void publish(String tenant, String uuid, OperateInfo operateInfo) throws BaasException;

  /**
   * 过期
   *
   * @param tenant      租户
   * @param uuid        单据id
   * @param operateInfo 做人信息
   * @throws BaasException
   */
  void expire(String tenant, String uuid, OperateInfo operateInfo) throws BaasException;

  /**
   * 终止
   *
   * @param tenant
   * @param uuid
   * @param operateInfo
   * @throws BaasException
   */
  void terminate(String tenant, String uuid, String reason, OperateInfo operateInfo) throws BaasException;

  /**
   * 关联门店
   *
   * @param tenant  租户
   * @param uuid    单据id
   * @param orgId   组织id
   * @param shopIds 门店id集合
   * @throws BaasException 业务异常
   */
  void relateShops(String tenant, String orgId, String uuid, List<String> shopIds) throws BaasException;

  /**
   * 查询关联门店
   *
   * @param tenant 租户
   * @param uuid   单据id
   * @return 关联门店列表
   */
  List<PricePromotionShop> listShops(String tenant, String uuid);

  /**
   * 查询和本单存在冲突的数据，如果没有查询到数据，则代表没有冲突
   */
  QueryResult<ConflictPromotionLine> conflictRemind(String tenant, PricePromotion promotion, int page, int size) throws BaasException;
}
