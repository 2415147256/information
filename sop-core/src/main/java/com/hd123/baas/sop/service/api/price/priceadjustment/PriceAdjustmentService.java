package com.hd123.baas.sop.service.api.price.priceadjustment;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.hd123.baas.sop.service.api.price.PriceAdjustmentLineEdit;
import com.hd123.rumba.commons.biz.entity.OperateInfo;
import com.hd123.rumba.commons.biz.query.QueryDefinition;
import com.hd123.rumba.commons.biz.query.QueryResult;
import com.qianfan123.baas.common.BaasException;

/**
 * @author zhengzewang on 2020/11/11.
 */
public interface PriceAdjustmentService {
  /**
   *
   */
  String ADJUSTMENT_LINE_PART = "adjustment_line_part";

  /**
   * 创建价格调整单（试算单）。
   * 
   * 创建的时候，不快照商品k/b/加价率，不快照定位，价格带等加价率等。（该数据等实际计算的时候再给）
   * 
   * 2. 先把所有的商品快照一份。包括商品类别和定位信息。（不包括k,b,加价率等信息）</br>
   * 
   * @param tenant
   *          租户
   * @return 价格调整单
   */
  PriceAdjustment create(String tenant, String orgId, OperateInfo operateInfo) throws BaasException;

  /**
   * 目标采购价修改。同时触发到店价更新 修改基础采购价
   * 
   * @param tenant
   *          租户
   * @param uuid
   *          单据id
   * @param lindId
   *          行id
   * @param operateInfo
   *          操作人信息
   */
  void modifyInPrice(String tenant, String uuid, String lindId, BigDecimal inPrice, BigDecimal initInPrice,
      OperateInfo operateInfo) throws BaasException;

  // TODO 修改到店价（分割品）

  /**
   * 到店价修改。仅分割品可修改
   * 
   * @param tenant
   *          租户
   * @param uuid
   *          单据id
   * @param lindId
   *          行id
   * @param basePrice
   *          到店价
   * @param operateInfo
   *          操作人
   */
  void modifyBasePrice(String tenant, String uuid, String lindId, BigDecimal basePrice, OperateInfo operateInfo)
      throws BaasException;

  void modifyLinePrice(String tenant, String uuid, String lindId, PriceAdjustmentLineEdit edit,
                       OperateInfo operateInfo) throws BaasException;

  /**
   * 更新行信息
   * 
   * @param tenant
   *          租户id
   * @param owner
   *          单据id
   * @param line
   *          行信息
   * @param operateInfo
   *          操作人信息
   * @throws BaasException
   *           异常
   */
  void update(String tenant, String owner, PriceAdjustmentLine line, OperateInfo operateInfo) throws BaasException;

  void batchModify(String tenant, String uuid, List<PriceAdjustmentLine> lines, OperateInfo operateInfo)
      throws BaasException;

  /**
   * 
   * 校验到店价。并支持修改开始生效日期
   * 
   * @param tenant
   *          租户
   * @param adjustment
   *          调整单
   * @param operateInfo
   *          操作人信息
   */
  void stepOne(String tenant, PriceAdjustment adjustment, OperateInfo operateInfo) throws BaasException;

  /**
   * 计算试算单的售价
   * 
   * @param tenant
   *          租户id
   * @param uuid
   *          试算单id
   * @param reload
   *          是否重新读取最新的配置
   * @param operateInfo
   *          操作人
   */
  void calSalePrice(String tenant, String uuid, Boolean reload, OperateInfo operateInfo) throws BaasException;

  /**
   * 修改加价规则
   *
   * @param tenant
   *          租户
   * @param uuid
   *          单据id
   * @param lineIds
   *          单据行ids
   * @param increaseRules
   *          加价规则
   * @param operateInfo
   *          操作人信息
   */
  void batchModifyRule(String tenant, String uuid, Collection<String> lineIds, PriceIncreaseType increaseType,
      List<PriceIncreaseRule> increaseRules, OperateInfo operateInfo) throws BaasException;

  /**
   * 开始试算。当前状态
   * 
   * @param tenant
   *          租户
   * @param uuid
   *          调整单
   * @param operateInfo
   *          操作人信息
   */
  void stepTwo(String tenant, String uuid, OperateInfo operateInfo) throws BaasException, IllegalAccessException;

  /**
   * 传入目标采购价、后台加价率，价格带加价率试算售价，到店价，不持久化，将计算结果返回。返回结果包括所有受影响的行。返回结果至少包含当前行
   *
   * @param tenant
   *          租户id
   * @param lineId
   *          行id
   * @param tryCalSalePrice
   *          参数
   * @return 返回计算结果，可能返回多个行，因为可能其他行的数据也会发生改变
   */
  List<PriceAdjustmentLine> tryCal(String tenant, String lineId, TryCalSalePrice tryCalSalePrice) throws BaasException;

  /**
   * 分页查询
   * 
   * @param tenant
   *          租户
   * @param qd
   *          查询条件
   * @return 价格调整单数据
   */
  QueryResult<PriceAdjustment> query(String tenant, QueryDefinition qd, String... fetchParts);

  /**
   * 查询详情
   * 
   * @param tenant
   *          租户
   * @param uuid
   *          uuid
   * @param fetchParts
   *          fetchParts
   * @return 价格调整单
   */
  PriceAdjustment get(String tenant, String uuid, String... fetchParts);

  /**
   * 试算单行分页查询
   * 
   * @param tenant
   *          租户
   * @param uuid
   *          uuid
   * @param qd
   *          查询条件
   * @return 价格调整单行
   */
  QueryResult<PriceAdjustmentLine> queryLine(String tenant, String uuid, QueryDefinition qd);

  /**
   * 根据行id查询试算单行
   * 
   * @param tenant
   *          租户id
   * @param lineId
   *          行id
   * @return 行信息
   */
  PriceAdjustmentLine getLine(String tenant, String lineId);

  /**
   * 试算单行分页查询
   *
   * @param tenant
   *          租户
   * @param uuid
   *          uuid
   */
  List<PriceAdjustmentLine> listBySkuGid(String tenant, String uuid, String skuGid);

  /**
   * 查询竞品信息
   * 
   * @param tenant
   *          租户
   * @param owner
   *          试算单ID
   * @return
   */
  List<PriceCompetitorLine> competitorLineList(String tenant, String owner);

  /**
   * 修改竞品行忽略状态
   *
   * @param tenant
   *          租户id
   * @param owner
   *          试算单id
   * @param skuIgnoreMap
   *          key为商品id，value为忽略状态值
   */
  void changeCompetitorIgnore(String tenant, String owner, Map<String, Boolean> skuIgnoreMap);

  /**
   * 审核单据
   * 
   * @param tenant
   *          租户
   * @param uuid
   *          单据id
   * @param operateInfo
   *          操作人信息
   */
  void audit(String tenant, String uuid, OperateInfo operateInfo) throws BaasException;

  /**
   * 取消/作废单据
   * 
   * @param tenant
   *          租户
   * @param uuid
   *          单据id
   * @param reason
   *          取消原因
   * @param operateInfo
   */
  void cancel(String tenant, String uuid, String reason, OperateInfo operateInfo) throws BaasException;

  /**
   * 发布
   *
   * @param tenant
   *          租户
   * @param uuid
   *          单据id
   * @param operateInfo
   *          做人信息
   * @throws BaasException
   */
  void publish(String tenant, String uuid, OperateInfo operateInfo) throws BaasException;

  /**
   * 过期
   * 
   * @param tenant
   *          租户
   * @param uuid
   *          单据id
   * @param operateInfo
   *          操作人信息
   * @throws BaasException
   *           异常
   */
  void expire(String tenant, String uuid, OperateInfo operateInfo) throws BaasException;

  /**
   * 获取指定日期之前最近生效的试算单
   * @param tenant
   * @param effectiveStartDate
   * @return
   */
  PriceAdjustment getLastEffective(String tenant, String orgId, Date effectiveStartDate, String... fetchParts);

  /**
   * 获取指定日期 生效的试算单
   */
  PriceAdjustment getEffective(String tenant,String orgId, Date effectiveStartDate, String... fetchParts);

}
